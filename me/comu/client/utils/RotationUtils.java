package me.comu.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.*;
import java.util.List;

public class RotationUtils
{
    public static float[] getRotations(final EntityLivingBase ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.posY + ent.getEyeHeight() / 2.0f - 0.5;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getAverageRotations(final List<EntityLivingBase> targetList) {
        double posX = 0.0;
        double posY = 0.0;
        double posZ = 0.0;
        for (final Entity ent : targetList) {
            posX += ent.posX;
            posY += ent.boundingBox.maxY - 2.0;
            posZ += ent.posZ;
        }
        posX /= targetList.size();
        posY /= targetList.size();
        posZ /= targetList.size();
        return new float[] { getRotationFromPosition(posX, posZ, posY)[0], getRotationFromPosition(posX, posZ, posY)[1] };
    }
    public static float[] getPredictedRotations(final EntityLivingBase ent) {
        final double x = ent.posX + (ent.posX - ent.lastTickPosX);
        final double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
        final double y = ent.posY + ent.getEyeHeight() / 2.0f;
        return getRotationFromPosition(x, z, y);
    }
    public static boolean canEntityBeSeen(final Entity e) {
        final net.minecraft.util.Vec3 vec1 = new net.minecraft.util.Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
        final AxisAlignedBB box = e.getEntityBoundingBox();
        net.minecraft.util.Vec3 vec2 = new net.minecraft.util.Vec3(e.posX, e.posY + e.getEyeHeight() / 1.32f, e.posZ);
        final double minx = e.posX - 0.25;
        final double maxx = e.posX + 0.25;
        final double miny = e.posY;
        final double maxy = e.posY + Math.abs(e.posY - box.maxY);
        final double minz = e.posZ - 0.25;
        final double maxz = e.posZ + 0.25;
        boolean see = Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(maxx, miny, minz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(minx, miny, minz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(minx, miny, maxz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(maxx, miny, maxz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(maxx, maxy, minz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(minx, maxy, minz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(minx, maxy, maxz - 0.1);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new net.minecraft.util.Vec3(maxx, maxy, maxz);
        see = (Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec1, vec2) == null);
        return see;
    }


    public static net.minecraft.util.Vec3 getRandomCenter(AxisAlignedBB bb) {
        return new net.minecraft.util.Vec3(bb.minX + (bb.maxX - bb.minX) * 0.8 * Math.random(), bb.minY + (bb.maxY - bb.minY) * 1 * Math.random(), bb.minZ + (bb.maxZ - bb.minZ) * 0.8 * Math.random());
    }

    public static float[] getRotationsBlock(final BlockPos block, final EnumFacing face) {
        final double x = block.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ + face.getFrontOffsetZ() / 2.0;
        final double y = block.getY() + 0.5;
        final double d1 = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(Math.atan2(d1, d2) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[] { yaw, pitch };
    }

    public static float getYawChange(final float yaw, final double posX, final double posZ) {
        final double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
        final double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity = 0.0;
        if (deltaZ < 0.0 && deltaX < 0.0) {
            if (deltaX != 0.0) {
                yawToEntity = 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
            }
        }
        else if (deltaZ < 0.0 && deltaX > 0.0) {
            if (deltaX != 0.0) {
                yawToEntity = -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
            }
        }
        else if (deltaZ != 0.0) {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(yaw - (float)yawToEntity));
    }

    public static float getPitchChange(final float pitch, final Entity entity, final double posY) {
        final double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        final double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        final double deltaY = posY - 2.2 + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        final double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(pitch - (float)pitchToEntity) - 2.5f;
    }

    public static float[] getRotationFromVector(final net.minecraft.util.Vec3 vec) {
        final net.minecraft.util.Vec3 eyesPos = new net.minecraft.util.Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static net.minecraft.util.Vec3 getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new net.minecraft.util.Vec3((double)(f1 * f2), (double)f3, (double)(f * f2));
    }


    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(Minecraft.getMinecraft().theWorld);
        var4.posX = (double) var0 + 0.5D;
        var4.posY = (double) var1 + 0.5D;
        var4.posZ = (double) var2 + 0.5D;
        var4.posX += (double) var3.getDirectionVec().getX() * 0.25D;
        var4.posY += (double) var3.getDirectionVec().getY() * 0.25D;
        var4.posZ += (double) var3.getDirectionVec().getZ() * 0.25D;
        return getDirectionToEntity(var4);
    }

    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{getYaw(var0) + Minecraft.getMinecraft().thePlayer.rotationYaw, getPitch(var0) + Minecraft.getMinecraft().thePlayer.rotationPitch};
    }

    public static float[] getRotationNeededForBlock(EntityPlayer paramEntityPlayer, BlockPos pos) {
        double d1 = pos.getX() - paramEntityPlayer.posX;
        double d2 = pos.getY() + 0.5 - (paramEntityPlayer.posY + paramEntityPlayer.getEyeHeight());
        double d3 = pos.getZ() - paramEntityPlayer.posZ;
        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
        float f1 = (float) (Math.atan2(d3, d1) * 180.0D / Math.PI) - 90.0F;
        float f2 = (float) -(Math.atan2(d2, d4) * 180.0D / Math.PI);
        return new float[]{f1, f2};
    }

    public static float getYaw(Entity var0) {
        double var1 = var0.posX - Minecraft.getMinecraft().thePlayer.posX;
        double var3 = var0.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double var5;

        if (var3 < 0.0D && var1 < 0.0D) {
            var5 = 90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else if (var3 < 0.0D && var1 > 0.0D) {
            var5 = -90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else {
            var5 = Math.toDegrees(-Math.atan(var1 / var3));
        }

        return MathHelper.wrapAngleTo180_float(-(Minecraft.getMinecraft().thePlayer.rotationYaw - (float) var5));
    }

    public static float getPitch(Entity var0) {
        double var1 = var0.posX - Minecraft.getMinecraft().thePlayer.posX;
        double var3 = var0.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double var5 = var0.posY - 1.6D + (double) var0.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        double var7 = (double) MathHelper.sqrt_double(var1 * var1 + var3 * var3);
        double var9 = -Math.toDegrees(Math.atan(var5 / var7));
        return -MathHelper.wrapAngleTo180_float(Minecraft.getMinecraft().thePlayer.rotationPitch - (float) var9);
    }

    public static float[] getRotationFromPosition(final double x, final double z, final double y) {
        final double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        final double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        final double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 0.6;
        final double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[] { yaw, pitch };
    }

    public static float getTrajAngleSolutionLow(final float d3, final float d1, final float velocity) {
        final float g = 0.006f;
        final float sqrt = velocity * velocity * velocity * velocity - g * (g * (d3 * d3) + 2.0f * d1 * (velocity * velocity));
        return (float)Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(sqrt)) / (g * d3)));
    }

    public static float getNewAngle(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public static float getDistanceBetweenAngles(final float angle1, final float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 360.0f - angle3;
        }
        return angle3;
    }
}
