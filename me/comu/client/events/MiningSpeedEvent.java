package me.comu.client.events;

import me.comu.api.event.Event;

public class MiningSpeedEvent extends Event
{
    private float speed = 1;

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
}
