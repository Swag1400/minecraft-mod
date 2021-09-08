package me.comu.client.events;

import me.comu.api.event.Event;

public class GammaSettingEvent extends Event
{
    private float gammaSetting;

    public GammaSettingEvent(float gammaSetting)
    {
        this.gammaSetting = gammaSetting;
    }

    public float getGammaSetting()
    {
        return gammaSetting;
    }

    public void setGammaSetting(float gammaSetting)
    {
        this.gammaSetting = gammaSetting;
    }
}
