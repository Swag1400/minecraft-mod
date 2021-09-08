package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Fucker extends ToggleableModule {

    private final Property<Boolean> box = new Property<>(true, "Box","b","esp");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.BED, "Mode", "m");
    public static BlockPos blockBreaking;
    List<BlockPos> beds = new ArrayList<>();
    List<BlockPos> cakes = new ArrayList<>();

    @Override
    public void onDisable(){
        if(blockBreaking != null)
            blockBreaking = null;
    }

    public Fucker() {
        super("Fucker", new String[]{"fucker","fuck","bedfucker","cakewars","bf","cw","cakebreak","cakedestroy","bedwars", "beddestroy", "bedbreak"}, 0xFF5ED7FF, ModuleType.WORLD);
        super.offerProperties(mode, box);
        this.listeners.add(new Listener<MotionUpdateEvent>("avoid_block_bounding_box_listener") {
            @Override
            public void call(MotionUpdateEvent event) {

                setTag(String.format("%sFucker", mode.getFixedValue()));

                if (isRunning()) {
            if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                int reach = 6;
                for (int y = reach; y >= -reach; --y) {
                    for (int x = -reach; x <= reach; ++x) {
                        for (int z = -reach; z <= reach; ++z) {
                            if (minecraft.thePlayer.isSneaking()) {
                                return;
                            }
                            BlockPos pos = new BlockPos(minecraft.thePlayer.posX + x, minecraft.thePlayer.posY + y, minecraft.thePlayer.posZ + z);
                            if (mode.getValue() == Mode.BED && getFacingDirectionForBed(pos) != null && bedCheck(minecraft.theWorld.getBlockState(pos).getBlock()) && minecraft.thePlayer.getDistance(minecraft.thePlayer.posX + x, minecraft.thePlayer.posY + y, minecraft.thePlayer.posZ + z) < minecraft.playerController.getBlockReachDistance() - 0.2) {
                                if (!beds.contains(pos))
                                    beds.add(pos);
                            }
                            if (mode.getValue() == Mode.CAKE && getFacingDirectionForCake(pos) != null && cakeCheck(minecraft.theWorld.getBlockState(pos).getBlock()) && minecraft.thePlayer.getDistance(minecraft.thePlayer.posX + x, minecraft.thePlayer.posY + y, minecraft.thePlayer.posZ + z) < minecraft.playerController.getBlockReachDistance() - 0.2) {
                                if (!cakes.contains(pos))
                                    cakes.add(pos);
                            }

                        }
                    }
                }
                BlockPos closest = null;
                if (!beds.isEmpty())
                    for (int i = 0; i < beds.size(); i++) {
                        BlockPos bed = beds.get(i);
                        if (mode.getValue() == Mode.BED && minecraft.thePlayer.getDistance(bed.getX(), bed.getY(), bed.getZ()) > minecraft.playerController.getBlockReachDistance() - 0.2 || minecraft.theWorld.getBlockState(bed).getBlock() != Blocks.bed) {
                            beds.remove(i);
                        }
                        if (mode.getValue() == Mode.BED && closest == null || minecraft.thePlayer.getDistance(bed.getX(), bed.getY(), bed.getZ()) < minecraft.thePlayer.getDistance(closest.getX(), closest.getY(), closest.getZ())) {
                            closest = bed;
                        }
                    }
                if (!cakes.isEmpty())
                    for (int i = 0; i < cakes.size(); i++) {
                        BlockPos cake = cakes.get(i);
                        if (mode.getValue() == Mode.CAKE && minecraft.thePlayer.getDistance(cake.getX(), cake.getY(), cake.getZ()) > minecraft.playerController.getBlockReachDistance() - 0.2 || minecraft.theWorld.getBlockState(cake).getBlock() != Blocks.cake) {
                            cakes.remove(i);
                        }
                        if (mode.getValue() == Mode.CAKE && closest == null || minecraft.thePlayer.getDistance(cake.getX(), cake.getY(), cake.getZ()) < minecraft.thePlayer.getDistance(closest.getX(), closest.getY(), closest.getZ())) {
                            closest = cake;
                        }
                    }
                if (closest != null) {
                    float[] rot = getRotations(closest, getClosestEnum(closest));
                    event.setRotationYaw(rot[0]);
                    event.setRotationPitch(rot[1]);
                    //minecraft.thePlayer.rotationYaw = rot[0];
                    // minecraft.thePlayer.rotationPitch = rot[1];
                    blockBreaking = closest;
                    return;
                }
                blockBreaking = null;
            }   else {
                if (mode.getValue().equals(Mode.BED)) {
                    if (blockBreaking != null) {
                        if (minecraft.playerController.blockHitDelay > 1) {
                            minecraft.playerController.blockHitDelay = 1;
                        }
                        minecraft.thePlayer.swingItem();
                        EnumFacing direction = getClosestEnum(blockBreaking);
                        if (direction != null) {
                            minecraft.playerController.func_180512_c(blockBreaking, direction);
                        }
                } else {
                    if (blockBreaking != null)
                        drawESP(blockBreaking.getX(), blockBreaking.getY(), blockBreaking.getZ(), blockBreaking.getX() + 1, blockBreaking.getY() + 0.5625, blockBreaking.getZ() + 1);

                    }
            } else if (mode.getValue().equals(Mode.CAKE)) {
                    if (blockBreaking != null) {
                        minecraft.thePlayer.swingItem();
                        EnumFacing direction = getClosestEnum(blockBreaking);
                        if (direction != null) {
                            minecraft.playerController.func_180512_c(blockBreaking, direction);
                        }
                    } else {
                        if (blockBreaking != null)
                            drawESP(blockBreaking.getX(), blockBreaking.getY(), blockBreaking.getZ(), blockBreaking.getX() + 1, blockBreaking.getY() + 0.5625, blockBreaking.getZ() + 1);

                    }
                }
            }
            }
            }

        });
        this.listeners.add(new Listener<RenderEvent>("auto_farm_motion_update_listener") {
            @Override
            public void call(RenderEvent event) {
                if (blockBreaking != null) {
                    GlStateManager.pushMatrix();
                    RenderMethods.enableGL3D();
                    double x = blockBreaking.getX() - minecraft.getRenderManager().viewerPosX;
                    double y = blockBreaking.getY() - minecraft.getRenderManager().viewerPosY;
                    double z = blockBreaking.getZ() - minecraft.getRenderManager().viewerPosZ;
                    AxisAlignedBB boundingBox = AxisAlignedBB.fromBounds(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                    if (box.getValue()) {
                        GlStateManager.color(250.0F, 100.0F, 90.0F, 0.3F);
                        RenderGlobal.drawOutlinedBoundingBox(boundingBox, -1);
                        drawESP(blockBreaking.getX(), blockBreaking.getY(), blockBreaking.getZ(), blockBreaking.getX() + 1, blockBreaking.getY() + 0.5625, blockBreaking.getZ() + 1);
                    }
                    RenderMethods.disableGL3D();
                    GlStateManager.popMatrix();
                }
            }
            });
        }
    public static float[] getRotations(BlockPos block, EnumFacing face){
        double x = block.getX() + 0.5 - minecraft.thePlayer.posX + (double)face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - minecraft.thePlayer.posZ + (double)face.getFrontOffsetZ()/2;
        double d1 = minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight() -(block.getY() + 0.5);
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
        if(yaw < 0.0F){
            yaw += 360f;
        }
        return  new float[]{yaw, pitch};
    }
    public void drawESP(double x, double y, double z, double x2, double y2, double z2) {
        double x3 = x - RenderManager.renderPosX;
        double y3 = y - RenderManager.renderPosY;
        double z3 = z - RenderManager.renderPosZ;
        double x4 = x2 - RenderManager.renderPosX;
        double y4 = y2 - RenderManager.renderPosY;
        Color color = new Color(250, 100, 90, 80);
        drawFilledBBESP(new AxisAlignedBB(x3, y3, z3, x4, y4, z2 - RenderManager.renderPosZ), color);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    private EnumFacing getClosestEnum(BlockPos pos){
        EnumFacing closestEnum = EnumFacing.UP;
        float rotations = MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[0]);
        if(rotations >= 45 && rotations <= 135){
            closestEnum = EnumFacing.EAST;
        }else if((rotations >= 135 && rotations <= 180) ||
                (rotations <= -135 && rotations >= -180)){
            closestEnum = EnumFacing.SOUTH;
        }else if(rotations <= -45 && rotations >= -135){
            closestEnum = EnumFacing.WEST;
        }else if((rotations >= -45 && rotations <= 0) ||
                (rotations <= 45 && rotations >= 0)){
            closestEnum = EnumFacing.NORTH;
        }
        if (MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) > 75 ||
                MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) < -75){
            closestEnum = EnumFacing.UP;
        }
        return closestEnum;
    }
    public void drawFilledBBESP(AxisAlignedBB axisalignedbb, Color color) {
        GL11.glPushMatrix();
        float red = (float)color.getRed()/255;
        float green = (float)color.getGreen()/255;
        float blue = (float)color.getBlue()/255;
        float alpha = 0.3f;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        ClientUtils.drawBoundingBox(axisalignedbb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    private boolean bedCheck(Block block) {
        return block == Blocks.bed;
    }
    private boolean cakeCheck(Block block) {
        return block == Blocks.cake;
    }
    private EnumFacing getFacingDirectionForBed(BlockPos pos) {
        EnumFacing direction = null;
        if (!minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.UP;
        } else if (!minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.DOWN;
        } else if (!minecraft.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.EAST;
        } else if (!minecraft.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.WEST;
        } else if (!minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.SOUTH;
        } else if (!minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof BlockBed)) {
            direction = EnumFacing.NORTH;
        }
        MovingObjectPosition rayResult = minecraft.theWorld.rayTraceBlocks(new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight(), minecraft.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null && rayResult.getPos() == pos) {
            return rayResult.field_178784_b;
        }
        return direction;
    }

    private EnumFacing getFacingDirectionForCake(BlockPos pos) {
        EnumFacing direction = null;
        if (!minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockCake)) {
            direction = EnumFacing.UP;
        } else if (!minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockCake)) {
            direction = EnumFacing.DOWN;
        } else if (!minecraft.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof BlockCake)) {
            direction = EnumFacing.EAST;
        } else if (!minecraft.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof BlockCake)) {
            direction = EnumFacing.WEST;
        } else if (!minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof BlockCake)) {
            direction = EnumFacing.SOUTH;
        } else if (!minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube() && !(minecraft.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof BlockCake)) {
            direction = EnumFacing.NORTH;
        }
        MovingObjectPosition rayResult = minecraft.theWorld.rayTraceBlocks(new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight(), minecraft.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null && rayResult.getPos() == pos) {
            return rayResult.field_178784_b;
        }
        return direction;
    }

    private enum Mode {
        BED, CAKE
    }

}

