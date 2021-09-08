package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

/**
 * Created by comu on 5/24/2018
 */
public class HypixelFly extends ToggleableModule {

    public HypixelFly() {
        super("HypixelFly", new String[]{"HypixelFly", "hypixel", "hfly", "flypixel"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
        this.listeners.add(new Listener<MotionUpdateEvent>("high_jump_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                if (MotionUpdateEvent.Time.BEFORE == event.getTime()) {
                    minecraft.thePlayer.motionY = 0.0;
                    minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-9, minecraft.thePlayer.posZ);
                    if (minecraft.thePlayer.ticksExisted % 3 == 0 && minecraft.theWorld.getBlockState(new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 0.2, minecraft.thePlayer.posZ)).getBlock() instanceof BlockAir) {
                        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-9, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch, true));
                    }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("high_jump_motion_update_listener") {
            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    final S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
                    packet.setYaw(minecraft.thePlayer.rotationYaw);
                    packet.setYaw(minecraft.thePlayer.rotationPitch);

                }
            }

        });
        this.listeners.add(new Listener<MovePlayerEvent>("high_jump_motion_update_listener") {
            @Override
            public void call(MovePlayerEvent event) {
                double forward = minecraft.thePlayer.movementInput.moveForward;
                double strafe = minecraft.thePlayer.movementInput.moveStrafe;
                float yaw = minecraft.thePlayer.rotationYaw;
                if (forward == 0.0 && strafe == 0.0) {
                    event.setMotionX(0.0);
                    event.setMotionZ(0.0);
                }
                else {
                    if (forward != 0.0) {
                        if (strafe > 0.0) {
                            yaw += ((forward > 0.0) ? -45 : 45);
                        } else if (strafe < 0.0) {
                            yaw += ((forward > 0.0) ? 45 : -45);
                        }
                        strafe = 0.0;
                        if (forward > 0.0) {
                            forward = 1.0;
                        } else if (forward < 0.0) {
                            forward = -1.0;
                        }
                    }
                    event.setMotionX(forward * 0.25 * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * 0.25 * Math.sin(Math.toRadians(yaw + 90.0f)));
                    event.setMotionZ(forward * 0.25 * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * 0.25 * Math.cos(Math.toRadians(yaw + 90.0f)));
                }
            }
        });
    }
}
