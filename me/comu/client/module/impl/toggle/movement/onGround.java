package me.comu.client.module.impl.toggle.movement;
/** package me.comu.exeter.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import net.minecraft.util.MathHelper;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.exeter.core.Exeter;
import me.comu.exeter.events.BlockSoulSandSlowdownEvent;
import me.comu.exeter.events.ItemInUseEvent;
import me.comu.exeter.events.MotionUpdateEvent;
import me.comu.exeter.module.ModuleType;
import me.comu.exeter.module.ToggleableModule;
import me.comu.exeter.module.impl.toggle.combat.AutoHeal;
import me.comu.exeter.properties.EnumProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;

public final class onGround extends ToggleableModule {
    public static Minecraft mc;
    public static int ticks;
    public static double moveSpeed;
    private double lastDist;
    private double save;
    public static boolean canStep;
    private boolean stop;
    public static boolean boost;
     mc = Minecraft.getMinecraft();
     boost = false;

    public onGround() {
        super("onGround", new String[]{"onGround", "og"}, ModuleType.MOVEMENT);
        this.listeners.add(new Listener<MotionUpdateEvent>("on_ground_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
            	if (mc.thePlayer.onGround) {
                    ticks = 2;
                }
                if (MathUtils.roundToPlace(mc.thePlayer.posY - (int)mc.thePlayer.posY, 3) == MathUtils.roundToPlace(0.138, 3)) {
                    final EntityPlayerSP thePlayer = mc.thePlayer;
                    thePlayer.motionY -= 0.08;
                    event.y -= 0.09316090325960147;
                    final EntityPlayerSP thePlayer2 = mc.thePlayer;
                    thePlayer2.posY -= 0.09316090325960147;
                }
                if (ticks == 1 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                    ticks = 2;
                    moveSpeed = 1.35 * getBaseMoveSpeed() - 0.01;
                }
                else if (ticks == 2) {
                    if (stop) {
                        moveSpeed = 0.35;
                        stop = false;
                    }
                    moveSpeed *= 0.9;
                    ticks = 3;
                    if ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) && !mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.motionY = 0.399399995003033;
                        event.y = 0.399399995003033;
                        moveSpeed *= (boost ? 2.4 : 2.385);
                        final EntityPlayerSP thePlayer3 = mc.thePlayer;
                        thePlayer3.motionY *= -5.0;
                        boost = false;
                    }
                }
                else if (ticks == 3) {
                    ticks = 4;
                    final double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                    moveSpeed = lastDist - difference;
                }
                else if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically) {
                    ticks = 1;
                }
                moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                final MovementInput movementInput = mc.thePlayer.movementInput;
                float forward = movementInput.moveForward;
                float strafe = movementInput.moveStrafe;
                float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
                if (forward == 0.0f && strafe == 0.0f) {
                    event.x = 0.0;
                    event.z = 0.0;
                }
                else if (forward != 0.0f) {
                    if (strafe >= 1.0f) {
                        yaw += ((forward > 0.0f) ? -45 : 45);
                        strafe = 0.0f;
                    }
                    else if (strafe <= -1.0f) {
                        yaw += ((forward > 0.0f) ? 45 : -45);
                        strafe = 0.0f;
                    }
                    if (forward > 0.0f) {
                        forward = 1.0f;
                    }
                    else if (forward < 0.0f) {
                        forward = -1.0f;
                    }
                }
                final double mx = Math.cos(Math.toRadians(yaw + 90.0f));
                final double mz = Math.sin(Math.toRadians(yaw + 90.0f));
                final double motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
                final double motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
                event.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
                event.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
                canStep = true;
                mc.thePlayer.stepHeight = 0.6f;
                if (forward == 0.0f && strafe == 0.0f) {
                    event.x = 0.0;
                    event.z = 0.0;
                }
                else {
                    boolean collideCheck = false;
                    if (Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.expand(0.5, 0.0, 0.5)).size() > 0) {
                        collideCheck = true;
                    }
                    if (forward != 0.0f) {
                        if (strafe >= 1.0f) {
                            yaw += ((forward > 0.0f) ? -45 : 45);
                            strafe = 0.0f;
                        }
                        else if (strafe <= -1.0f) {
                            yaw += ((forward > 0.0f) ? 45 : -45);
                            strafe = 0.0f;
                        }
                        if (forward > 0.0f) {
                            forward = 1.0f;
                        }
                        else if (forward < 0.0f) {
                            forward = -1.0f;
                        }
    }
};
            }
        });
    }
}

**/
