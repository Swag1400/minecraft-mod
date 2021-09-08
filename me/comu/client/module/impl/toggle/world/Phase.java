package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.AirBobbingEvent;
import me.comu.client.events.BlockBoundingBoxEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.PushOutOfBlocksEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public final class Phase extends ToggleableModule
{
    private final Property<Boolean> slow = new Property<>(false, "Slow", "s");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.PACKET, "Mode", "m");

    private double distance = 1.2D;
    private final Stopwatch stopwatch = new Stopwatch();
    private boolean zoomies = false;
    private int delay;

    public Phase()
    {
        super("Phase", new String[] {"phase", "noclip"}, 0xFFCC9ED9, ModuleType.WORLD);
        this.offerProperties(mode, slow);
        this.listeners.add(new Listener<MovePlayerEvent>("phase_move_player_listener")
        {
     
            @Override
            public void call(MovePlayerEvent event)
            {
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
            	if (sf.getValue()) {
                setTag(String.format("Phase \2477" + mode.getFixedValue()));
            	}

                if (slow.getValue())
                {
                    event.setMotionX(event.getMotionX() * 0.3D);
                    event.setMotionZ(event.getMotionZ() * 0.3D);
                }
            }
        });
        this.listeners.add(new Listener<MotionUpdateEvent>("phase_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                float direction = minecraft.thePlayer.rotationYaw;

                if (minecraft.thePlayer.moveForward < 0F)
                {
                    direction += 180F;
                             }
                

                if (minecraft.thePlayer.moveStrafing > 0F)
                {
                    direction -= 90F * (minecraft.thePlayer.moveForward < 0F ? -0.5F : minecraft.thePlayer.moveForward > 0F ? 0.5F : 1F);
                }

                if (minecraft.thePlayer.moveStrafing < 0F)   // what else is broke
                {
                    direction += 90F * (minecraft.thePlayer.moveForward < 0F ? -0.5F : minecraft.thePlayer.moveForward > 0F ? 0.5F : 1F);
                }

                double x = Math.cos(Math.toRadians(direction + 90)) * 0.31D;
                double z = Math.sin(Math.toRadians(direction + 90)) * 0.31D;
                double ix = minecraft.getRenderViewEntity().getDirectionFacing().getDirectionVec().getX() * 0.1D;
                double iz = minecraft.getRenderViewEntity().getDirectionFacing().getDirectionVec().getZ() * 0.1D;

                switch (mode.getValue())
                {
                    case MOTORBOAT:
                        if (minecraft.thePlayer.isSneaking() && event.getTime() == MotionUpdateEvent.Time.BEFORE)
                        {
                            minecraft.thePlayer.setBoundingBox((minecraft.thePlayer.getEntityBoundingBox().offset(x, 0, z)));
                        }

                        break;

                    case SAND:
                        minecraft.thePlayer.motionY = 0D;

                        if (minecraft.gameSettings.keyBindJump.getIsKeyPressed())
                        {
                            minecraft.thePlayer.motionY = 0.3D;
                        }
                        else if (minecraft.gameSettings.keyBindSneak.getIsKeyPressed())
                        {
                            minecraft.thePlayer.motionY = -0.3D;
                        }

                        break;

                    case HCF:
                    	
                        if (minecraft.thePlayer.isSneaking() && event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                            if ((isInsideBlock()) && (minecraft.thePlayer.isSneaking()))
                            {
                              float yaw = minecraft.thePlayer.rotationYaw;
                              minecraft.thePlayer.boundingBox.offsetAndUpdate(distance * Math.cos(Math.toRadians(yaw + 90.0F)), 0.0D, distance * Math.sin(Math.toRadians(yaw + 90.0F)));
                            }
                          }

                        break;

         

                    case PARA:
                        minecraft.thePlayer.motionY = 0D;

                        if (minecraft.thePlayer.isCollidedHorizontally && !PlayerHelper.isInsideBlock())
                            if (PlayerHelper.getFacingWithProperCapitals().equalsIgnoreCase("EAST"))
                            {
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX + 0.5, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                                minecraft.thePlayer.setPosition(minecraft.thePlayer.posX + 1, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.POSITIVE_INFINITY, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                            }
                            else if (PlayerHelper.getFacingWithProperCapitals().equalsIgnoreCase("WEST"))
                            {
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX - 0.5, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                                minecraft.thePlayer.setPosition(minecraft.thePlayer.posX - 1, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.POSITIVE_INFINITY, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                            }
                            else if (PlayerHelper.getFacingWithProperCapitals().equalsIgnoreCase("NORTH"))
                            {
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ - 0.5, minecraft.thePlayer.onGround));
                                minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ - 1);
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.POSITIVE_INFINITY, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                            }
                            else if (PlayerHelper.getFacingWithProperCapitals().equalsIgnoreCase("SOUTH"))
                            {
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ + 0.5, minecraft.thePlayer.onGround));
                                minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ + 1);
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.POSITIVE_INFINITY, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                            }

                        break;

                    case VERTICAL:
                        switch (event.getTime())
                        {
                            case AFTER:
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 0.05D, minecraft.thePlayer.posZ, true));

                                if (!minecraft.thePlayer.isCollidedVertically && PlayerHelper.isInsideBlock())
                                {
                                    setRunning(false);
                                }

                                break;
                        }

                    case SKIP:
                        if (stopwatch.hasCompleted(150) && minecraft.thePlayer.isCollidedHorizontally)
                        {
                            double[] yOffsets = new double[] { -0.02500000037252903, -0.028571428997176036, -0.033333333830038704, -0.04000000059604645, -0.05000000074505806, -0.06666666766007741, -0.10000000149011612, -0.20000000298023224, -0.04000000059604645, -0.033333333830038704, -0.028571428997176036, -0.02500000037252903};

                            for (int index = 0; index < yOffsets.length; index++)
                            {
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + yOffsets[index], minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX + (x * index), minecraft.thePlayer.posY, minecraft.thePlayer.posZ + (z * index), minecraft.thePlayer.onGround));
                            }
                        }

                        break;
                    case TEST:
                        for (int i = 5; i <= 5; i++) {
                            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX + 5, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround));
                            stopwatch.reset();
                        }
                        break;
                    case NUF:
                        if (!minecraft.thePlayer.isOnLadder())
                        {
                            minecraft.thePlayer.motionY = 0.0D;
                            double xOff;
                            double zOff;
                            double mx = Math.cos(Math.toRadians(minecraft.thePlayer.rotationYaw + 90.0F));
                            double mz = Math.sin(Math.toRadians(minecraft.thePlayer.rotationYaw + 90.0F));
                            xOff = minecraft.thePlayer.movementInput.moveForward * 0.152D * mx + minecraft.thePlayer.movementInput.moveStrafe * 0.152D * mz;
                            zOff = minecraft.thePlayer.movementInput.moveForward * 0.152D * mz - minecraft.thePlayer.movementInput.moveStrafe * 0.152D * mx;
                            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX + minecraft.thePlayer.motionX * 11.0D + xOff, minecraft.thePlayer.posY + (minecraft.gameSettings.keyBindJump.isPressed() ? 0.0624D : zoomies ? 0.0625D : 1.0E-8D) - (minecraft.gameSettings.keyBindSneak.isPressed() ? 0.0624D : zoomies ? 0.0625D : 2.0E-8D), minecraft.thePlayer.posZ + minecraft.thePlayer.motionZ * 11.0D + zOff, false));
                            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX + minecraft.thePlayer.motionX * 11.0D + xOff, 1337.0D + minecraft.thePlayer.posY, minecraft.thePlayer.posZ + minecraft.thePlayer.motionZ * 11.0D + zOff, false));
                            minecraft.thePlayer.setPositionAndUpdate(minecraft.thePlayer.posX + xOff, minecraft.thePlayer.posY, minecraft.thePlayer.posZ + zOff);
                            zoomies = (!zoomies);
                        }

                        break;


                    case PACKET: //Hypnomemer //
                        if (event.getTime().equals(MotionUpdateEvent.Time.AFTER) && minecraft.thePlayer.isCollidedHorizontally)
                        {
                            if (!minecraft.thePlayer.isOnLadder())
                            {
                                double xOff1;
                                double zOff1;
                                double multiplier1 = 0.3D;
                                double mx1 = Math.cos(Math.toRadians(minecraft.thePlayer.rotationYaw + 90.0F));
                                double mz1 = Math.sin(Math.toRadians(minecraft.thePlayer.rotationYaw + 90.0F));
                                xOff1 = minecraft.thePlayer.movementInput.moveForward * multiplier1 * mx1 + minecraft.thePlayer.movementInput.moveStrafe * multiplier1 * mz1;
                                zOff1 = minecraft.thePlayer.movementInput.moveForward * multiplier1 * mz1 - minecraft.thePlayer.movementInput.moveStrafe * multiplier1 * mx1;
                                minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX + xOff1, minecraft.thePlayer.posY, minecraft.thePlayer.posZ + zOff1, false));

                                for (int i = 1; i < 10; i++)
                                {
                                    minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, 8.988465674311579E307D, minecraft.thePlayer.posZ, false));
                                }

                                minecraft.thePlayer.setPosition(minecraft.thePlayer.posX + xOff1, minecraft.thePlayer.posY, minecraft.thePlayer.posZ + zOff1);
                            }

                            return;
                        }

                        break;
                }
            }
        });
        this.listeners.add(new Listener<AirBobbingEvent>("phase_air_bobbing_listener")
        {
            @Override
            public void call(AirBobbingEvent event)
            {
                event.setCanceled(true);
            }
        });
        this.listeners.add(new Listener<PushOutOfBlocksEvent>("phase_push_out_of_blocks_listener")
        {
            @Override
            public void call(PushOutOfBlocksEvent event)
            {
                event.setCanceled(true);
            }
        });
        this.listeners.add(new Listener<BlockBoundingBoxEvent>("phase_block_bounding_box_listener")
        {
            @Override
            public void call(BlockBoundingBoxEvent event)
            {
                switch (mode.getValue())
                {
                    case MOTORBOAT:
                        if (event.getBoundingBox() != null && event.getBoundingBox().maxY > minecraft.thePlayer.getEntityBoundingBox().minY && minecraft.thePlayer.isSneaking())
                        {
                            event.setBoundingBox(null);
                        }

                        break;

                    case PACKET:
                        if ((PlayerHelper.isInsideBlock()) && minecraft.thePlayer.isCollidedHorizontally && (event.getBoundingBox() != null) && (event.getBoundingBox().maxY > minecraft.thePlayer.getEntityBoundingBox().minY))
                        {
                            event.setBoundingBox(null);
                        }

                        break;

                    case SAND:
                        if (PlayerHelper.isInsideBlock() && event.getBoundingBox() != null)
                        {
                            event.setBoundingBox(null);
                        }

                        break;

                    case PARA:
                    case NUF:
                        if ((event.getBoundingBox() != null) && (event.getBoundingBox().maxY > minecraft.thePlayer.getEntityBoundingBox().minY))
                        {
                            event.setBoundingBox(null);
                        }


                        // if ((PlayerHelper.isInsideBlock()) && minecraft.thePlayer.isCollidedHorizontally && (event.getBoundingBox() != null) && (event.getBoundingBox().maxY > minecraft.thePlayer.getEntityBoundingBox().minY)) {
                        // event.setBoundingBox(null);
                        break;
                    case TEST:
                        if (event.getBoundingBox() != null && minecraft.thePlayer.isCollided)
                            event.setBoundingBox(null);
                        break;
                    case VERTICAL:
                        event.setBoundingBox(null);
                        break;

                    case JUMP:
                        if ((PlayerHelper.isInsideBlock() && minecraft.gameSettings.keyBindJump.pressed) || (!PlayerHelper.isInsideBlock() && event.getBoundingBox() != null && event.getBoundingBox().maxY > minecraft.thePlayer.boundingBox.minY && minecraft.thePlayer.isSneaking()))
                        {
                            event.setBoundingBox(null);
                        }

                        break;


                    case HCF:
                        if ((event.getBoundingBox() != null) && (event.getBoundingBox().maxY > minecraft.thePlayer.boundingBox.minY) && (minecraft.thePlayer.isSneaking())) {
                            event.setBoundingBox(null);
                          }
                }
            }
        });
    }
    public float getDirection() {
        float yaw = minecraft.thePlayer.rotationYaw;
        yaw = MathHelper.wrapAngleTo180_float(yaw);
        return yaw * 0.017453292f;
    }
    private boolean isInsideBlock()
    {
      for (int x = MathHelper.floor_double(minecraft.thePlayer.boundingBox.minX); x < MathHelper.floor_double(minecraft.thePlayer.boundingBox.maxX) + 1; x++) {
        for (int y = MathHelper.floor_double(minecraft.thePlayer.boundingBox.minY); y < MathHelper.floor_double(minecraft.thePlayer.boundingBox.maxY) + 1; y++) {
          for (int z = MathHelper.floor_double(minecraft.thePlayer.boundingBox.minZ); z < MathHelper.floor_double(minecraft.thePlayer.boundingBox.maxZ) + 1; z++)
          {
            Block block = minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
            if ((block != null) && (!(block instanceof BlockAir)))
            {
              AxisAlignedBB boundingBox = block.getCollisionBoundingBox(minecraft.theWorld, new BlockPos(x, y, z), minecraft.theWorld.getBlockState(new BlockPos(x, y, z)));
              if ((block instanceof BlockHopper)) {
                boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
              }
              if (boundingBox != null) {
                if (minecraft.thePlayer.boundingBox.intersectsWith(boundingBox)) {
                  return true;
                }
              }
            }
          }
        }
      }
      return false;
    }

    public enum Mode
    {
        SAND, PARA, VERTICAL, SKIP, PACKET, NUF, MOTORBOAT, JUMP, HCF, TEST
    }
}
