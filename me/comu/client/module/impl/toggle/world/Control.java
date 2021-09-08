package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public final class Control extends ToggleableModule {
    private final Property<Boolean> silent = new Property<>(true, "Silent", "s");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.SURVIVAL, "Mode", "m");
    private BlockPos selection;
    private MovingObjectPosition target;
    public PathFinder pathFinder;
    public float oldYaw;
    public float oldPitch;
    public static Vec3 pos1;
    public static Vec3 pos2;


    @Override
    protected void onDisable() {
        minecraft.timer.timerSpeed = 1.0f;
    }

    public Control() {
        super("Control", new String[]{"survival", "survivalnuker", "sn", "nuker", "control"}, 0xFFCC846E, ModuleType.WORLD);
        this.offerProperties(mode, silent);
        pathFinder = new PathFinder(new WalkNodeProcessor());
        Gun.getInstance().getCommandManager().register(new Command(new String[]{"controlpos", "cpos", "controlposition"}, new Argument("position")) {
            @Override
            public String dispatch() {
                String pos = getArgument("Position").getValue();
                if (pos.contains("pos1") || pos.contains("pos-1") || pos.contains("position1") || pos.contains("position-1") || pos.contains("1")) {
                    Control.pos1 = Minecraft.getMinecraft().thePlayer.getPositionVector();
                    return "Control Position 1 set to \247e" + (int) Control.pos1.xCoord + ", " + (int) Control.pos1.yCoord + ", " + (int) Control.pos1.zCoord + "\2477.";
                } else if (pos.contains("pos2") || pos.contains("pos-2") || pos.contains("position2") || pos.contains("position-2") || pos.contains("2")) {
                    Control.pos2 = Minecraft.getMinecraft().thePlayer.getPositionVector();
                    return "Control Position 2 set to \247e" + (int) Control.pos2.xCoord + ", " + (int) Control.pos2.yCoord + ", " + (int) Control.pos2.zCoord + "\2477.";
                }
                Control.pos1 = null;
                Control.pos2 = null;
                return "Couldn't set position vectors.";


            }
        });
        this.listeners.add(new Listener<MotionUpdateEvent>("speedy_gonzales_mining_speed_listener") {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (isRunning()) {
                    oldPitch = minecraft.thePlayer.rotationPitch;
                    oldYaw = minecraft.thePlayer.rotationYaw;
                    if (selection != null && minecraft.thePlayer.getDistance(selection.getX() + 0.5, selection.getY() + 0.5, selection.getZ() + 0.5) > 3.5) {
                        final PathEntity pe = pathFinder.func_180782_a(minecraft.theWorld, minecraft.thePlayer, selection, 50.0f);
                        if (pe != null && pe.getCurrentPathLength() > 1) {
                            final PathPoint point = pe.getPathPointFromIndex(1);
                            final float[] rot = getRotationTo(new Vec3(point.xCoord + 0.5, point.yCoord + 0.5, point.zCoord + 0.5));
                            minecraft.thePlayer.rotationYaw = rot[0];
                            final EntityPlayerSP thePlayer = minecraft.thePlayer;
                            final EntityPlayerSP thePlayer2 = minecraft.thePlayer;
                            final double n = 0.0;
                            thePlayer2.motionZ = n;
                            thePlayer.motionX = n;
                            final double offset = mode.getValue() == Mode.SURVIVAL ? 0.3f : 0.5f;
                            final double newx = Math.sin(minecraft.thePlayer.rotationYaw * 3.1415927f / 180.0f) * offset;
                            final double newz = Math.cos(minecraft.thePlayer.rotationYaw * 3.1415927f / 180.0f) * offset;
                            final EntityPlayerSP thePlayer3 = minecraft.thePlayer;
                            thePlayer3.motionX -= newx;
                            final EntityPlayerSP thePlayer4 = minecraft.thePlayer;
                            thePlayer4.motionZ += newz;
                            if (mode.getValue() == Mode.CREATIVE && minecraft.thePlayer.isCollidedHorizontally && minecraft.thePlayer.onGround) {
                                minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0, minecraft.thePlayer.posZ);
                            }
                            if (mode.getValue() == Mode.SURVIVAL) {
                                if (minecraft.thePlayer.isCollidedHorizontally && minecraft.thePlayer.onGround) {
                                    minecraft.thePlayer.jump();
                                }
                                if ((minecraft.thePlayer.isInWater() || minecraft.thePlayer.isInsideOfMaterial(Material.lava)) && !minecraft.gameSettings.keyBindSneak.pressed && !minecraft.gameSettings.keyBindJump.pressed) {
                                    final EntityPlayerSP thePlayer5 = minecraft.thePlayer;
                                    thePlayer5.motionY += 0.039;
                                }
                            }
                        }
                    }
                    final Vec3 pos1 = Control.pos1;
                    final Vec3 pos2 = Control.pos2;
                    target = null;
                    selection = null;
                    if (pos1 != null && pos2 != null) {
                        final double minX = Math.min(pos1.xCoord, pos2.xCoord);
                        final double maxX = Math.max(pos1.xCoord, pos2.xCoord);
                        final double minY = Math.min(pos1.yCoord, pos2.yCoord);
                        final double maxY = Math.max(pos1.yCoord, pos2.yCoord);
                        final double minZ = Math.min(pos1.zCoord, pos2.zCoord);
                        final double maxZ = Math.max(pos1.zCoord, pos2.zCoord);
                        double x = maxX;
                        double y = maxY;
                        double z = maxZ;
                        boolean xDir = false;
                        boolean zDir = false;
                        while (true) {
                            final BlockPos blockPos = new BlockPos(x, y, z);
                            if (isBlockInSelection(blockPos)) {
                                final IBlockState state = minecraft.theWorld.getBlockState(blockPos);
                                if (state.getBlock() != Blocks.air) {
                                    selection = blockPos;
                                    break;
                                }
                            }
                            x += (xDir ? 1 : -1);
                            if (xDir) {
                                if (x <= Math.max(pos1.xCoord, pos2.xCoord)) {
                                    continue;
                                }
                            } else if (x >= Math.min(pos1.xCoord, pos2.xCoord)) {
                                continue;
                            }
                            xDir = !xDir;
                            z += (zDir ? 1 : -1);
                            if (zDir) {
                                if (z <= Math.max(pos1.zCoord, pos2.zCoord)) {
                                    continue;
                                }
                            } else if (z >= Math.min(pos1.zCoord, pos2.zCoord)) {
                                continue;
                            }
                            --y;
                            zDir = !zDir;
                            xDir = !xDir;
                            if (y < Math.min(pos1.yCoord, pos2.yCoord)) {
                                break;
                            }
                        }
                    }
                    if (selection != null) {
                        final Block block = minecraft.theWorld.getBlockState(selection).getBlock();
                        block.setBlockBoundsBasedOnState(minecraft.theWorld, selection);
                        final AxisAlignedBB sel = block.getSelectedBoundingBox(minecraft.theWorld, selection);
                        final Vec3 from = minecraft.thePlayer.getPositionVector().addVector(0.0, minecraft.thePlayer.getEyeHeight(), 0.0);
                        final Vec3 to = new Vec3(sel.minX + (sel.maxX - sel.minX) / 2.0, sel.minY + (sel.maxY - sel.minY) / 2.0, sel.minZ + (sel.maxZ - sel.minZ) / 2.0);
                        Vec3 alternate = null;
                        target = minecraft.theWorld.rayTraceBlocks(from, to, true);
                        if (target != null) {
                            final IBlockState state2 = minecraft.theWorld.getBlockState(target.getPos());
                            boolean inSelection = isBlockInSelection(target.getPos());
                            boolean validBlock = state2.getBlock() != Blocks.air;
                            boolean inRange = target.hitVec.distanceTo(from) < minecraft.playerController.getBlockReachDistance() - 0.1 && to.distanceTo(from) < minecraft.playerController.getBlockReachDistance() - 0.1;
                            if (!inRange) {
                                target = null;
                            } else if (!inSelection || !validBlock) {
                                boolean done = false;
                                for (int i = -1; i <= 1; ++i) {
                                    if (done) {
                                        break;
                                    }
                                    for (int j = -1; j <= 1 && !done; ++j) {
                                        for (int k = -1; k <= 1; ++k) {
                                            final Vec3 toNew = to.addVector(i / 2.0f, j / 2.0f, k / 2.0f);
                                            target = minecraft.theWorld.rayTraceBlocks(from, toNew, true);
                                            if (target != null) {
                                                inSelection = isBlockInSelection(target.getPos());
                                                validBlock = (state2.getBlock() != Blocks.air);
                                                inRange = (target.hitVec.distanceTo(from) < minecraft.playerController.getBlockReachDistance() - 0.1 && to.distanceTo(from) < minecraft.playerController.getBlockReachDistance() - 0.1);
                                                if (inSelection && validBlock && inRange) {
                                                    done = true;
                                                    alternate = target.hitVec;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (target != null) {
                            float[] rot2 = getRotationTo(target);
                            if (alternate != null) {
                                rot2 = getRotationTo(alternate);
                            }
                            final EntityPlayerSP thePlayer6 = minecraft.thePlayer;
                            thePlayer6.rotationYaw += angleDifference(rot2[0], minecraft.thePlayer.rotationYaw);
                            final EntityPlayerSP thePlayer7 = minecraft.thePlayer;
                            thePlayer7.rotationPitch += angleDifference(rot2[1], minecraft.thePlayer.rotationPitch);
                        }
                    }
                    if (silent.getValue()) {
                        minecraft.thePlayer.rotationPitch = oldPitch;
                        minecraft.thePlayer.rotationYaw = oldYaw;
                    }
                    if (target != null) {
                        if (mode.getValue() == Mode.SURVIVAL) {
                            minecraft.thePlayer.swingItem();
                            minecraft.playerController.func_180512_c(target.getPos(), target.field_178784_b);
                        }
                        if (mode.getValue() == Mode.CREATIVE) {
                            minecraft.playerController.func_180512_c(target.getPos(), target.field_178784_b);
                        }
                    }
                }
            }
        });
        this.listeners.add(new Listener<RenderEvent>("speedy_gonzales_mining_speed_listener") {

            @Override
            public void call(RenderEvent event) {
                if (isRunning()) {
                    if (Control.pos1 != null && Control.pos2 != null) {
                        drawESP((int) Control.pos1.xCoord, (int) Control.pos1.yCoord, (int) Control.pos1.zCoord + 1, (int) Control.pos2.xCoord, (int) Control.pos2.yCoord, (int) Control.pos2.zCoord + 1, 0.2, 1.0, 0.2);
                    }
                }
            }
        });
    }


    public void drawESP(final double x, final double y, final double z, final double x2, final double y2, final double z2, final double r, final double g, final double b) {
        final double x3 = x - RenderManager.renderPosX;
        final double y3 = y - RenderManager.renderPosY;
        final double z3 = z - RenderManager.renderPosZ;
        final double x4 = x2 - RenderManager.renderPosX;
        final double y4 = y2 - RenderManager.renderPosY;
        RenderMethods.enableGL3D();


        AxisAlignedBB boundingBox = new AxisAlignedBB(x3, y3, z3, x4, y4, z2 - RenderManager.renderPosZ);
        GlStateManager.color(0.0F, 0.0F, 0.25F, 0.75F);
        RenderMethods.renderCrosses(boundingBox);
        RenderMethods.drawOutlinedBox(boundingBox);
        RenderMethods.disableGL3D();

    }

    public static float[] getRotationTo(final Vec3 pos) {
        final double xD = minecraft.thePlayer.posX - pos.xCoord;
        final double yD = minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight() - pos.yCoord;
        final double zD = minecraft.thePlayer.posZ - pos.zCoord;
        final double yaw = Math.atan2(zD, xD);
        final double pitch = Math.atan2(yD, Math.sqrt(Math.pow(xD, 2.0) + Math.pow(zD, 2.0)));
        return new float[]{(float) Math.toDegrees(yaw) + 90.0f, (float) Math.toDegrees(pitch)};
    }

    public static float angleDifference(final float to, final float from) {
        return ((to - from) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public static float[] getRotationTo(final MovingObjectPosition mop) {
        final BlockPos pos = mop.getPos();
        final double xD = minecraft.thePlayer.posX - mop.hitVec.xCoord;
        final double yD = minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight() - mop.hitVec.yCoord;
        final double zD = minecraft.thePlayer.posZ - mop.hitVec.zCoord;
        final double yaw = Math.atan2(zD, xD);
        final double pitch = Math.atan2(yD, Math.sqrt(Math.pow(xD, 2.0) + Math.pow(zD, 2.0)));
        return new float[]{(float) Math.toDegrees(yaw) + 90.0f, (float) Math.toDegrees(pitch)};
    }

    public boolean isBlockInSelection(final BlockPos pos) {
        return Control.pos1 == null || Control.pos2 == null || (pos.getX() >= Math.min(Control.pos1.xCoord, Control.pos2.xCoord) && pos.getX() <= Math.max(Control.pos1.xCoord, Control.pos2.xCoord) && pos.getY() >= Math.min(Control.pos1.yCoord, Control.pos2.yCoord) && pos.getY() <= Math.max(Control.pos1.yCoord, Control.pos2.yCoord) && pos.getZ() >= Math.min(Control.pos1.zCoord, Control.pos2.zCoord) && pos.getZ() <= Math.max(Control.pos1.zCoord, Control.pos2.zCoord));
    }

    private enum Mode {
        SURVIVAL, CREATIVE
    }
}
