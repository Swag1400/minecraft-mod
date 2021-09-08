package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

public final class Crosshair extends ToggleableModule
{
    private String red = "RED";
    private String green = "GREEN";
    private String blue = "BLUE";
    private String opacity = "OPACITY";
    private String gap = "GAP";
    private String width = "WIDTH";
    private String size = "SIZE";
    private String dynamic = "DYNAMIC";

    public Crosshair()
    {
        super("Crosshair", new String[] {"Crosshair", "cs"}, ModuleType.RENDER);
        this.listeners.add(new Listener<RenderEvent>("crosshair_render_entity_listener")
        {
            @Override
            public void call(RenderEvent event)
            {
            }
        });
    }
}
