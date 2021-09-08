package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;

public final class Respawn  extends ToggleableModule
{

    private final Property<Boolean> fhome = new Property<>(false, "fhome", "home", "fh", "f-home", "factions", "fac", "facs");
    private final Stopwatch stopwatch = new Stopwatch();
    private boolean done;
    public Respawn()
    {

        super("Respawn", new String[] {"respawn"}, ModuleType.MISCELLANEOUS);
        this.offerProperties(fhome);
        this.listeners.add(new Listener<MotionUpdateEvent>("respawn_input_listener")

        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if ((event.getTime() == MotionUpdateEvent.Time.BEFORE) && (!minecraft.thePlayer.isEntityAlive()))
                {
                    minecraft.thePlayer.respawnPlayer();
                    done = true;
                }
                if (fhome.getValue()) {
                    if (stopwatch.hasCompleted(2500) && done == true) {
                        minecraft.thePlayer.sendChatMessage("/f home");
                        stopwatch.reset();
                        done = false;
                    }
                }
            }
        });
        }
}
