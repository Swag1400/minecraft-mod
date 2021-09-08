package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.ClientUtils;
import net.minecraft.util.Vec3;

public class PathFind extends ToggleableModule {

    private int xPos, yPos, zPos;

    public PathFind() {
        super("PathFind", new String[]{"PathFind","pf","path","pathfinder","findpath","pathf"}, 0x83FF52, ModuleType.WORLD);
        this.listeners.add(new Listener<MotionUpdateEvent>("path_find_motion_update_event") {
            @Override
            public void call(MotionUpdateEvent event) {
                Gun.getInstance().getCommandManager().register(new Command(new String[] {"PathFind","pf","path","pathfinder","findpath","pathf"}, new Argument("x"), new Argument("y"), new Argument("z"))
                {
                    @Override
                    public String dispatch()
                    {
                        String name = getArgument("label").getValue();
                        int x = Integer.parseInt(getArgument("x").getValue());
                        int y = Integer.parseInt(getArgument("y").getValue());
                        int z = Integer.parseInt(getArgument("z").getValue());
                        xPos = x; yPos = y; zPos = z;
                        return "Path Finding set to: " + x + ", "+ y + ", " + z + "\247e.";
                    }
                });
             //   move(xPos, yPos, zPos);
            }
        });

    }

//    public void move(int x, int y, int z) {
//        if (ClientUtils.player().getDistance(x + 0.5, y + 0.5, z+ 0.5) > 0.3) {
//            final PathEntity pe = this.pathFinder.func_176188_a(ClientUtils.world(), ClientUtils.player(), this.pos, 40.0f);
//            if (pe != null && pe.getCurrentPathLength() > 1) {
//                final PathPoint point = pe.getPathPointFromIndex(1);
//                final float[] rot = getRotationTo(new Vec3(point.xCoord + 0.5, point.yCoord + 0.5, point.zCoord + 0.5));
//                minecraft.thePlayer.rotationYaw= rot[0];
//                final EntityPlayerSP player = ClientUtils.player();
//                final EntityPlayerSP player2 = ClientUtils.player();
//                final double n = 0.0;
//                player2.motionZ = n;
//                player.motionX = n;
//                final double offset = 0.26;
//                final double newx = Math.sin( minecraft.thePlayer.rotationYaw * 3.1415927f / 180.0f) * offset;
//                final double newz = Math.cos( minecraft.thePlayer.rotationYaw * 3.1415927f / 180.0f) * offset;
//                final EntityPlayerSP player3 = ClientUtils.player();
//                player3.motionX -= newx;
//                final EntityPlayerSP player4 = ClientUtils.player();
//                player4.motionZ += newz;
//                if (ClientUtils.player().onGround && ClientUtils.player().isCollidedHorizontally) {
//                    ClientUtils.player().jump();
//                }
//            }
//        }
//    }
    public float[] getRotationTo(final Vec3 pos) {
        final double xD = ClientUtils.player().posX - pos.xCoord;
        final double yD = ClientUtils.player().posY + ClientUtils.player().getEyeHeight() - pos.yCoord;
        final double zD = ClientUtils.player().posZ - pos.zCoord;
        final double yaw = Math.atan2(zD, xD);
        final double pitch = Math.atan2(yD, Math.sqrt(Math.pow(xD, 2.0) + Math.pow(zD, 2.0)));
        return new float[] { (float)Math.toDegrees(yaw) + 90.0f, (float)Math.toDegrees(pitch) };
    }

}
