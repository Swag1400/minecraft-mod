package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.LocationUtils;
import net.minecraft.potion.Potion;

public final class GuardianHop extends ToggleableModule
{
	private double moveSpeed;
	private double lastDist;
	private int stage;
	

    public GuardianHop()
    {
        super("GuardianHop", new String[] {"GuardianHop", "ghop", "gh","gayhop","guardian-hop"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
      //   offerProperties(keepSprint);
         listeners.add(new Listener<MotionUpdateEvent>("sprint_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
            	minecraft.getTimer().timerSpeed = 1.06f;
            	if(minecraft.thePlayer.moveForward == 0.0 || minecraft.thePlayer.moveStrafing == 0.0) {
            		moveSpeed = getBaseMoveSpeed();
            	}
                if (stage == 1 && minecraft.thePlayer.isCollidedVertically && (minecraft.thePlayer.moveForward != 0.0 || minecraft.thePlayer.moveStrafing != 0.0)) {
                    moveSpeed = 1.51 + getBaseMoveSpeed();
                }
                if (stage == 2 && minecraft.thePlayer.isCollidedVertically && (minecraft.thePlayer.moveForward != 0.0f || minecraft.thePlayer.moveStrafing != 0.0f)) {
                    event.setPositionY(minecraft.thePlayer.motionY = 0.42);
                    moveSpeed *= 2.2;
                } 
                else if (stage == 3) {
                	if (minecraft.getCurrentServerData().serverIP.contains("veltpvp") || minecraft.getCurrentServerData().serverIP.contains("arcane")) {
                         moveSpeed =  lastDist - 0.66 * ( lastDist -  getBaseMoveSpeed());
                    }
                    else {
                         moveSpeed =  lastDist - 0.6 * ( lastDist -  getBaseMoveSpeed());
                	}
                } else {
                    if ((minecraft.theWorld.getCollidingBoundingBoxes( minecraft.thePlayer,  minecraft.thePlayer.boundingBox.offset(0.0,  minecraft.thePlayer.motionY, 0.0)).size() > 0 ||  minecraft.thePlayer.isCollidedVertically) &&  stage > 0) {
                         stage = (( minecraft.thePlayer.moveForward != 0.0 ||  minecraft.thePlayer.moveStrafing != 0.0) ? 1 : 0);
                    }
                    moveSpeed = lastDist - lastDist / 180.0;
                }
                 moveSpeed = Math.max( moveSpeed,  getBaseMoveSpeed());
                if ( stage > 0) {
                    double forward =  minecraft.thePlayer.movementInput.moveForward;
                    double strafe =  minecraft.thePlayer.movementInput.moveStrafe;
                    float yaw =  minecraft.thePlayer.rotationYaw;
                    if (forward == 0.0 && strafe == 0.0) {
                        event.setPositionX(0.0);
                        event.setPositionZ(0.0);
                    }
                    else {
                            if (forward != 0.0) {
                                if (strafe > 0.0) {
                                    yaw += ((forward > 0.0) ? -45 : 45);
                                }
                                else if (strafe < 0.0) {
                                    yaw += ((forward > 0.0) ? 45 : -45);
                                }
                                strafe = 0.0;
                                if (forward > 0.0) {
                                    forward = 1.0;
                                }
                                else if (forward < 0.0) {
                                    forward = -1.0;
                                }
                                }
                            event.setPositionX(forward *  moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe *  moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f)));
                            event.setPositionZ(forward *  moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe *  moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f)));
                    }
                }
                if (minecraft.thePlayer.moveForward != 0.0 || minecraft.thePlayer.moveStrafing != 0.0) {
                	++stage;
                }
            }
        });
        this.listeners.add(new Listener<MotionUpdateEvent>("no_slow_block_soul_sand_slowdown_listener")
         {
			@Override
			public void call(MotionUpdateEvent event) {
		        if (!minecraft.thePlayer.onGround && minecraft.getCurrentServerData().serverIP.contains("veltpvp") || minecraft.getCurrentServerData().serverIP.contains("arcane")) {
		            minecraft.thePlayer.setLocation(new LocationUtils(minecraft.thePlayer.posX, (minecraft.thePlayer.ticksExisted % 2 == 0) ? (minecraft.thePlayer.posY + 0.01) : (minecraft.thePlayer.posY - 0.01), minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch));
		        }
		        if (event.getTime() == Time.BEFORE) {
		            final double xDist = minecraft.thePlayer.posX - minecraft.thePlayer.prevPosX;
		            final double zDist = minecraft.thePlayer.posZ - minecraft.thePlayer.prevPosZ;
		            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
		        }
			}
			});

    }
    @Override
    public void onEnable() {
    	if (minecraft.thePlayer != null) {
    		moveSpeed = getBaseMoveSpeed();
    	}
    	lastDist = 0.0;
    	stage = 3;
    	minecraft.getTimer().timerSpeed = 1.0f;
    }
    @Override
    public void onDisable() {
    	minecraft.getTimer().timerSpeed = 1.0f;
    }
    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

}
