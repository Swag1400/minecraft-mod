package me.comu.client.events;

import me.comu.api.event.Event;

public class ViewmodelEvent extends Event
{
    private final Type type;
    private boolean noFov, noPitchLimit;

    public ViewmodelEvent(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isNoFov()
    {
        return noFov;
    }

    public boolean isNoPitchLimit()
    {
        return noPitchLimit;
    }

    public void setNoFov(boolean noFov)
    {
        this.noFov = noFov;
    }

    public void setNoPitchLimit(boolean noPitchLimit)
    {
        this.noPitchLimit = noPitchLimit;
    }

    public enum Type
    {
        FOV, PITCH_LIMIT
    }
}
