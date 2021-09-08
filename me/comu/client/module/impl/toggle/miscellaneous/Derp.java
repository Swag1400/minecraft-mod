package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.*;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;

import java.util.concurrent.ThreadLocalRandom;

public final class Derp extends ToggleableModule                          // value, min, max
{
    private final NumberProperty<Long> Increment = new NumberProperty<>(42L, 1L, 42L, 2L, "Increment", "inc");
    private final Property<Boolean> spin = new Property<>(true, "Spin", "spinny"), headless = new Property<>(true, "Headless", "head");
    private final Property<Boolean> wtfspin = new Property<>(true, "WTFSPIN", "wtf");
    private double serverYaw;
    private final Stopwatch stopwatch = new Stopwatch();

    public Derp()
    {
        super("Spin-Bot", new String[] {"Derp", "retard", "spinbot", "spin-bot", "spin"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.offerProperties(Increment, spin, headless, wtfspin);
        this.listeners.add(new Listener<MotionUpdateEvent>("derp_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (wtfspin.getValue()) {
                    if (event.getTime() == Time.BEFORE) {
                        event.setRotationYaw(ThreadLocalRandom.current().nextInt(-180, 180));
                        event.setRotationPitch(ThreadLocalRandom.current().nextInt(90, 180));
                    }
                }
                if (event.getTime() == Time.BEFORE)
                {
                    if (spin.getValue())
                    {
                        serverYaw += Increment.getValue();
                        event.setRotationYaw((float)serverYaw);
                    }

                    if (headless.getValue())
                    {
                        event.setRotationPitch(180.0f);
                    }
                }
            }
        });
    }
}
