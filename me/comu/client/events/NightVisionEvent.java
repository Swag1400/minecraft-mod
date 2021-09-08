package me.comu.client.events;

import me.comu.api.event.Event;

public class NightVisionEvent extends Event
{
    private final Type type;

    public NightVisionEvent(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        TIME, VISUAL
    }
}
