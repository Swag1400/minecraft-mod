package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.module.impl.toggle.world.Phase;
import me.comu.client.notification.Notification;
import me.comu.client.notification.NotificationManager;
import me.comu.client.notification.NotificationType;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class Speed extends ToggleableModule {
    private final NumberProperty<Float> timer = new NumberProperty<>(1.0F, 0.1F, 10F, 0.1F, "Timer", "Timerspeed", "t");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.HOP, "Mode", "m");
    private final Property<Boolean> ice = new Property<>(true, "Ice", "i"),
            depthstrider = new Property<>(true, "DepthStrider", "ds"), lagback = new Property<>(true, "Lagback", "lag");

    private final Stopwatch stopwatch = new Stopwatch();

    private int stage, cooldownHops, ticks = 0;
    private boolean isSpeeding = false, wasOnWater;
    private double moveSpeed, lastDist, speed, slow;
    private boolean stop;
    private boolean speed2;
    public static boolean boost;
    private double save;
    public static boolean canStep;
    private boolean speedTick;


    public Speed() {
        super("Speed", new String[]{"speed", "fastrun", "swiftness"}, 0xFF5DCF6E, ModuleType.MOVEMENT);
        offerProperties(mode, ice, depthstrider, timer, lagback);
        listeners.add(new Listener<MotionUpdateEvent>("speed_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
                if (sf.getValue()) {
                    setTag(String.format("Swiftness \2477" + mode.getFixedValue()));
                }
                // TextGUI tg = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("look");
                //     EnumProperty<Look> modeL = (EnumProperty) tg.getPropertyByAlias("Look");
                Phase phase = (Phase) Gun.getInstance().getModuleManager().getModuleByAlias("phase");
                //if (modeL.getValue() == Look.DEFAULT) {
                //setTag(String.format("Speed §7" + String.format(StringUtils.capitalize(mode.getValue().toString()))));


                //    }
                if (phase != null && phase.isRunning()) {
                    return;
                }

                if (mode.getValue() == Mode.OFFSET && stage == 2 && PlayerHelper.isMoving()) {
                    event.setPositionY(event.getPositionY() + 0.4D);
                }

                if (mode.getValue() == Mode.YPORT && stage == 3) {
                    event.setPositionY(event.getPositionY() + 0.4D);
                }

                if (mode.getValue() == Mode.HOP || mode.getValue() == Mode.NCPHOP2 || mode.getValue() == Mode.NCPHOP || mode.getValue() == Mode.VHOP || mode.getValue() == Mode.VHOP2 || mode.getValue() == Mode.TEST || mode.getValue() == Mode.JUMP || mode.getValue() == Mode.YPORT) {
                    double xDist = minecraft.thePlayer.posX - minecraft.thePlayer.prevPosX;
                    double zDist = minecraft.thePlayer.posZ - minecraft.thePlayer.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                }

                if (mode.getValue() == Mode.SHOTBOW) {
                    if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                        switch (stage) {
                            case 1:
                                event.setPositionY(event.getPositionY() + 1.0E-4D);
                                stage += 1;
                                break;

                            case 2:
                                event.setPositionY(event.getPositionY() + 2.0E-4D);
                                stage += 1;
                                break;

                            default:
                                stage = 1;

                                if ((!minecraft.thePlayer.isSneaking()) && ((minecraft.thePlayer.moveForward != 0.0F) || (minecraft.thePlayer.moveStrafing != 0.0F)) && (!minecraft.gameSettings.keyBindJump.isPressed())) {
                                    stage = 1;
                                } else {
                                    moveSpeed = getBaseMoveSpeed();
                                }

                                break;
                        }
                    }
                }
            }
        });
        {
            listeners.add(new Listener<MovePlayerEvent>("speed_move_player_listener") {
                @Override
                public void call(MovePlayerEvent event) {
                    Phase phase = (Phase) Gun.getInstance().getModuleManager().getModuleByAlias("phase");

                    if (phase != null && phase.isRunning()) {
                        return;
                    }

                    if (ice.getValue()) {
                        Blocks.ice.slipperiness = 0.6F;
                        Blocks.packed_ice.slipperiness = 0.6F;
                    }

                    if (depthstrider.getValue() && PlayerHelper.isInLiquid()) {
                        ticks++;

                        if (ticks == 4) {
                            setMoveSpeed(event, 0.4);
                        }

                        if (ticks >= 5) {
                            setMoveSpeed(event, 0.3);
                            ticks = 0;
                        }
                    }

                    float moveForward = minecraft.thePlayer.movementInput.moveForward;
                    float moveStrafe = minecraft.thePlayer.movementInput.moveStrafe;
                    float rotationYaw = minecraft.thePlayer.rotationYaw;

                    switch (mode.getValue()) {
                        case HOP:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }

                            if (minecraft.thePlayer.onGround) {
                                stage = 2;
                                minecraft.getTimer().timerSpeed = 1F;
                            }

                            if (round(minecraft.thePlayer.posY - (int) minecraft.thePlayer.posY, 3) == round(0.138D, 3)) {
                                minecraft.thePlayer.motionY -= 0.13D;
                                event.setMotionY(event.getMotionY() - 0.13D);
                                minecraft.thePlayer.posY -= 0.13D;
                            }

                            if ((stage == 1) && (PlayerHelper.isMoving())) {
                                stage = 2;
                                moveSpeed = (1.35D * getBaseMoveSpeed() - 0.01D);
                            } else if (stage == 2) {
                                stage = 3;

                                if (PlayerHelper.isMoving()) {
                                    minecraft.thePlayer.motionY = 0.4D;
                                    event.setMotionY(0.4D);

                                    if (cooldownHops > 0) {
                                        cooldownHops -= 1;
                                    }

                                    moveSpeed *= 2.149D;
                                }
                            } else if (stage == 3) {
                                stage = 4;
                                double difference = 0.66D * (lastDist - getBaseMoveSpeed());
                                moveSpeed = (lastDist - difference);
                            } else {
                                if ((minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0D, minecraft.thePlayer.motionY, 0D)).size() > 0) || (minecraft.thePlayer.isCollidedVertically)) {
                                    stage = 1;
                                }

                                moveSpeed = (lastDist - lastDist / 159D);
                            }

                            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());

                            if ((moveForward == 0F) && (moveStrafe == 0F)) {
                                event.setMotionX(0D);
                                event.setMotionZ(0D);
                                moveSpeed = 0D;
                            } else if (moveForward != 0F) {
                                if (moveStrafe >= 1F) {
                                    rotationYaw += (moveForward > 0F ? -45F : 45F);
                                    moveStrafe = 0F;
                                } else if (moveStrafe <= -1F) {
                                    rotationYaw += (moveForward > 0F ? 45F : -45F);
                                    moveStrafe = 0F;
                                }

                                if (moveForward > 0F) {
                                    moveForward = 1F;
                                } else if (moveForward < 0F) {
                                    moveForward = -1F;
                                }
                            }

                            double motionX = Math.cos(Math.toRadians(rotationYaw + 90F));
                            double motionZ = Math.sin(Math.toRadians(rotationYaw + 90F));

                            if (cooldownHops == 0) {
                                event.setMotionX(moveForward * moveSpeed * motionX + moveStrafe * moveSpeed * motionZ);
                                event.setMotionZ(moveForward * moveSpeed * motionZ - moveStrafe * moveSpeed * motionX);
                            }

                            minecraft.thePlayer.stepHeight = 0.6F;

                            if ((moveForward == 0F) && (moveStrafe == 0F)) {
                                event.setMotionX(0D);
                                event.setMotionZ(0D);
                            }

                            break;

                        case NCPHOP:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }

                            if (minecraft.thePlayer.onGround) {
                                stage = 2;
                                minecraft.getTimer().timerSpeed = 1F;
                            }

                            if (round(minecraft.thePlayer.posY - (int) minecraft.thePlayer.posY, 3) == round(0.138D, 3)) {
                                minecraft.thePlayer.motionY -= 0.28D;
                                event.setMotionY(event.getMotionY() - 0.0931D);
                                minecraft.thePlayer.posY -= 0.0931D;
                            }

                            if ((stage == 1) && (PlayerHelper.isMoving())) {
                                stage = 2;
                                moveSpeed = (1.35D * getBaseMoveSpeed() - 0.01D);
                            } else if (stage == 2) {
                                stage = 3;

                                if (PlayerHelper.isMoving()) {
                                    minecraft.thePlayer.motionY = 0.4D;
                                    event.setMotionY(0.4D);

                                    if (cooldownHops > 0) {
                                        cooldownHops -= 1;
                                    }

                                    moveSpeed *= 1.5D;
                                }
                            } else if (stage == 3.0D) {
                                stage = 4;
                                double difference = 0.66D * (lastDist - getBaseMoveSpeed());
                                moveSpeed = (lastDist - difference);
                            } else {
                                if ((minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0D, minecraft.thePlayer.motionY, 0D)).size() > 0) || (minecraft.thePlayer.isCollidedVertically)) {
                                    stage = 1;
                                }

                                moveSpeed = (lastDist - lastDist / 159D);
                            }

                            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());

                            if ((moveForward == 0F) && (moveStrafe == 0F)) {
                                event.setMotionX(0D);
                                event.setMotionZ(0D);
                                moveSpeed = 0D;
                            } else if (moveForward != 0F) {
                                if (moveStrafe >= 1F) {
                                    rotationYaw += (moveForward > 0F ? -45F : 45F);
                                    moveStrafe = 0F;
                                } else if (moveStrafe <= -1F) {
                                    rotationYaw += (moveForward > 0F ? 45F : -45F);
                                    moveStrafe = 0F;
                                }

                                if (moveForward > 0F) {
                                    moveForward = 1F;
                                } else if (moveForward < 0F) {
                                    moveForward = -1F;
                                }
                            }

                            double motionXNCP = Math.cos(Math.toRadians(rotationYaw + 90F));
                            double motionZNCP = Math.sin(Math.toRadians(rotationYaw + 90F));

                            if (cooldownHops == 0) {
                                event.setMotionX(moveForward * moveSpeed * motionXNCP + moveStrafe * moveSpeed * motionZNCP);
                                event.setMotionZ(moveForward * moveSpeed * motionZNCP - moveStrafe * moveSpeed * motionXNCP);
                            }

                            minecraft.thePlayer.stepHeight = 0.6F;

                            if ((moveForward == 0F) && (moveStrafe == 0F)) {
                                event.setMotionX(0D);
                                event.setMotionZ(0D);
                            }

                            break;
                        case VHOP:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }
                            if (Helper.player().moveForward == 0.0f && Helper.player().moveStrafing == 0.0f) {
                                moveSpeed = getBaseMoveSpeed();
                            }
                            if (MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.4, 3)) {
                                event.setMotionY(Helper.player().motionY = 0.31);
                            } else if (MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.71, 3)) {
                                event.setMotionY(Helper.player().motionY = 0.04);
                            } else if (MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.75, 3)) {
                                event.setMotionY(Helper.player().motionY = -0.2);
                            }
                            List collidingList = ClientUtils.world().getCollidingBoundingBoxes(Helper.player(), Helper.player().boundingBox.offset(0.0, -0.56, 0.0));
                            if (collidingList.size() > 0 && MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.55, 3)) {
                                event.setMotionY(Helper.player().motionY = -0.14);
                            }
                            if (stage == 1 && Helper.player().isCollidedVertically && (Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f)) {
                                moveSpeed = 2.0 * getBaseMoveSpeed() - 0.4;
                            } else if (stage == 2 && Helper.player().isCollidedVertically && (Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f)) {
                                event.setMotionY(Helper.player().motionY = 0.4);
                                moveSpeed *= 2.149;
                            } else if (stage == 3) {
                                final double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                                moveSpeed = lastDist - difference;
                            } else {
                                collidingList = ClientUtils.world().getCollidingBoundingBoxes(Helper.player(), Helper.player().boundingBox.offset(0.0, Helper.player().motionY, 0.0));
                                if ((collidingList.size() > 0 || Helper.player().isCollidedVertically) && stage > 0) {
                                    if (1.35 * getBaseMoveSpeed() - 0.01 > moveSpeed) {
                                        stage = 0;
                                    } else {
                                        stage = ((Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f) ? 1 : 0);
                                    }
                                }
                                moveSpeed = lastDist - lastDist / 159.0;
                            }
                            if (stage > 8) {
                                moveSpeed = getBaseMoveSpeed();
                            }
                            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                            if (stage > 0) {
                                setMoveSpeed(event, moveSpeed);
                            }
                            if (Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f) {
                                ++stage;
                            }


                            break;

                        // TODO: fix yport
                        case VANILLA:
                            minecraft.thePlayer.motionX *= 1.5;
                            minecraft.thePlayer.motionZ *= 1.5;
                            break;
//                       case   : // fast hop for uni
//                           if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning())
//                           {
//                               return;
//                           }
//
//                           if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock()))
//                           {
//                               moveSpeed = 0D;
//                               wasOnWater = true;
//                               return;
//                           }
//
//                           if (minecraft.thePlayer.onGround)
//                           {
//                               stage = 2;
//                               minecraft.getTimer().timerSpeed = 1F;
//                           }
//
//                           if (round(minecraft.thePlayer.posY - (int) minecraft.thePlayer.posY, 3) == round(0.138D, 3))
//                           {
//                               minecraft.thePlayer.motionY -= 0.13D;
//                               event.setMotionY(event.getMotionY() - 0.13D);
//                               minecraft.thePlayer.posY -= 0.13D;
//                           }
//
//                           if ((stage == 1) && (PlayerHelper.isMoving()))
//                           {
//                               stage = 2;
//                               moveSpeed = (12.15D * getBaseMoveSpeed() - 0.01D);
//                           }
//                           else if (stage == 2)
//                           {
//                               stage = 3;
//
//                               if (PlayerHelper.isMoving())
//                               {
//                                   minecraft.thePlayer.motionY = 0.4D;
//                                   event.setMotionY(0.4D);
//
//                                   if (cooldownHops > 0)
//                                   {
//                                       cooldownHops -= 1;
//                                   }
//
//                                   moveSpeed *= 2.649D;
//                               }
//                           }
//                           else if (stage == 3)
//                           {
//                               stage = 4;
//                               double difference = 0.66D * (lastDist - getBaseMoveSpeed());
//                               moveSpeed = (lastDist - difference);
//                           }
//                           else
//                           {
//                               if ((minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0D, minecraft.thePlayer.motionY, 0D)).size() > 0) || (minecraft.thePlayer.isCollidedVertically))
//                               {
//                                   stage = 1;
//                               }
//
//                               moveSpeed = (lastDist - lastDist / 159D);
//                           }
//
//                           moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
//
//                           if ((moveForward == 0F) && (moveStrafe == 0F))
//                           {
//                               event.setMotionX(0D);
//                               event.setMotionZ(0D);
//                               moveSpeed = 0D;
//                           }
//                           else if (moveForward != 0F)
//                           {
//                               if (moveStrafe >= 1F)
//                               {
//                                   rotationYaw += (moveForward > 0F ? -45F : 45F);
//                                   moveStrafe = 0F;
//                               }
//                               else if (moveStrafe <= -1F)
//                               {
//                                   rotationYaw += (moveForward > 0F ? 45F : -45F);
//                                   moveStrafe = 0F;
//                               }
//
//                               if (moveForward > 0F)
//                               {
//                                   moveForward = 1F;
//                               }
//                               else if (moveForward < 0F)
//                               {
//                                   moveForward = -1F;
//                               }
//                           }
//
//                           double motionX2 = Math.cos(Math.toRadians(rotationYaw + 90F));
//                           double motionZ2 = Math.sin(Math.toRadians(rotationYaw + 90F));
//
//                           if (cooldownHops == 0)
//                           {
//                               event.setMotionX(moveForward * moveSpeed * motionX2 + moveStrafe * moveSpeed * motionZ2);
//                               event.setMotionZ(moveForward * moveSpeed * motionZ2 - moveStrafe * moveSpeed * motionX2);
//                           }
//
//                           minecraft.thePlayer.stepHeight = 0.6F;
//
//                           if ((moveForward == 0F) && (moveStrafe == 0F))
//                           {
//                               event.setMotionX(0D);
//                               event.setMotionZ(0D);
//                           }
//
//                           break;
                        case YPORT:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || !minecraft.thePlayer.onGround || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }


                            if (!minecraft.thePlayer.onGround) {
                                stage = 3;
                            }

                            if (!minecraft.thePlayer.isCollidedHorizontally && PlayerHelper.isMoving()) {
                                if (minecraft.thePlayer.onGround) {

//                                    minecraft.getTimer().timerSpeed = 1.2F;
                                    minecraft.getTimer().timerSpeed = 1.07F;
//                                    stopwatch.reset();

                                    if (stage == 2) {
                                        moveSpeed *= 2.149D;
                                        stopwatch.reset();
                                        minecraft.getTimer().timerSpeed = 1F;
                                        stage = 3;
                                    } else if (stage == 3) {
                                        stage = 2;
                                        double difference = 0.66F * (lastDist - getBaseMoveSpeed());
                                        moveSpeed = (lastDist - difference);
                                    } else {
                                        List collidingList1 = minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, minecraft.thePlayer.motionY, 0.0D));

                                        if ((collidingList1.size() > 0) || (minecraft.thePlayer.isCollidedVertically)) {
                                            event.setMotionY(event.getMotionY() * getBaseMoveSpeed());
                                             stage = 1;
                                        }
                                    }
                                } else {
                                    minecraft.getTimer().timerSpeed = 1.0F;
                                }

                                moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                                setMoveSpeed(event, moveSpeed);
                                stopwatch.reset();
                            }

                            break;

             /*           case ONGROUND:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning())
                            {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || !minecraft.thePlayer.onGround || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock()))
                            {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }

                            if (minecraft.thePlayer.onGround)
                            {
                                ticks = 2;
                            }

                            if (MathHelper.roundToPlace(minecraft.thePlayer.posY - (int)minecraft.thePlayer.posY, 3) == MathHelper.roundToPlace(0.138, 3))
                            {
                                final EntityPlayerSP thePlayer = minecraft.thePlayer;
                                thePlayer.motionY -= 0.08;
                                event.setMotionY(event.getMotionY() - 0.09316090325960147);
                                final EntityPlayerSP thePlayer2 = minecraft.thePlayer;
                                thePlayer2.posY -= 5.0931609032596015;
                            }

                            if (ticks == 1 && (minecraft.thePlayer.moveForward != 0.0f || minecraft.thePlayer.moveStrafing != 0.0f))
                            {
                                ticks = 2;
                                moveSpeed = 1.35 * getBaseMoveSpeed() - 0.01;
                            }
                            else if (ticks == 2)
                            {
                                if (stop)
                                {
                                    moveSpeed = 0.85;
                                    stop = false;
                                }

                                moveSpeed *= 0.9;
                                ticks = 3;

                                if ((minecraft.thePlayer.moveForward != 0.0f || minecraft.thePlayer.moveStrafing != 0.0f) && !minecraft.thePlayer.isCollidedHorizontally)
                                {
                                    minecraft.thePlayer.motionY = 0.399999995003033;
                                    event.setMotionY(0.399999999993033);
                                    moveSpeed *= (boost ? 3.5 : 2.385);
                                    final EntityPlayerSP thePlayer3 = minecraft.thePlayer;
                                    thePlayer3.motionY *= -1999.0;
                                    boost = false;
                                }
                            }
                            else if (ticks == 3)
                            {
                                ticks = 4;
                                final double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                                moveSpeed = lastDist - difference;
                            }
                            else if (minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.boundingBox.offset(0.0, minecraft.thePlayer.motionY, 0.0)).size() > 0 || minecraft.thePlayer.isCollidedVertically)
                            {
                                ticks = 1;
                            }

                            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                            final MovementInput movementInput = minecraft.thePlayer.movementInput;
                            float forward = movementInput.moveForward;
                            float strafe = movementInput.moveStrafe;
                            float yaw = minecraft.thePlayer.rotationYaw;

                            if (forward == 0.0f && strafe == 0.0f)
                            {
                                event.setMotionX(0.0);
                                event.setMotionZ(0.0);
                            }

                            if (strafe >= 1.0f)
                            {
                                yaw += ((forward > 0.0f) ? -45 : 45);
                                strafe = 0.0f;
                            }
                            else if (strafe <= -1.0f)
                            {
                                yaw += ((forward > 0.0f) ? 45 : -45);
                                strafe = 0.0f;
                            }

                            if (forward > 0.0f)
                            {
                                forward = 1.0f;
                            }
                            else if (forward < 0.0f)
                            {
                                forward = -1.0f;
                            }

                            final double mx = Math.cos(Math.toRadians(yaw + 90.0f));
                            final double mz = Math.sin(Math.toRadians(yaw + 90.0f));
                            final double motionXX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
                            final double motionZZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
                            event.setMotionX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
                            event.setMotionZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
                            canStep = true;
                            minecraft.thePlayer.stepHeight = 0.6f;

                            if (forward == 0.0f && strafe == 0.0f)
                            {
                                event.setMotionX(0.0);
                                event.setMotionZ(0.0);
                            }
                            else
                            {
                                boolean collideCheck = false;
                                minecraft.getMinecraft();

                                if (minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.boundingBox.expand(0.5, 0.0, 0.5)).size() > 0)
                                {
                                    collideCheck = true;
                                }
                            }

                            if (forward != 0.0f)
                            {
                                if (strafe >= 1.0f)
                                {
                                    yaw += ((forward > 0.0f) ? -45 : 45);
                                    strafe = 0.0f;
                                }
                                else if (strafe <= -1.0f)
                                {
                                    yaw += ((forward > 0.0f) ? 45 : -45);
                                    strafe = 0.0f;
                                }

                                if (forward > 0.0f)
                                {
                                    forward = 1.0f;
                                }
                                else if (forward < 0.0f)
                                {
                                    forward = -1.0f;
                                }
                            }
*/
                        case AAC:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || !minecraft.thePlayer.onGround || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }
                            boolean boost = Math.abs(minecraft.thePlayer.rotationYawHead - minecraft.thePlayer.rotationYaw) < 90;
                            if (minecraft.thePlayer.moveForward > 0 && minecraft.thePlayer.hurtTime < 5) {
                                if (minecraft.thePlayer.onGround) {
//                                  minecraft.thePlayer.jump();
                                    if (minecraft.thePlayer.isCollidedHorizontally)
                                    minecraft.thePlayer.motionY = 0.405;
                                    float f = ClientUtils.getDirection();
                                    minecraft.thePlayer.motionX -= (double) (MathHelper.sin(f) * 0.2F);
                                    minecraft.thePlayer.motionZ += (double) (MathHelper.cos(f) * 0.2F);
                                } else {
                                    double currentSpeed = Math.sqrt(minecraft.thePlayer.motionX * minecraft.thePlayer.motionX + minecraft.thePlayer.motionZ * minecraft.thePlayer.motionZ);
                                    double speed = boost ? 1.0064 : 1.001;

                                    double direction = ClientUtils.getDirection();

                                    minecraft.thePlayer.motionX = -Math.sin(direction) * speed * currentSpeed;
                                    minecraft.thePlayer.motionZ = Math.cos(direction) * speed * currentSpeed;
                                }
                            }
                            break;


                        case VHOP2:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }
                            if (Helper.player().moveForward == 0.0f && Helper.player().moveStrafing == 0.0f) {
                                moveSpeed = getBaseMoveSpeed();
                            }
                            if (MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.4, 3)) {
                                event.setMotionY(Helper.player().motionY = 0.31);
                            } else if (MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.71, 3)) {
                                event.setMotionY(Helper.player().motionY = 0.04);
                            } else if (MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.75, 3)) {
                                event.setMotionY(Helper.player().motionY = -0.2);
                            }
                            List collidingList22 = ClientUtils.world().getCollidingBoundingBoxes(Helper.player(), Helper.player().boundingBox.offset(0.0, -0.56, 0.0));
                            if (collidingList22.size() > 0 && MathHelper.roundToPlace(Helper.player().posY - (int) Helper.player().posY, 3) == MathHelper.roundToPlace(0.55, 3)) {
                                event.setMotionY(Helper.player().motionY = -0.14);
                            }
                            if (stage == 1 && Helper.player().isCollidedVertically && (Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f)) {
                                moveSpeed = 2.0 * getBaseMoveSpeed() - 0.4;
                            } else if (stage == 2 && Helper.player().isCollidedVertically && (Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f)) {
                                event.setMotionY(Helper.player().motionY = 0.4);
                                moveSpeed *= 2.03;
                                minecraft.getTimer().timerSpeed = 1.1F;
                            } else if (stage == 3) {
                                final double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                                moveSpeed = lastDist - difference;
                            } else {
                                collidingList = ClientUtils.world().getCollidingBoundingBoxes(Helper.player(), Helper.player().boundingBox.offset(0.0, Helper.player().motionY, 0.0));
                                if ((collidingList.size() > 0 || Helper.player().isCollidedVertically) && stage > 0) {
                                    if (2.0 * getBaseMoveSpeed() - 0.01 > moveSpeed) {
                                        stage = 0;
                                    } else {
                                        stage = ((Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f) ? 1 : 0);
                                    }
                                }
                                moveSpeed = lastDist - lastDist / 59.0;
                            }
                            if (stage > 8) {
                                moveSpeed = getBaseMoveSpeed();
                            }
                            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                            if (stage > 0) {
                                setMoveSpeed(event, moveSpeed);
                            }
                            if (Helper.player().moveForward != 0.0f || Helper.player().moveStrafing != 0.0f) {
                                ++stage;
                            }
                            minecraft.getTimer().timerSpeed = 1.0f;

                            break;
                        case TEST:

                            break;
                        case OFFSET:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || !minecraft.thePlayer.onGround || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }

                            moveSpeed = getBaseMoveSpeed();

                            if (minecraft.thePlayer.onGround) {
                                if (stage == 1) {
                                    moveSpeed *= 2.15D;
                                } else if (stage == 2) {
                                    minecraft.getTimer().timerSpeed = 1.07F;
                                    moveSpeed *= 1.165D;
                                }

                                if (stage > 3) {
                                    stage = 0;
                                    moveSpeed = getBaseMoveSpeed();
                                }

                                stage += 1;
                                setMoveSpeed(event, moveSpeed);
                            }

                            break;

                        case SHOTBOW:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }

                            if (!minecraft.thePlayer.isCollidedHorizontally && PlayerHelper.isMoving() && minecraft.thePlayer.onGround) {
                                switch (stage) {
                                    case 1:
                                        moveSpeed = 0.579D;
                                        break;

                                    case 2:
                                        moveSpeed = 0.66781D;
                                        break;

                                    default:
                                        moveSpeed = getBaseMoveSpeed();
                                }

                                moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                                setMoveSpeed(event, moveSpeed);
                                stage += 1;
                            }

                            break;
                        case GUARDIAN:
                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || !minecraft.thePlayer.onGround || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }
                            if (!PlayerHelper.isInLiquid() && !PlayerHelper.isOnLiquid() && !((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning() && !((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("longjump")).isRunning()) {
                                if (Helper.player().fallDistance >= 3.0) {
                                    event.setMotionX(0.0);
                                    event.setMotionZ(0.0);
                                    minecraft.getTimer().elapsedTicks = 1;
                                } else {
                                    event.setMotionX(event.getMotionX() * 1.5);
                                    event.setMotionZ(event.getMotionZ() * 1.5);
                                    minecraft.getTimer().elapsedTicks = minecraft.thePlayer.ticksExisted % 2 == 0 ? 3 : minecraft.thePlayer.ticksExisted % 3 == 0 ? 2 : 2;
                                }
                            }

                            break;
                        case TIMER:
                            if (minecraft.theWorld != null)
                                minecraft.getTimer().timerSpeed = timer.getValue();
                            break;
                        case NCPHOP2:
                            if (((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("flight")).isRunning()) {
                                return;
                            }

                            if ((PlayerHelper.isInLiquid()) || PlayerHelper.isOnLiquid() || (minecraft.thePlayer.isOnLadder()) || (minecraft.thePlayer.isEntityInsideOpaqueBlock())) {
                                moveSpeed = 0D;
                                wasOnWater = true;
                                return;
                            }

                            if (minecraft.thePlayer.onGround) {
                                stage = 2;
                                minecraft.getTimer().timerSpeed = 1F;
                            }

                            if (round(minecraft.thePlayer.posY - (int) minecraft.thePlayer.posY, 3) == round(0.138D, 3)) {
//                                minecraft.thePlayer.motionY -= 0.28D;
//                                event.setMotionY(event.getMotionY() - 0.0931D);
//                                minecraft.thePlayer.posY -= 0.0931D;
                            }

                            if ((stage == 1) && (PlayerHelper.isMoving())) {
                                stage = 2;
                                moveSpeed = (1.5D * getBaseMoveSpeed() - 0.01D);
                            } else if (stage == 2) {
                                stage = 3;

                                if (PlayerHelper.isMoving()) {
                                    minecraft.thePlayer.motionY = 0.4D;
                                    event.setMotionY(0.4D);

                                    if (cooldownHops > 0) {
                                        cooldownHops -= 1;
                                    }

                                    moveSpeed *= 1.5D;
                                }
                            } else if (stage == 3.0D) {
                                stage = 4;
                                double difference = 0.66D * (lastDist - getBaseMoveSpeed());
                                moveSpeed = (lastDist - difference);
                            } else {
                                if ((minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0D, minecraft.thePlayer.motionY, 0D)).size() > 0) || (minecraft.thePlayer.isCollidedVertically)) {
                                    stage = 1;
                                }

                                moveSpeed = (lastDist - lastDist / 159D);
                            }

                            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());

                            if ((moveForward == 0F) && (moveStrafe == 0F)) {
                                event.setMotionX(0D);
                                event.setMotionZ(0D);
                                moveSpeed = 0D;
                            } else if (moveForward != 0F) {
                                if (moveStrafe >= 1F) {
                                    rotationYaw += (moveForward > 0F ? -45F : 45F);
                                    moveStrafe = 0F;
                                } else if (moveStrafe <= -1F) {
                                    rotationYaw += (moveForward > 0F ? 45F : -45F);
                                    moveStrafe = 0F;
                                }

                                if (moveForward > 0F) {
                                    moveForward = 1F;
                                } else if (moveForward < 0F) {
                                    moveForward = -1F;
                                }
                            }

                            double motionXNCP2 = Math.cos(Math.toRadians(rotationYaw + 90F));
                            double motionZNCP2 = Math.sin(Math.toRadians(rotationYaw + 90F));

                            if (cooldownHops == 0) {
                                event.setMotionX(moveForward * moveSpeed * motionXNCP2 + moveStrafe * moveSpeed * motionZNCP2);
                                event.setMotionZ(moveForward * moveSpeed * motionZNCP2 - moveStrafe * moveSpeed * motionXNCP2);
                            }

                            minecraft.thePlayer.stepHeight = 0.6F;

                            if ((moveForward == 0F) && (moveStrafe == 0F)) {
                                event.setMotionX(0D);
                                event.setMotionZ(0D);
                            }
                            break;
                        case JUMP: // Created by comu//
                            if (!minecraft.thePlayer.capabilities.isFlying) {
                                if (PlayerHelper.isMoving() && !PlayerHelper.isOnLiquid() && !PlayerHelper.isInLiquid()) {
                                    if (minecraft.thePlayer.onGround && stage > 1) {
                                        stage = -5;
                                    }

                                    if (stage < 0 && PlayerHelper.isMoving()) {
                                        minecraft.getTimer().timerSpeed = 1.0F;

                                        if (stage % 2 == 0) {
                                            minecraft.getTimer().timerSpeed = 1.21F;
                                        } else {
                                            minecraft.getTimer().timerSpeed = 1.0F;
                                        }

                                        minecraft.thePlayer.motionZ *= (-0.1);
                                        minecraft.thePlayer.motionX *= (-0.1);
                                    } else if (stage == 0) {
                                        moveSpeed = 4.48 * getBaseMoveSpeed();
                                    } else if (stage == 1) {
                                        event.setMotionY(minecraft.thePlayer.motionY = 0.4F);
                                        minecraft.thePlayer.onGround = false;
                                        moveSpeed *= 2.149F;
                                    } else if (stage == 2) {
                                        minecraft.thePlayer.onGround = false;
                                        double difference = 0.66F * (lastDist - getBaseMoveSpeed());
                                        minecraft.getTimer().timerSpeed = 1.07F;
                                        moveSpeed = lastDist - difference;
                                    } else {
                                        minecraft.thePlayer.onGround = false;
                                        moveSpeed = lastDist - lastDist / 159.0F;
                                        minecraft.getTimer().timerSpeed = 1.07F;
                                        cooldownHops++;

                                        if (cooldownHops > 50) {
                                            stage = -8;
                                            cooldownHops = 0;
                                        }
                                    }

                                    if (stage >= 0) {
                                        moveSpeed = Math.max(getBaseMoveSpeed(), moveSpeed);
                                        setMoveSpeed(event, moveSpeed);
                                        List collidingList1 = minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0, minecraft.thePlayer.motionY, 0.0));
                                        List collidingList2 = minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0, -0.4F, 0.0));

                                        if (!minecraft.thePlayer.isCollidedVertically && (collidingList1.size() > 0 || collidingList2.size() > 0)) {
                                            float speed = -0.00000001F;
                                            event.setMotionY(minecraft.thePlayer.motionY = speed);
                                        }
                                    }

                                    stage++;

                                    if (stage > 4) {
                                        stage = 4;
                                    }
                                } else {
                                    moveSpeed = 0.0F;
                                    stage = -6;
                                }
                            } else {
                                moveSpeed = 0.0F;
                                stage = -6;
                            }

                            break;
                    }
                }
            });
        }
        listeners.add(new Listener<PacketEvent>("speed_move_player_listener") {
            @Override
            public void call(PacketEvent event) {
            if (lagback.getValue() && event.getPacket() instanceof S08PacketPlayerPosLook)
            {
                NotificationManager.notify(new Notification(NotificationType.INFO, "Flagged Anti-Cheat", "Disabled Speed", 5));
                minecraft.thePlayer.onGround = false;
                final EntityPlayerSP thePlayer9 = minecraft.thePlayer;
                thePlayer9.motionX *= 0.0;
                final EntityPlayerSP thePlayer10 = minecraft.thePlayer;
                thePlayer10.motionZ *= 0.0;
                minecraft.thePlayer.jumpMovementFactor = 0.0f;
                ToggleableModule toggleSounds = (ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("modulesounds");
                if (toggleSounds.isRunning()) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
                }
                toggle();
            }
            }
        });
    }

    private void setMoveSpeed(MovePlayerEvent event, double speed) {
        double forward = minecraft.thePlayer.movementInput.moveForward;
        double strafe = minecraft.thePlayer.movementInput.moveStrafe;
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

            event.setMotionX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setMotionZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (mode.getValue() == Mode.YPORT || mode.getValue() == Mode.OFFSET) {
            moveSpeed = getBaseMoveSpeed();
            lastDist = 0;
            stage = 2;
        }

        if (mode.getValue() == Mode.SHOTBOW) {
            stage = 4;
            lastDist = 0;
        } else {
            moveSpeed = getBaseMoveSpeed();
        }

        //       if (mode.getValue() == Mode.ONGROUND)
        //     {
        //       stop = true;
        //     boost = true;
        //}
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        minecraft.getTimer().timerSpeed = 1F;
        moveSpeed = 0D;
        stage = 0;
        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;

    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bigDecimal = new BigDecimal(value).setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;

        if (minecraft.thePlayer != null) {
            if (minecraft.thePlayer.isPotionActive(Potion.moveSpeed) && mode.getValue() != Mode.JUMP) {
                int amplifier = minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                baseSpeed *= (1D + 0.2D * (amplifier + 1));
            }
        }

        return baseSpeed;
    }


    public enum Mode {
        HOP, NCPHOP, VHOP, OLD, JUMP, YPORT, SHOTBOW, OFFSET, AAC, GUARDIAN, TIMER, VANILLA, NCPHOP2, VHOP2, TEST
    }
}
