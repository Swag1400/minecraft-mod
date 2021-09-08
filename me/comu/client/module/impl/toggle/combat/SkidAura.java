package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.module.impl.toggle.movement.Speed;
import me.comu.client.notification.Notification;
import me.comu.client.notification.NotificationManager;
import me.comu.client.notification.NotificationType;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SkidAura extends ToggleableModule {

    public static final EnumProperty<Type> type;
    private static final EnumProperty<Rotations> rotations;
    private static final EnumProperty<Priority> priority;
    private static final Property<Boolean> autoblock;
    private final NumberProperty<Float> range;
    private final NumberProperty<Float> blockRange;
    private static final Property<Boolean> players;
    private static final Property<Boolean> animals;
    private static final Property<Boolean> teams;
    private final NumberProperty<Long> hitChance;
    private static final Property<Boolean> walls;
    private static final Property<Boolean> invisibles;
    private final NumberProperty<Long> maxAPS;
    private final NumberProperty<Long> minAPS;
    private static final EnumProperty<AutoBlockMode> autoBlockMode;
    private static final EnumProperty<Mode> mode;
    private static final Property<Boolean> death;
    private static final Property<Boolean> interact;
    public static EntityLivingBase target;
    public static EntityLivingBase vip;
    public static EntityLivingBase blockTarget;
    private Stopwatch switchTimer;
    public static float sYaw;
    public static float sPitch;
    public static float aacB;
    private double fall;
    int[] randoms;
    private boolean isBlocking;
    public boolean isSetup;
    private Stopwatch newTarget;
    private Stopwatch lastStep;
    private Stopwatch rtimer;
    private List<EntityLivingBase> loaded;
    private int index;
    private int timer;
    private int crits;
    private int groundTicks;


    static {
        autoblock = new Property<Boolean>(true, "Auto-Block", "b", "ab", "block", "autoblock");
        players = new Property<Boolean>(true, "Players", "player", "p", "pl");
        animals = new Property<Boolean>(true, "Others", "animal", "an", "monster", "monsters", "mon", "other");
        invisibles = new Property<Boolean>(true, "Invisibles", "invisible", "invis", "inv");
        interact = new Property<Boolean>(true, "Interact");
        death = new Property<Boolean>(true, "Death");
        walls = new Property<Boolean>(true, "Walls", "wall");
        teams = new Property<Boolean>(true, "Teams");
        autoBlockMode = new EnumProperty<AutoBlockMode>(AutoBlockMode.NCP, "Block-Mode", "autoblockmode", "abm", "bm", "blockmode");
        mode = new EnumProperty<Mode>(Mode.Single, "Mode", "Targeting", "target", "t");
        type = new EnumProperty<Type>(Type.PRE, "Type", "motionmode", "motiontype");
        rotations = new EnumProperty<Rotations>(Rotations.Smooth, "Rotations", "rotationtype", "rotationmode");
        priority = new EnumProperty<Priority>(Priority.Angle, "Priority");


    }

    public SkidAura() {
        super("Aura", new String[]{"skidaura", "saura", "skiddedaura"}, 0xF75FFF, ModuleType.COMBAT);
        switchTimer = new Stopwatch();
        randoms = new int[]{0, 1, 0};
        isBlocking = false;
        newTarget = new Stopwatch();
        lastStep = new Stopwatch();
        rtimer = new Stopwatch();
        loaded = new CopyOnWriteArrayList<>();
        minAPS = new NumberProperty<Long>(10L, 1L, 20L, 1L, "Min-APS", "minaps", "max");
        maxAPS = new NumberProperty<Long>(10L, 1L, 20L, 1L, "Max-APS", "maxaps", "min");
        range = new NumberProperty<Float>(5f, 3.0f, 6.0f, 0.1F, "Reach", "range", "r");
        hitChance = new NumberProperty<Long>(100L, 0L, 100L, 5L, "Hit-Chance", "hitchance", "chance");
        blockRange = new NumberProperty<Float>(5f, 3.0f, 6.0f, 0.1F, "Block-Range", "brange", "blockrange", "breach", "blockreach");
        offerProperties(minAPS, maxAPS, range, hitChance, blockRange, autoblock, players, animals, invisibles, interact, walls, teams, autoBlockMode, mode, type, rotations, priority);
        listeners.add(new Listener<MotionUpdateEvent>("kill_aura_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
                if (sf.getValue()) {
                    setTag("Aura \2477" + mode.getFixedValue());
                }
                final int min = minAPS.getValue().intValue();
                final int max = maxAPS.getValue().intValue();
                final double hitchance = hitChance.getDoubleValue();
                final int cps = ClientUtils.randomNumber(min, max);
                final double reach = range.getValue();
                final double blockReach = blockRange.getValue();
                final boolean autoblockValue = autoblock.getValue();
                final Criticals critsMod = (Criticals) Gun.getInstance().getModuleManager().getModuleByAlias("criticals");
                final EnumProperty<Criticals.Mode> critsMode = (EnumProperty) critsMod.getPropertyByAlias("mode");
                final boolean minicrit = critsMod.isRunning() && (critsMode.getValue().equals(Criticals.Mode.MINI));
                boolean shouldMiss = ClientUtils.randomNumber(0, 100) > hitchance;
                EntityLivingBase newT = getOptimalTarget(range.getValue());
                if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                    ++timer;
                    if (death.getValue()) {
                        if (!minecraft.thePlayer.isEntityAlive() || (minecraft.currentScreen != null && minecraft.currentScreen instanceof GuiGameOver)) {
                            toggle();
                            NotificationManager.notify(new Notification(NotificationType.INFO, "Aura Death", "Aura disabled due to death.", 5));
                            return;
                        }
                        if (minecraft.thePlayer.ticksExisted <= 1) {
                            toggle();
                            NotificationManager.notify(new Notification(NotificationType.INFO, "Aura Death", "Aura disabled due to death.", 5));
                            return;
                        }
                    }

                    blockTarget = getOptimalTarget(blockRange.getValue());
                    if (mode.getValue().equals(Mode.Multi)) {
                        loaded = getTargets(range.getValue());
                        if (loaded.size() > 0) {
                            target = loaded.get(0);
                            final float[] rot = RotationUtils.getRotations(target);
                            event.setRotationYaw(rot[0]);
                            event.setRotationPitch(rot[1]);
                            sYaw = rot[0];
                            sPitch = rot[1];
                            if (autoblock.getValue()) {
                                if (hasSword()) {
                                    minecraft.thePlayer.itemInUseCount = 999;
                                } else if (minecraft.thePlayer.itemInUseCount == 999) {
                                    minecraft.thePlayer.itemInUseCount = 0;
                                }
                            } else if (minecraft.thePlayer.itemInUseCount == 999) {
                                if (isBlocking && hasSword()) {
                                    unBlock();
                                }
                                minecraft.thePlayer.itemInUseCount = 0;
                            }
                            if (minicrit) {
                                miniCrit(event,critsMode.getValue().equals(Criticals.Mode.PACKET), critsMode.getValue().equals(Criticals.Mode.MINI));
                            }
                            if (timer >= 20 / cps) {
                                timer = 0;
                                AutoHeal2 autoHeal2 = (AutoHeal2) Gun.getInstance().getModuleManager().getModuleByAlias("autopot2");
                                if (autoHeal2.isPotting) {
                                    shouldMiss = true;
                                    minecraft.thePlayer.swingItem();
                                }
                                if (isBlocking && hasSword()) {
                                    unBlock();
                                }
                                if (loaded.size() >= 1 && !shouldMiss) {
                                    minecraft.playerController.syncCurrentPlayItem();
                                    minecraft.thePlayer.swingItem();
                                }
                                for (final EntityLivingBase targ : loaded) {
                                    if (!shouldMiss) {
                                        minecraft.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(targ, C02PacketUseEntity.Action.ATTACK));
                                        minecraft.thePlayer.attackTargetEntityWithCurrentItem(targ);
                                    }
                                }
                                if (autoblock.getValue() && autoBlockMode.getValue().equals(AutoBlockMode.Basic1) && hasSword()) {
                                    block(target);
                                }
                            } else if (autoblock.getValue() && (autoBlockMode.getValue().equals(AutoBlockMode.NCP) || autoBlockMode.getValue().equals(AutoBlockMode.Hypixel)) && hasSword() && isBlocking) {
                                unBlock();
                            }
                        }
                    } else {
                        if (mode.getValue().equals(Mode.Switch)) {
                            final float cooldown = rotations.getValue().equals(Rotations.Smooth) ? 500.0f : 400.0f;
                            if (switchTimer.hasCompleted((long) cooldown)) {
                                loaded = getTargets(range.getValue());
                            }
                            if (index >= loaded.size()) {
                                index = 0;
                            }
                            if (switchTimer.hasCompleted((long) cooldown) && loaded.size() > 0) {
                                ++index;
                                if (index >= loaded.size()) {
                                    index = 0;
                                }
                                switchTimer.reset();
                            }
                            if (loaded.size() > 0) {
                                newT = loaded.get(index);
                            } else {
                                newT = null;
                            }
                        } else if (mode.getValue().equals(Mode.Multi2)) {
                            loaded = getTargets(range.getValue());
                            if (index >= loaded.size()) {
                                index = 0;
                            }
                            if (timer >= 20 / cps && loaded.size() > 0) {
                                ++index;
                                if (index >= loaded.size()) {
                                    index = 0;
                                }
                            }
                            if (loaded.size() > 0) {
                                newT = loaded.get(index);
                            } else {
                                newT = null;
                            }
                        }
                        if (target != newT) {
                            newTarget.reset();
                            if (!mode.getValue().equals(Mode.Multi2)) {
                                shouldMiss = true;
                            }
                            target = newT;
                            if (target == null) {
                                sYaw = minecraft.thePlayer.rotationYaw;
                                sPitch = minecraft.thePlayer.rotationPitch;
                            }
                        }
                        if (target != null) {
                            if ((!validEntity(target, range.getValue()) || !minecraft.theWorld.loadedEntityList.contains(target)) && mode.getValue().equals(Mode.Switch)) {
                                loaded = getTargets(range.getValue());
                                ++index;
                                if (index >= loaded.size()) {
                                    index = 0;
                                }
                                return;
                            }
                            if (!validEntity(target, range.getValue()) && mode.getValue().equals(Mode.Multi2)) {
                                loaded = getTargets(range.getValue());
                                return;
                            }
                            if (minicrit) {
                                miniCrit(event,critsMode.getValue().equals(Criticals.Mode.PACKET), critsMode.getValue().equals(Criticals.Mode.MINI));
                            }
                            float[] rot = RotationUtils.getRotations(target);
                            switch (rotations.getValue()) {
                                case Basic: {
                                    event.setRotationYaw(rot[0]);
                                    event.setRotationYaw(rot[1]);
                                    sYaw = rot[0];
                                    sPitch = rot[1];
                                    break;
                                }
                                case Smooth: {
                                    smoothAim(event);
                                    break;
                                }
                                case Legit: {
                                    aacB /= 2.0f;
                                    customRots(event, target);
                                    break;
                                }
                                case Predict: {
                                    rot = RotationUtils.getPredictedRotations(target);
                                    event.setRotationYaw(rot[0]);
                                    event.setRotationPitch(rot[1]);
                                    sYaw = rot[0];
                                    sPitch = rot[1];
                                    break;
                                }
                            }
                            if (timer >= 20 / cps && type.getValue().equals(Type.PRE)) {
                                timer = 0;
                                final int XR = ClientUtils.randomNumber(1, -1);
                                final int YR = ClientUtils.randomNumber(1, -1);
                                final int ZR = ClientUtils.randomNumber(1, -1);
                                randoms[0] = XR;
                                randoms[1] = YR;
                                randoms[2] = ZR;
                                float neededYaw = RotationUtils.getYawChange(sYaw, target.posX, target.posZ);
                                if (rotations.getValue().equals(Rotations.Legit)) {
                                    neededYaw = getCustomRotsChange(sYaw, sPitch, target.posX, target.posY, target.posZ)[0];
                                }
                                float interval = 60.0f - minecraft.thePlayer.getDistanceToEntity(target) * 10.0f;
                                if (rotations.getValue().equals(Rotations.Legit)) {
                                    interval = 50.0f - minecraft.thePlayer.getDistanceToEntity(target) * 10.0f;
                                }
                                AutoHeal2 autoHeal2 = (AutoHeal2) Gun.getInstance().getModuleManager().getModuleByAlias("autopot2");
                                if (neededYaw > interval || neededYaw < -interval || !newTarget.hasCompleted(70) || autoHeal2.isPotting) {
                                    shouldMiss = true;
                                }
                                if (!shouldMiss || mode.getValue().equals(Mode.Multi2)) {
                                    hitEntity(target, autoblock.getValue());
                                } else {
                                    minecraft.thePlayer.swingItem();
                                }
                            }
                        }
                    }
                    if (blockTarget != null) {
                        if (autoblock.getValue()) {
                            if (hasSword()) {
                                if (autoBlockMode.getValue().equals(AutoBlockMode.NCP) || autoBlockMode.getValue().equals(AutoBlockMode.Hypixel)) {
                                    if (hasSword() && isBlocking) {
                                        unBlock();
                                    }
                                } else if (minecraft.thePlayer.itemInUseCount == 0) {
                                    block(blockTarget);
                                }
                                minecraft.thePlayer.itemInUseCount = 999;
                            } else if (minecraft.thePlayer.itemInUseCount == 999) {
                                minecraft.thePlayer.itemInUseCount = 0;
                            }
                        } else if (minecraft.thePlayer.itemInUseCount == 999) {
                            if (isBlocking && hasSword()) {
                                unBlock();
                            }
                            minecraft.thePlayer.itemInUseCount = 0;
                        }
                    } else if (minecraft.thePlayer.itemInUseCount == 999) {
                        if (isBlocking && hasSword()) {
                            unBlock();
                        }
                        minecraft.thePlayer.itemInUseCount = 0;
                    }
                } else {
                    if (type.getValue().equals(Type.POST) && target != null) {
                        timer = 0;
                        final int XR2 = ClientUtils.randomNumber(1, -1);
                        final int YR2 = ClientUtils.randomNumber(1, -1);
                        final int ZR2 = ClientUtils.randomNumber(1, -1);
                        randoms[0] = XR2;
                        randoms[1] = YR2;
                        randoms[2] = ZR2;
                        float neededYaw2 = RotationUtils.getYawChange(sYaw, target.posX, target.posZ);
                        if (rotations.getValue().equals(SkidAura.Rotations.Legit)) {
                            neededYaw2 = getCustomRotsChange(sYaw, sPitch, target.posX, target.posY, target.posZ)[0];
                        }
                        float interval2 = 60.0f - minecraft.thePlayer.getDistanceToEntity(target) * 10.0f;
                        if (rotations.getValue().equals(Rotations.Legit)) {
                            interval2 = 50.0f - minecraft.thePlayer.getDistanceToEntity(target) * 10.0f;
                        }
                        AutoHeal2 autoHeal2 = (AutoHeal2) Gun.getInstance().getModuleManager().getModuleByAlias("autopot2");
                        if (neededYaw2 > interval2 || neededYaw2 < -interval2 || !newTarget.hasCompleted(70) || autoHeal2.isPotting) {
                            shouldMiss = true;
                        }
                        if (!shouldMiss || mode.getValue().equals(Mode.Multi2)) {
                            hitEntity(target, autoblock.getValue());
                        } else {
                            minecraft.thePlayer.swingItem();
                        }
                    }
                    if (blockTarget != null && !isBlocking && autoblock.getValue() && hasSword()) {
                        if (autoBlockMode.getValue().equals(AutoBlockMode.Hypixel)) {
                            blockHypixel(blockTarget);
                        } else if (autoBlockMode.getValue().equals(AutoBlockMode.NCP)) {
                            block(blockTarget);
                        } else if (autoBlockMode.getValue().equals(AutoBlockMode.Basic2) && timer == 0) {
                            block(blockTarget);
                        }
                    }
                }


            }
        });
        listeners.add(new Listener<RenderEvent>("") {
            @Override
            public void call(RenderEvent event) {
                if (target != null) {
                    final int color = new Color(255, 70, 70).getRGB();
                    if (mode.getValue().equals(Mode.Multi)) {
                        for (final EntityLivingBase ent : loaded) {
                            drawESP(ent, color);
                        }
                    } else {
                        drawESP(target, color);
                    }
                }
            }
        });
    }

    private EntityLivingBase getOptimalTarget(final double range) {
        final List<EntityLivingBase> load = new ArrayList<EntityLivingBase>();
        for (final Object o : minecraft.theWorld.loadedEntityList) {
            if (o instanceof EntityLivingBase) {
                final EntityLivingBase ent = (EntityLivingBase) o;
                if (!validEntity(ent, range)) {
                    continue;
                }
                if (ent == vip) {
                    return ent;
                }
                load.add(ent);
            }
        }
        if (load.isEmpty()) {
            return null;
        }
        return getTarget(load);
    }

    private EntityLivingBase getTarget(final List<EntityLivingBase> list) {
        sortList(list);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public float[] getCustomRotsChange(final float yaw, final float pitch, double x, double y, double z) {
        double xDiff = x - minecraft.thePlayer.posX;
        double zDiff = z - minecraft.thePlayer.posZ;
        double yDiff = y - minecraft.thePlayer.posY;
        final double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        double mult = 1.0 / (dist + 1.0E-4) * 2.0;
        if (mult > 0.2) {
            mult = 0.2;
        }
        if (!minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(minecraft.thePlayer, minecraft.thePlayer.boundingBox).contains(target)) {
            x += 0.3 * this.randoms[0];
            y -= 0.4 + mult * this.randoms[1];
            z += 0.3 * this.randoms[2];
        }
        xDiff = x - minecraft.thePlayer.posX;
        zDiff = z - minecraft.thePlayer.posZ;
        yDiff = y - minecraft.thePlayer.posY;
        final float yawToEntity = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitchToEntity = (float) (-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[]{MathHelper.wrapAngleTo180_float(-(yaw - yawToEntity)), -MathHelper.wrapAngleTo180_float(pitch - pitchToEntity) - 2.5f};
    }

    public void customRots(final MotionUpdateEvent em, final EntityLivingBase ent) {
        final double randomYaw = 0.05;
        final double randomPitch = 0.05;
        final float[] rotsN = getCustomRotsChange(sYaw, sPitch, target.posX + ClientUtils.randomNumber(1, -1) * randomYaw, target.posY + ClientUtils.randomNumber(1, -1) * randomPitch, target.posZ + ClientUtils.randomNumber(1, -1) * randomYaw);
        final float targetYaw = rotsN[0];
        float yawFactor = targetYaw * targetYaw / (4.7f * targetYaw);
        if (targetYaw < 5.0f) {
            yawFactor = targetYaw * targetYaw / (3.7f * targetYaw);
        }
        if (Math.abs(yawFactor) > 7.0f) {
            aacB = yawFactor * 7.0f;
            yawFactor = targetYaw * targetYaw / (3.7f * targetYaw);
        } else {
            yawFactor = targetYaw * targetYaw / (6.7f * targetYaw) + aacB;
        }
        em.setRotationYaw(sYaw + yawFactor);
        sYaw += yawFactor;
        final float targetPitch = rotsN[1];
        final float pitchFactor = targetPitch / 3.7f;
        em.setRotationPitch(sPitch + pitchFactor);
        sPitch += pitchFactor;
    }

    private void sortList(final List<EntityLivingBase> weed) {
        switch (priority.getValue()) {
            case Range: {
                weed.sort((o1, o2) -> (int) (o1.getDistanceToEntity(minecraft.thePlayer) * 1000.0f - o2.getDistanceToEntity(minecraft.thePlayer) * 1000.0f));
                break;
            }
            case FOV: {
                weed.sort(Comparator.comparingDouble(o -> RotationUtils.getDistanceBetweenAngles(minecraft.thePlayer.rotationPitch, RotationUtils.getRotations(o)[0])));
                break;
            }
            case Angle: {

                weed.sort((o1, o2) -> {
                    float[] rot1;
                    float[] rot2;
                    rot1 = RotationUtils.getRotations(o1);
                    rot2 = RotationUtils.getRotations(o2);
                    return (int) (minecraft.thePlayer.rotationYaw - rot1[0] - (minecraft.thePlayer.rotationYaw - rot2[0]));
                });
                break;
            }
            case Health: {
                weed.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
                break;
            }
            case Armor: {
                weed.sort(Comparator.comparingInt(o -> (o instanceof EntityPlayer) ? ((EntityPlayer) o).inventory.getTotalArmorValue() : ((int) o.getHealth())));
                break;
            }
        }
    }

    boolean validEntity(final EntityLivingBase entity, final double range) {
        if (minecraft.thePlayer.isEntityAlive() && !(entity instanceof EntityPlayerSP) && minecraft.thePlayer.getDistanceToEntity(entity) <= range) {
            if (!RotationUtils.canEntityBeSeen(entity) && !walls.getValue()) {
                return false;
            }

            if (entity instanceof EntityPlayer) {
                if (players.getValue()) {
                    final EntityPlayer player = (EntityPlayer) entity;
                    return (player.isEntityAlive() || player.getHealth() != 0.0);
                }
            } else if (!entity.isEntityAlive()) {
                return false;
            }
            if (animals.getValue() && (entity instanceof EntityMob || entity instanceof EntityIronGolem || entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
                return !entity.getName().equals("Villager") || !(entity instanceof EntityVillager);
            }
        }
        return false;
    }

    private void hitEntity(final EntityLivingBase ent, final boolean shouldBlock) {
        if (this.isBlocking && this.hasSword()) {
            this.unBlock();
        }
        final C0BPacketEntityAction act = new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING);
        minecraft.thePlayer.swingItem();
        minecraft.playerController.attackEntity(minecraft.thePlayer, target);
        if (shouldBlock && autoBlockMode.getValue().equals(AutoBlockMode.Basic1) && this.hasSword()) {
            this.block(ent);
        }
    }

    private List<EntityLivingBase> getTargets(final double range) {
        final List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
        for (final Object o : minecraft.theWorld.loadedEntityList) {
            if (o instanceof EntityLivingBase) {
                final EntityLivingBase entity = (EntityLivingBase) o;
                if (!this.validEntity(entity, range)) {
                    continue;
                }
                targets.add(entity);
            }
        }
        this.sortList(targets);
        return targets;
    }

    public void miniCrit(final MotionUpdateEvent em, boolean crits, boolean critsMini) {
        double offset = 0.0;
        boolean ground = false;
        final int min = minAPS.getValue().intValue();
        final int max = maxAPS.getValue().intValue();
        final double hitchance = hitChance.getValue().doubleValue();
        final int cps = ClientUtils.randomNumber(min, max);
        final int delay = 20 / cps;
        if (critsMini) {
            if (this.crits == 0) {
                final double x = minecraft.thePlayer.posX;
                final double y = minecraft.thePlayer.posY;
                final double z = minecraft.thePlayer.posZ;
                final C03PacketPlayer.C04PacketPlayerPosition p = new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0626001, z, false);
                offset = 0.0;
                this.crits = 1;
            } else if ((this.timer == delay - 2 || (delay - 2 <= 0 && this.timer <= delay - 2) || this.timer >= delay) && this.crits >= 1 && this.lastStep.hasCompleted(20)) {
                this.crits = 0;
                offset = 0.0628;
                this.fall += offset;
            }
        } else if (crits) {
            if (!this.lastStep.hasCompleted(20) || this.groundTicks <= 0) {
                this.crits = -1;
                ground = true;
            }
            ++this.crits;
            if (this.crits == 1) {
                offset = 0.0625;
            } else if (this.crits == 2) {
                offset = 0.0626;
            } else if (this.crits == 3) {
                offset = 0.0;
            } else if (this.crits == 4) {
                offset = 0.0;
                this.crits = 0;
            }
        }
        final boolean aa = ClientUtils.isOnGround(0.001);
        if (!aa) {
            this.groundTicks = 0;
            this.crits = 0;
            this.fall = 0.0;
        } else {
            ++this.groundTicks;
        }
        if (minecraft.thePlayer.isCollidedVertically && aa && !minecraft.thePlayer.isJumping && !minecraft.thePlayer.isInWater() && !minecraft.gameSettings.keyBindJump.getIsKeyPressed()) {
            Speed speed = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
            if (speed.isRunning()) {
                return;
            }
        }
        if (minecraft.thePlayer.motionY != -0.1552320045166016) {
            if (offset != 0.0) {
                isSetup = true;
            } else {
                isSetup = false;
            }
            minecraft.thePlayer.lastReportedPosY = 0.0;
            em.setPositionY(minecraft.thePlayer.posY + offset);
            em.setOnGround(ground);
        }

    }

    public void drawESP(final Entity entity, final int color) {
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * minecraft.timer.renderPartialTicks;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * minecraft.timer.renderPartialTicks + entity.getEyeHeight() * 1.2;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * minecraft.timer.renderPartialTicks;
        final double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX) + 0.2;
        final double height = 0.1;
        final Vec3 vec = new Vec3(x - width / 2.0, y, z - width / 2.0);
        final Vec3 vec2 = new Vec3(x + width / 2.0, y + height, z + width / 2.0);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
        minecraft.entityRenderer.setupCameraTransform(minecraft.timer.renderPartialTicks, 2);
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        ClientUtils.drawBoundingBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void smoothAim(final MotionUpdateEvent em) {
        final double randomYaw = 0.05;
        final double randomPitch = 0.05;
        final float targetYaw = RotationUtils.getYawChange(sYaw, target.posX + ClientUtils.randomNumber(1, -1) * randomYaw, target.posZ + ClientUtils.randomNumber(1, -1) * randomYaw);
        final float yawFactor = targetYaw / 1.7f;
        em.setRotationYaw(sYaw + yawFactor);
        sYaw += yawFactor;
        final float targetPitch = RotationUtils.getPitchChange(sPitch, target, target.posY + ClientUtils.randomNumber(1, -1) * randomPitch);
        final float pitchFactor = targetPitch / 1.7f;
        em.setRotationPitch(sPitch + pitchFactor);
        sPitch += pitchFactor;
    }

    private void block(final EntityLivingBase ent) {
        this.isBlocking = true;
        if (interact.getValue()) {
            minecraft.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, new Vec3(ClientUtils.randomNumber(-50, 50) / 100.0, ClientUtils.randomNumber(0, 200) / 100.0, ClientUtils.randomNumber(-50, 50) / 100.0)));
            minecraft.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, C02PacketUseEntity.Action.INTERACT));
        }
        minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
    }

    @Override
    protected void onEnable() {
        if (minecraft.thePlayer != null) {
            sYaw = minecraft.thePlayer.rotationYaw;
            sPitch = minecraft.thePlayer.rotationPitch;
            loaded.clear();
            isBlocking = minecraft.thePlayer.isBlocking();
        }
        newTarget.reset();
        timer = 20;
        groundTicks = (PlayerHelper.isOnGround(0.01) ? 1 : 0);
        aacB = 0.0f;
    }

    @Override
    protected void onDisable() {
        loaded.clear();
        if (minecraft.thePlayer == null) {
            return;
        }
        if (isBlocking && hasSword() && minecraft.thePlayer.getItemInUseCount() == 999) {
            unBlock();
        }
        if (minecraft.thePlayer.itemInUseCount == 999) {
            minecraft.thePlayer.itemInUseCount = 0;
        }
        target = null;
        blockTarget = null;
    }

    private void unBlock() {
        isBlocking = false;
        minecraft.playerController.syncCurrentPlayItem();
        minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

    private boolean hasSword() {
        return minecraft.thePlayer.inventory.getCurrentItem() != null && minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }

    private enum Mode {
        Switch, Single, Multi, Multi2
    }

    private enum Type {
        PRE, POST
    }

    private enum AutoBlockMode {
        Basic1, NCP, Hypixel, Basic2
    }

    private enum Rotations {
        Basic, Smooth, Legit, Predict
    }

    private enum Priority {
        Angle, Range, Health, FOV, Armor
    }

    private void blockHypixel(final EntityLivingBase ent) {
        this.isBlocking = true;
        if (interact.getValue()) {
            minecraft.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, new Vec3(ClientUtils.randomNumber(-50, 50) / 100.0, ClientUtils.randomNumber(0, 200) / 100.0, ClientUtils.randomNumber(-50, 50) / 100.0)));
            minecraft.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, C02PacketUseEntity.Action.INTERACT));
        }
        minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(ClientUtils.getHypixelBlockpos(minecraft.getSession().getUsername()), 255, minecraft.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
    }
}
