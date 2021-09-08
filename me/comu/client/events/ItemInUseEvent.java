package me.comu.client.events;

import me.comu.api.event.Event;

public class ItemInUseEvent extends Event
{
    private float speed;

    public ItemInUseEvent(float speed)
    {
        this.speed = speed;
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
}
