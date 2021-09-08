package me.comu.api.minecraft.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.UUID;


public final class EntityHelper
{
    private static Minecraft minecraft = Minecraft.getMinecraft();

    public static Entity getClosestEntity(double x, double y, double z, float distance)
    {
        double var9 = -1D;
        Entity closestEntity = null;

        for (int loadedEntity = 0; loadedEntity < minecraft.theWorld.loadedEntityList.size(); ++loadedEntity)
        {
            Entity entity = (Entity) minecraft.theWorld.loadedEntityList.get(loadedEntity);

            if (IEntitySelector.field_180132_d.apply(entity))
            {
                double distanceSq = entity.getDistanceSq(x, y, z);

                if ((distance < 0D || distanceSq < distance * distance) && (var9 == -1D || distanceSq < var9))
                {
                    var9 = distanceSq;
                    closestEntity = entity;
                }
            }
        }

        return closestEntity;
    }
    public static boolean isOnSameTeam(final boolean teams, final EntityLivingBase e)
    {
        return teams && e.isOnSameTeam(EntityHelper.minecraft.thePlayer);
    }

    public static float[] getRotations(Entity entity)
    {
        double positionX = entity.posX - minecraft.thePlayer.posX;
        double positionZ = entity.posZ - minecraft.thePlayer.posZ;
        double positionY = entity.posY + entity.getEyeHeight() / 1.3D - (minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight());
        double positions = MathHelper.sqrt_double(positionX * positionX + positionZ * positionZ);
        float yaw = (float)(Math.atan2(positionZ, positionX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) - (Math.atan2(positionY, positions) * 180.0D / Math.PI);
        return new float[] {yaw, pitch};
    }

    public static float getYawminecraftToEntity(final Entity entity) {
        final double deltaX = entity.posX - minecraft.thePlayer.posX;
        final double deltaZ = entity.posZ - minecraft.thePlayer.posZ;
        double yawToEntity;
        if (deltaZ < 0.0 && deltaX < 0.0) {
            yawToEntity = 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else if (deltaZ < 0.0 && deltaX > 0.0) {
            yawToEntity = -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else if (minecraft.thePlayer.getDistanceToEntity(entity) <= 0.14) {
            yawToEntity = -15.0f - minecraft.thePlayer.getDirection();
        }
        else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(minecraft.thePlayer.rotationYaw - (float)yawToEntity));
    }
    public static float getPitchChangeToEntity(final Entity entity) {
        final double deltaX = entity.posX - minecraft.thePlayer.posX;
        final double deltaZ = entity.posZ - minecraft.thePlayer.posZ;
        final double deltaY = entity.posY - 1.6 + entity.getEyeHeight() - minecraft.thePlayer.posY;
        final double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(minecraft.thePlayer.rotationPitch - (float)pitchToEntity);
    }
    public static float[] getAngles(final Entity e) {
        return new float[] { getYawminecraftToEntity(e) + minecraft.thePlayer.rotationYaw, getPitchChangeToEntity(e) + minecraft.thePlayer.rotationPitch };
    }
    public static float[] getRotationsAtLocation(Location location, Entity entity)
    {
        double positionX = entity.posX - minecraft.thePlayer.posX;
        double positionZ = entity.posZ - minecraft.thePlayer.posZ;
        double locationMath;

        switch (location)
        {
            case HEAD:
                locationMath = 1D;
                break;

            case BODY:
                locationMath = 1.3D;
                break;

            case LEGS:
                locationMath = 2.9D;
                break;

            case FEET:
                locationMath = 4D;
                break;

            case GROUND:
                locationMath = 6D;

            default:
                locationMath = 1.3D;
                break;
        }

        double positionY = entity.posY + entity.getEyeHeight() / locationMath - (minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight());
        double positions = MathHelper.sqrt_double(positionX * positionX + positionZ * positionZ);
        float yaw = (float)(Math.atan2(positionZ, positionX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) - (Math.atan2(positionY, positions) * 180.0D / Math.PI);
        return new float[] {yaw, pitch};
    }

    public static UUID getUUID(EntityPlayer entityPlayer)
    {
        return entityPlayer.getUniqueID();
    }

    public enum Location
    {
        HEAD, BODY, LEGS, FEET, GROUND
    }
}
