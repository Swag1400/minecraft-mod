package me.comu.client.events;

import me.comu.api.event.Event;

/**
 * Created by comu on 12/20/2018
 */

public class RenderSkullEvent extends Event
{
    private final Time time;

    public RenderSkullEvent(Time time)
    {
        this.time = time;
    }

    public Time getTime()
    {
        return time;
    }

    public enum Time
    {
        BEFORE, AFTER
    }
}
