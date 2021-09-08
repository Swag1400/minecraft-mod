package me.comu.client.module.impl.toggle.movement;

import me.comu.client.core.Gun;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;

import java.util.List;

public final class LongJump extends ToggleableModule {
    private final NumberProperty<Float> boost = new NumberProperty<>(4.48F, 1F, 20F, 1F, "Boost", "b");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.BOOST, "Mode", "m");

    private int stage, lastHDistance, airTicks, headStart, groundTicks;
    private double moveSpeed, lastDist;
    private boolean isSpeeding;

    public LongJump() {
        super("LongJump", new String[]{"longjump", "jump", "lj"}, 0xFF696969, ModuleType.MOVEMENT);
        this.offerProperties(boost, mode);
        this.listeners.add(new Listener<MovePlayerEvent>("long_jump_move_player_listener") {
            @Override
            public void call(MovePlayerEvent event) {
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
                if (sf.getValue()) {
                    setTag("LongJump \2477" + mode.getFixedValue());
                }
                if (mode.getValue() == Mode.DIREKT) {
                    return;
                }

                if (mode.getValue() == Mode.BOOST && ((minecraft.thePlayer.moveForward != 0.0F) || (minecraft.thePlayer.moveStrafing != 0.0F) && !PlayerHelper.isOnLiquid() && !PlayerHelper.isInLiquid())) {
                    if (stage == 0) {
                        minecraft.getTimer().timerSpeed = 1.0F;
                        moveSpeed = (boost.getValue() * getBaseMoveSpeed());
                    } else if (stage == 1) {
                        minecraft.thePlayer.motionY = 0.42D;
                        event.setMotionY(0.42D);
                        moveSpeed *= 2.149D;
                    } else if (stage == 2) {
                        double difference = 0.66D * (lastDist - getBaseMoveSpeed());
                        moveSpeed = (lastDist - difference);
                    } else {
                        moveSpeed = (lastDist - lastDist / 159.0D);
                    }

                    moveSpeed = Math.max(getBaseMoveSpeed(), moveSpeed);
                    setMoveSpeed(event, moveSpeed);
                    List collidingList = minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, minecraft.thePlayer.motionY, 0.0D));
                    List collidingList2 = minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, -0.4D, 0.0D));

                    if ((!minecraft.thePlayer.isCollidedVertically) && ((collidingList.size() > 0) || (collidingList2.size() > 0))) {
                        minecraft.thePlayer.motionY = -0.001D;
                        event.setMotionY(-0.001D);
                    }

                    stage += 1;
                } else if (stage > 0) {
                    setRunning(false);
                    minecraft.getTimer().timerSpeed = 1F;
                }
            }
        });
        this.listeners.add(new Listener<MotionUpdateEvent>("long_jump_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                switch (mode.getValue()) {
                    case JUMP:
                        Flight fl = (Flight) Gun.getInstance().getModuleManager().getModuleByAlias("flight");
                        Speed sp = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
                        if (minecraft.thePlayer.onGround && minecraft.thePlayer.isMoving() && !fl.isRunning() && !sp.isRunning()) {
                            minecraft.thePlayer.setSpeed(boost.getValue());
                            minecraft.thePlayer.motionY = 0.48;
                            minecraft.getTimer().timerSpeed = 10.0f;
                        } else {
                            minecraft.getTimer().timerSpeed = 1.0f;
                        }
                        if (minecraft.thePlayer.fallDistance >= 3.0f) {
                            minecraft.thePlayer.motionX = 0.0;
                            minecraft.thePlayer.motionZ = 0.0;
                        }
                        break;
                    case BOOST:
                        if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                            if ((minecraft.thePlayer.moveForward != 0.0F) || (minecraft.thePlayer.moveStrafing != 0.0F)) {
                                double xDist = minecraft.thePlayer.posX - minecraft.thePlayer.prevPosX;
                                double zDist = minecraft.thePlayer.posZ - minecraft.thePlayer.prevPosZ;
                                lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                            } else {
                                event.setCanceled(true);
                            }

                            break;
                        }
                 /*   case JUMP:
                    	 if (minecraft.thePlayer.onGround && PlayerHelper.isMoving() && !((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning() && !((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("speed")).isRunning())
                         {
                    		 minecraft.thePlayer.motionY = -4.994;
                    		 minecraft.getTimer().timerSpeed = 2.0f;
                         } else {
                        	 minecraft.getTimer().timerSpeed = 1.0f;
                         }
                        	 if (minecraft.thePlayer.fallDistance >= 3.0) {
                        		 minecraft.thePlayer.motionX = 0.0;
                        		 minecraft.thePlayer.motionZ = 0.0;
                        	 }
                         
                    	break;
                    	*/
                    case DIREKT: //  http://hastebin.com/yezemowame.avrasm || created by dylmil : ported to 1.8 - arabpvp
                        if (!PlayerHelper.isInLiquid() && !PlayerHelper.isOnLiquid()) {
                            if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                                if (minecraft.thePlayer.onGround) {
                                    lastHDistance = 0;
                                }

                                float direction = minecraft.thePlayer.rotationYaw + (minecraft.thePlayer.moveForward < 0 ? 180 : 0) + (minecraft.thePlayer.moveStrafing > 0 ? (-90 * (minecraft.thePlayer.moveForward < 0 ? -0.5F : (minecraft.thePlayer.moveForward > 0 ? 0.5F : 1))) : 0) - (minecraft.thePlayer.moveStrafing < 0 ? (-90 * (minecraft.thePlayer.moveForward < 0 ? -0.5F : (minecraft.thePlayer.moveForward > 0 ? 0.5F : 1))) : 0),
                                        xDir = (float) Math.cos((direction + 90) * Math.PI / 180),
                                        zDir = (float) Math.sin((direction + 90) * Math.PI / 180);

                                if (!minecraft.thePlayer.isCollidedVertically) {
                                    airTicks++;
                                    isSpeeding = true;

                                    if (minecraft.gameSettings.keyBindSneak.getIsKeyPressed()) {
                                        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(0, Integer.MAX_VALUE, 0, false));
                                    }

                                    groundTicks = 0;

                                    if (!minecraft.thePlayer.isCollidedVertically) {
                                        if (minecraft.thePlayer.motionY == -0.07190068807140403) {
                                            minecraft.thePlayer.motionY *= 0.35F;
                                        } else if (minecraft.thePlayer.motionY == -0.10306193759436909) {
                                            minecraft.thePlayer.motionY *= 0.55F;
                                        } else if (minecraft.thePlayer.motionY == -0.13395038817442878) {
                                            minecraft.thePlayer.motionY *= 0.67F;
                                        } else if (minecraft.thePlayer.motionY == -0.16635183030382) {
                                            minecraft.thePlayer.motionY *= 0.69F;
                                        } else if (minecraft.thePlayer.motionY == -0.19088711097794803) {
                                            minecraft.thePlayer.motionY *= 0.71F;
                                        } else if (minecraft.thePlayer.motionY == -0.21121925191528862) {
                                            minecraft.thePlayer.motionY *= 0.2F;
                                        } else if (minecraft.thePlayer.motionY == -0.11979897632390576) {
                                            minecraft.thePlayer.motionY *= 0.93F;
                                        } else if (minecraft.thePlayer.motionY == -0.18758479151225355) {
                                            minecraft.thePlayer.motionY *= 0.72F;
                                        } else if (minecraft.thePlayer.motionY == -0.21075983825251726) {
                                            minecraft.thePlayer.motionY *= 0.76F;
                                        }

                                        if (minecraft.thePlayer.motionY < -0.2 && minecraft.thePlayer.motionY > -0.24) {
                                            minecraft.thePlayer.motionY *= 0.7;
                                        }

                                        if (minecraft.thePlayer.motionY < -0.25 && minecraft.thePlayer.motionY > -0.32) {
                                            minecraft.thePlayer.motionY *= 0.8;
                                        }

                                        if (minecraft.thePlayer.motionY < -0.35 && minecraft.thePlayer.motionY > -0.8) {
                                            minecraft.thePlayer.motionY *= 0.98;
                                        }

                                        if (minecraft.thePlayer.motionY < -0.8 && minecraft.thePlayer.motionY > -1.6) {
                                            minecraft.thePlayer.motionY *= 0.99;
                                        }
                                    }

                                    minecraft.getTimer().timerSpeed = 0.5F;
                                    double[] speedVals = {0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.397060, 0.394525, 0.39200, 0.389400, 0.386440, 0.383655, 0.381105, 0.378670, 0.376250, 0.373840, 0.371450, 0.369000, 0.366600, 0.364200, 0.361800, 0.359450, 0.3570, 0.3540, 0.3510, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};

                                    if (minecraft.gameSettings.keyBindForward.pressed) {
                                        try {
                                            minecraft.thePlayer.motionX = (xDir * speedVals[airTicks - 1] * 3);
                                            minecraft.thePlayer.motionZ = (zDir * speedVals[airTicks - 1] * 3);
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            ;
                                        }
                                    } else {
                                        minecraft.thePlayer.motionX = 0;
                                        minecraft.thePlayer.motionZ = 0;
                                    }
                                } else {
                                    minecraft.getTimer().timerSpeed = 1F;
                                    airTicks = 0;
                                    groundTicks++;
                                    headStart--;
                                    minecraft.thePlayer.motionX /= 13;
                                    minecraft.thePlayer.motionZ /= 13;

                                    if (groundTicks == 1) {
                                        updatePosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
                                        updatePosition(minecraft.thePlayer.posX + 0.0624, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
                                        updatePosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + .419, minecraft.thePlayer.posZ);
                                        updatePosition(minecraft.thePlayer.posX + 0.0624, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
                                        updatePosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + .419, minecraft.thePlayer.posZ);
                                    } else if (groundTicks > 2) {
                                        groundTicks = 0;
                                        minecraft.thePlayer.motionX = (xDir * 0.3);
                                        minecraft.thePlayer.motionZ = (zDir * 0.3);
                                        minecraft.thePlayer.motionY = 0.424F;
                                    }
                                }
                            }
                        }
                }
            }
        });
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        headStart = 4;
        groundTicks = 0;
        stage = 0;
    }

    public void updatePosition(double x, double y, double z) {
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, minecraft.thePlayer.onGround));
    }

    public Block getBlock(BlockPos pos) {
        return minecraft.theWorld.getBlockState(pos).getBlock();
    }

    private double getDistance(EntityPlayer player, double distance) {
        @SuppressWarnings("unchecked") List<AxisAlignedBB> boundingBoxes = (List<AxisAlignedBB>) player.worldObj.getCollidingBoundingBoxes(player, player.getEntityBoundingBox().addCoord(0, -distance, 0));

        if (boundingBoxes.isEmpty()) {
            return 0.0;
        }

        double y = 0.0;

        for (AxisAlignedBB boundingBox : boundingBoxes)
            if (boundingBox.maxY > y) {
                y = boundingBox.maxY;
            }

        return player.posY - y;
    }

    private void setMoveSpeed(MovePlayerEvent event, double speed) {
        MovementInput movementInput = minecraft.thePlayer.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = minecraft.thePlayer.rotationYaw;

        if ((forward == 0.0D) && (strafe == 0.0D)) {
            event.setMotionX(0.0D);
            event.setMotionZ(0.0D);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;

                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            }

            event.setMotionX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setMotionZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;

        if (minecraft.thePlayer != null) {
            if (minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
                int amplifier = minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                baseSpeed *= (1D + 0.2D * (amplifier + 1));
            }
        }

        return baseSpeed;
    }

    public enum Mode {
        BOOST, DIREKT, JUMP
    }
}
