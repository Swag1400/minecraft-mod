package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;

public final class FastPlace extends ToggleableModule
{
    private final NumberProperty<Integer> delay = new NumberProperty<>(0, 0, 10, 1, "Delay", "d");
    public FastPlace()
    {
        super("FastPlace", new String[] {"fastplace", "fp", "place"}, 0xFFCC90D4, ModuleType.WORLD);
        this.offerProperties(delay);
        this.listeners.add(new Listener<TickEvent>("fast_place_tick_listener")
        {
            @Override
            public void call(TickEvent event)
            {
                minecraft.rightClickDelayTimer = delay.getValue();
            }
        });
    }
}
