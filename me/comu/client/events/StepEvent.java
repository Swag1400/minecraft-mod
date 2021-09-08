package me.comu.client.events;

import me.comu.api.event.Event;

public class StepEvent extends Event
{
    private final Time time;
    private float height;

    public StepEvent(float height)
    {
        this.time = Time.BEFORE;
        this.height = height;
    }

    public StepEvent(Time time)
    {
        this.time = time;
    }

    public Time getTime()
    {
        return time;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public enum Time
    {
        BEFORE, AFTER
    }
}
