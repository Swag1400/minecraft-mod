package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

public final class Dolphin extends ToggleableModule {

    int stage, water;
    private EnumProperty<Mode> mode = new EnumProperty<>(Mode.DOLPHIN, "Mode", "m");
    Stopwatch timer = new Stopwatch();

    public Dolphin() {
        super("Dolphin", new String[]{"dolphin", "dolph"}, 0xFFB5C75B, ModuleType.MOVEMENT);
        this.offerProperties(mode);
        this.listeners.add(new Listener<MotionUpdateEvent>("jesus_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                if (minecraft.thePlayer.isInWater() && !minecraft.thePlayer.isSneaking() && shouldJesus() && !mode.getValue().equals(Mode.AAC)) {
                    minecraft.thePlayer.motionY = 0.09;
                }
                if (mode.getValue().equals(Mode.BASIC)) {
                    if (PlayerHelper.isOnLiquid(0.001))
                        if (PlayerHelper.isTotalOnLiquid(0.001) && minecraft.thePlayer.onGround && !minecraft.thePlayer.isInWater()) {
                            event.setPositionY(event.getPositionY() + (minecraft.thePlayer.ticksExisted % 2 == 0 ? 0.0000000001D : -0.000000000001D));
                        }
                }
                if (mode.getValue().equals(Mode.DOLPHIN)) {
                    if (minecraft.thePlayer.onGround && !minecraft.thePlayer.isInWater() && shouldJesus()) {
                        stage = 1;
                        timer.reset();
                    }
                    if (stage > 0 && !timer.hasCompleted(2500)) {
                        if ((minecraft.thePlayer.isCollidedVertically && !PlayerHelper.isOnGround(0.001)) || minecraft.thePlayer.isSneaking()) {
                            stage = -1;
                        }
                        minecraft.thePlayer.motionX *= 0;
                        minecraft.thePlayer.motionZ *= 0;
                        if (!PlayerHelper.isInLiquid() && !minecraft.thePlayer.isInWater()) {
                            PlayerHelper.setMotion(0.25 + PlayerHelper.getSpeedEffect() * 0.05);
                        }
                        double motionY = getMotionY(stage);
                        if (motionY != -999) {
                            minecraft.thePlayer.motionY = motionY;

                        }

                        stage += 1;
                    }
                } else {
                    water = 0;
                }

            }


        });
        this.listeners.add(new Listener<PacketEvent>("lalala") {
            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    stage = 0;
                }
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();

                    if(PlayerHelper.isOnLiquid(0.001))
                        if(PlayerHelper.isTotalOnLiquid(0.001) && minecraft.thePlayer.onGround && !minecraft.thePlayer.isInWater()){
                            if (minecraft.thePlayer.ticksExisted % 2 == 0)
                            {
                                player.setPositionY(player.getPositionY() - 0.01F);
                            }
                        }
                    }
            }
        });
    }


    public double getMotionY(double stage) {
        stage--;
        double[] motion = new double[]{0.500, 0.484, 0.468, 0.436, 0.404, 0.372, 0.340, 0.308, 0.276, 0.244, 0.212, 0.180, 0.166, 0.166,
                0.156, 0.123, 0.135, 0.111, 0.086, 0.098, 0.073, 0.048, 0.06, 0.036, 0.0106, 0.015, 0.004, 0.004, 0.004, 0.004,
                -0.013, -0.045, -0.077, -0.109};
        if (stage < motion.length && stage >= 0)
            return motion[(int) stage];
        else
            return -999;

    }

    boolean shouldJesus() {
        double x = minecraft.thePlayer.posX;
        double y = minecraft.thePlayer.posY;
        double z = minecraft.thePlayer.posZ;
        ArrayList<BlockPos> pos = new ArrayList<BlockPos>(Arrays.asList(new BlockPos(x + 0.3, y, z + 0.3),
                new BlockPos(x - 0.3, y, z + 0.3), new BlockPos(x + 0.3, y, z - 0.3), new BlockPos(x - 0.3, y, z - 0.3)));
        for (BlockPos po : pos) {
            if (!(minecraft.theWorld.getBlockState(po).getBlock() instanceof BlockLiquid))
                continue;
            if (minecraft.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) instanceof Integer) {
                if ((int) minecraft.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) <= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onEnable() {
        stage = 0;
        water = 0;
    }

    private enum Mode {
        DOLPHIN, AAC, BASIC
    }
}

