package me.comu.client.events;

import me.comu.api.event.Event;

public class RenderEvent extends Event
{
    private float partialTicks;

    public RenderEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
