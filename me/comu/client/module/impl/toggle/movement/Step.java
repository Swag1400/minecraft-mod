package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.StepEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nuf on 5/22/2016.
 */
public final class Step extends ToggleableModule {
    private final NumberProperty<Float> height = new NumberProperty<>(1.1F, 1.1F, 10F, 0.1F, "Height", "h");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.PACKET, "Mode", "m");
    private final Stopwatch stopwatch = new Stopwatch();

    private double moveSpeed;
    private double lastDist;
    public static int stage;
    public boolean isHigh;
    public boolean jump;
    private int fix;
    private double oldY;

    public Step() {
        super("Step", new String[]{"step", "autojump"}, 0xc6d43c, ModuleType.MOVEMENT);
        offerProperties(height, mode);
        listeners.add(new Listener<StepEvent>("step_step_listener") {
            @Override
            public void call(StepEvent event) {
            	setDrawn(true);
                Speed speed = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
                EnumProperty<Speed.Mode> mode = (EnumProperty) speed.getPropertyByAlias("Mode");
                if (speed != null && speed.isRunning() && (mode.getValue() == Speed.Mode.HOP || mode.getValue() == Speed.Mode.NCPHOP|| mode.getValue() == Speed.Mode.NCPHOP2|| mode.getValue() == Speed.Mode.VHOP|| mode.getValue() == Speed.Mode.VHOP2)) {
                    return;
                }
                if (!PlayerHelper.isMoving() || !minecraft.thePlayer.onGround || PlayerHelper.isInLiquid() || PlayerHelper.isOnLiquid() || !minecraft.thePlayer.isCollidedHorizontally || !minecraft.thePlayer.isCollidedVertically || minecraft.thePlayer.isOnLadder()) {
                    return;
                }
                switch (event.getTime()) {
                    case BEFORE:
                        if (fix == 0) {
                            oldY = minecraft.thePlayer.posY;
                            event.setHeight(canStep() ? height.getValue() : 0.5F);
                        }
                        break;
                    case AFTER:
                        double offset = minecraft.thePlayer.getEntityBoundingBox().minY - oldY;
                        if (offset > 0.6 && fix == 0 && canStep() && stopwatch.hasCompleted(65L)) {
                            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.42D, minecraft.thePlayer.posZ, true));
                            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.75D, minecraft.thePlayer.posZ, true));
                            fix = 2;
                        }
                        break;
                }

            }
        });
        listeners.add(new Listener<MotionUpdateEvent>("step_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                switch (mode.getValue()) {
                    case PACKET:
                        if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                            if (event.getPositionY() - event.getOldPositionY() >= 0.75 && !minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().addCoord(0.0, -0.1, 0.0)).isEmpty())
                                stopwatch.reset();
                            if (fix > 0) {
                                event.setCanceled(true);
                                fix--;
                            }
                        }
                        break;
                    case TIMER:
                        if (height.getValue() > 0.625) {
                            minecraft.timer.timerSpeed = 0.37f - ((height.getValue() >= 1.0) ? (Math.abs(1.0f - (float)height.getValue()) * (0.37f * 0.55f)) : 0.0f);
                            if (minecraft.timer.timerSpeed <= 0.05f) {
                                minecraft.timer.timerSpeed = 0.05f;
                            }
                            stopwatch.reset();
                            ncpStep(height.getValue());
                            break;
                        }
                        break;
                    case VANILLA:
                        minecraft.thePlayer.stepHeight = height.getValue();
                        break;
                    case SPIDER:
                   spider();
                }
            }
        });
        listeners.add(new Listener<PacketEvent>("step_motion_update_listener") {
            @Override
            public void call(PacketEvent event) {
                    if (event.getPacket() instanceof C03PacketPlayer && isHigh ) {
                        final C03PacketPlayer localC03PacketPlayer = (C03PacketPlayer)event.getPacket();
                        localC03PacketPlayer.setOnGround(true);
                        isHigh = false;
                    }
                }
        });

    }


    private boolean canStep() {
        return !PlayerHelper.isOnLiquid() && !PlayerHelper.isInLiquid() && minecraft.thePlayer.onGround && !minecraft.gameSettings.keyBindJump.getIsKeyPressed() && minecraft.thePlayer.isCollidedVertically && minecraft.thePlayer.isCollidedHorizontally;
    }
    
    private void ncpStep(final double height) {
        final List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
        final double posX = minecraft.thePlayer.posX;
        final double posZ = minecraft.thePlayer.posZ;
        double y = minecraft.thePlayer.posY;
        if (height < 1.1) {
            double first = 0.42;
            double second = 0.75;
            if (height != 1.0) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            if (first == 0.42) {
                first = 0.41999998688698;
            }
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
            if (y + second < y + height) {
                minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
            }
            return;
        }
        if (height < 1.6) {
            for (int i = 0; i < offset.size(); ++i) {
                final double off = offset.get(i);
                y += off;
                minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
            }
        }
        else if (height < 2.1) {
            final double[] array;
            final double[] heights = array = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869 };
            for (final double off2 : array) {
                minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off2, posZ, false));
            }
        }
        else {
            final double[] array2;
            final double[] heights = array2 = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
            for (final double off2 : array2) {
                minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off2, posZ, false));
            }
        }
    }

    @Override
    protected  void onEnable() {
        super.onEnable();
        stage = 1;
    }
    @Override
    protected void onDisable() {
        super.onDisable();
        minecraft.thePlayer.stepHeight = 0.5F;
    }
    public void spider() {
        if (minecraft.thePlayer.isCollidedHorizontally) {
            minecraft.thePlayer.motionX = 0.0;
            minecraft.thePlayer.motionZ = 0.0;
            jump = true;
            if (minecraft.thePlayer.motionY < -0.19) {
                if (minecraft.thePlayer.onGround) {
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                    isHigh = true;
                }
                else {
                    minecraft.thePlayer.motionY = 0.42;
                    isHigh = true;
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
            }
        }
        else {
            jump = false;
        }
    }

    public enum Mode {
        PACKET, TIMER, VANILLA, SPIDER
    }
}
