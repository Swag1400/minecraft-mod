package me.comu.client.presets;

import me.comu.api.interfaces.Labeled;

public abstract class Preset implements Labeled
{
    private final String label;

    protected Preset(String label)
    {
        this.label = label;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    public abstract void onSet();
}
