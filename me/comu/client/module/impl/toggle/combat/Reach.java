package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.utils.AStarCustomPathFinder;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.Vec3;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public final class Reach extends ToggleableModule {

    private final NumberProperty<Double> range = new NumberProperty<>(4.73D, 3D, 6D, 0.1D, "Range", "reach", "re", "r");
    private final EnumProperty<Mode> mode = new EnumProperty<Mode>(Mode.VANILLA,"Mode", "m");
    private ArrayList<Vec3> path;
    private List<Vec3>[] test;
    private List<EntityLivingBase> targets;
    private Stopwatch cps;
    private double dashDistance;

    public Reach() {
        super("Reach", new String[]{"reach","re", "range"}, 0xFF64A0, ModuleType.COMBAT);
        this.offerProperties(range, mode);
        this.dashDistance = 5.0;
        this.path = new ArrayList<Vec3>();
        this.test = (List<Vec3>[]) new ArrayList[50];
        this.targets = new CopyOnWriteArrayList<EntityLivingBase>();
        this.cps = new Stopwatch();
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_armor_tick_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                    if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                        if (mode.getValue() == Mode.CALCULATED) {
                        targets = getTargets();
                        final int maxtTargets = 5;
                        if (cps.hasCompleted(500) && targets.size() > 0) {
                            for (int i = 0; i < ((targets.size() > maxtTargets) ? maxtTargets : targets.size()); ++i) {
                                final EntityLivingBase T = targets.get(i);
                                final Vec3 topFrom = new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
                                final Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                                path = computePath(topFrom, to);
                                test[i] = path;
                                for (final Vec3 pathElm : path) {
                                    minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                                }
                                Collections.reverse(path);
                                for (final Vec3 pathElm : path) {
                                    minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                                }
                            }
                            cps.reset();
                        }
                    } else if (mode.getValue() == Mode.AURA)
                        {
                            EntityPlayer entity = minecraft.theWorld.getClosestPlayerToEntity(minecraft.thePlayer, range.getValue());
                            if (entity == minecraft.thePlayer)
                                return;
                            final double x = entity.posX - ClientUtils.player().posX;
                            final double z = entity.posZ - ClientUtils.player().posZ;
                            final double h = ClientUtils.player().posY + ClientUtils.player().getEyeHeight() - (entity.posY + entity.getEyeHeight());
                            final double h2 = Math.sqrt(x * x + z * z);
                            if (x < range.getValue() && z < range.getValue())
                            {
                                minecraft.func_175102_a().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                                minecraft.thePlayer.swingItem();
                                minecraft.playerController.attackEntity(minecraft.thePlayer, entity);
                            }

                        }
                }
            }

            private List<EntityLivingBase> getTargets() {
                final List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
                for (final Object o3 : minecraft.theWorld.getLoadedEntityList()) {
                    if (o3 instanceof EntityLivingBase) {
                        final EntityLivingBase entity = (EntityLivingBase) o3;
                        if (!validEntity(entity)) {
                            continue;
                        }
                        targets.add(entity);
                    }
                }
                targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(minecraft.thePlayer) * 1000.0f - o2.getDistanceToEntity(minecraft.thePlayer) * 1000.0f));
                return targets;
            }

            boolean validEntity(final EntityLivingBase entity) {
                if (minecraft.thePlayer.isEntityAlive() && !(entity instanceof EntityPlayerSP) && minecraft.thePlayer.getDistanceToEntity(entity) <= range.getValue()) {

                    if (entity.isPlayerSleeping()) {
                        return false;
                    }
                    if (Gun.getInstance().getFriendManager().isFriend(entity.getName())) {
                        return false;
                    }
                    if (entity instanceof EntityPlayer) {
                        final EntityPlayer player = (EntityPlayer) entity;
                        return (player.isEntityAlive() || player.getHealth() != 0.0)   && !Gun.getInstance().getFriendManager().isFriend(player.getName());

                    } else if (!entity.isEntityAlive()) {
                        return false;
                    }
                    if (entity instanceof EntityMob) {
                        return false;
                    }
                    if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
                        return !entity.getName().equals("Villager");
                    }
                }
                return false;
            }

        });
    }

    public static boolean isTeam(final EntityPlayer e, final EntityPlayer e2) {
        if (e2.getTeam() != null && e.getTeam() != null) {
            final Character target = e2.getDisplayName().getFormattedText().charAt(1);
            final Character player = e.getDisplayName().getFormattedText().charAt(1);
            return target.equals(player);
        }
        return true;
    }
    private boolean canPassThrow(final BlockPos pos) {
        final Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }
    private ArrayList<Vec3> computePath(Vec3 topFrom, final Vec3 to) {
        if (!this.canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0.0, 1.0, 0.0);
        }
        final AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        final ArrayList<Vec3> path = new ArrayList<Vec3>();
        final ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (final Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0.0, 0.5));
                lastDashLoc = pathElm;
            }
            else {
                boolean canContinue = true;
                Label_0356: {
                    if (pathElm.squareDistanceTo(lastDashLoc) > this.dashDistance * this.dashDistance) {
                        canContinue = false;
                    }
                    else {
                        final double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                        final double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                        final double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                        final double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                        final double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                        final double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                        for (int x = (int)smallX; x <= bigX; ++x) {
                            for (int y = (int)smallY; y <= bigY; ++y) {
                                for (int z = (int)smallZ; z <= bigZ; ++z) {
                                    if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                        canContinue = false;
                                        break Label_0356;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            ++i;
        }
        return path;
    }

    public enum Mode {
        VANILLA, CALCULATED, AURA
    }
}
