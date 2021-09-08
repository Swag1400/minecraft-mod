package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import net.minecraft.network.play.server.S03PacketTimeUpdate;


/**
 * Created by comu on 11/15/2018
 */
public class WorldTime extends ToggleableModule {

    private final EnumProperty<Mode> mode = new EnumProperty<Mode>(Mode.CUSTOM, "Vibe", "v");
    private final NumberProperty<Long> time = new NumberProperty<Long>(9000L, 0L, 24000L, 1000L, "Time", "t");

    public WorldTime() {
        super("Ambiance", new String[] {"ambiance","ambi","WorldTime","World-Time","time","wtime"}, ModuleType.WORLD);
        super.offerProperties(mode, time);
        this.listeners.add(new Listener<PacketEvent>("ambiance_packet_event") {
            @Override
            public void call(PacketEvent event) {
            if (event.getPacket() instanceof S03PacketTimeUpdate)
                event.setCanceled(true);
            }
        });
            this.listeners.add(new Listener<MotionUpdateEvent>("ambiance_packet_event") {
                @Override
                public void call(MotionUpdateEvent event) {
                    if (event.getTime() == MotionUpdateEvent.Time.AFTER) {
                        switch (mode.getValue()) {
                            case DAY:
                            minecraft.theWorld.setWorldTime(1000);
                                break;
                            case SUNSET:
                            minecraft.theWorld.setWorldTime(12000);
                                break;
                            case NIGHT:
                                minecraft.theWorld.setWorldTime(13000);
                                break;
                            case SUNRISE:
                            minecraft.theWorld.setWorldTime(23000);
                                break;
                            case MIDNIGHT:
                                minecraft.theWorld.setWorldTime(16000);
                                break;
                            case CUSTOM:
                                minecraft.theWorld.setWorldTime(time.getValue());
                                break;
                        }
                    }
                }
            });
    }


    private enum Mode {
        DAY, SUNSET, NIGHT, SUNRISE, MIDNIGHT, CUSTOM
    }

}
