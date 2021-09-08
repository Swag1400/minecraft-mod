package me.comu.client.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Vec3;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathUtils
{
    public EntityPlayer pos;
    public PathFinder pathFinder;
    private float yaw;
    public static float fakeYaw;
    public static float fakePitch;

    public PathUtils(final String name) {
        this.pathFinder = new PathFinder(new WalkNodeProcessor());
        for (final Object i : ClientUtils.world().loadedEntityList) {
            if (i instanceof EntityPlayer && i != null) {
                final EntityPlayer player = (EntityPlayer)i;
                if (!player.getName().contains(name)) {
                    continue;
                }
                this.pos = player;
            }
        }
        if (this.pos != null) {
            this.move();
            final float[] rot = this.getRotationTo(this.pos.getPositionVector());
            PathUtils.fakeYaw = rot[0];
            PathUtils.fakePitch = rot[1];
        }
    }

    public void move() {
        if (ClientUtils.player().getDistance(this.pos.posX + 0.5, this.pos.posY + 0.5, this.pos.posZ + 0.5) > 0.3) {
            final PathEntity pe = this.pathFinder.func_176188_a(ClientUtils.world(), ClientUtils.player(), this.pos, 40.0f);
            if (pe != null && pe.getCurrentPathLength() > 1) {
                final PathPoint point = pe.getPathPointFromIndex(1);
                final float[] rot = this.getRotationTo(new Vec3(point.xCoord + 0.5, point.yCoord + 0.5, point.zCoord + 0.5));
                this.yaw = rot[0];
                final EntityPlayerSP player = ClientUtils.player();
                final EntityPlayerSP player2 = ClientUtils.player();
                final double n = 0.0;
                player2.motionZ = n;
                player.motionX = n;
                final double offset = 0.26;
                final double newx = Math.sin(this.yaw * 3.1415927f / 180.0f) * offset;
                final double newz = Math.cos(this.yaw * 3.1415927f / 180.0f) * offset;
                final EntityPlayerSP player3 = ClientUtils.player();
                player3.motionX -= newx;
                final EntityPlayerSP player4 = ClientUtils.player();
                player4.motionZ += newz;
                if (ClientUtils.player().onGround && ClientUtils.player().isCollidedHorizontally) {
                    ClientUtils.player().jump();
                }
            }
        }
    }

    public static float angleDifference(final float to, final float from) {
        return ((to - from) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public float[] getRotationTo(final net.minecraft.util.Vec3 pos) {
        final double xD = ClientUtils.player().posX - pos.xCoord;
        final double yD = ClientUtils.player().posY + ClientUtils.player().getEyeHeight() - pos.yCoord;
        final double zD = ClientUtils.player().posZ - pos.zCoord;
        final double yaw = Math.atan2(zD, xD);
        final double pitch = Math.atan2(yD, Math.sqrt(Math.pow(xD, 2.0) + Math.pow(zD, 2.0)));
        return new float[] { (float)Math.toDegrees(yaw) + 90.0f, (float)Math.toDegrees(pitch) };
    }
}
