package me.comu.client.staff;

import me.comu.api.interfaces.Labeled;


public class Staff implements Labeled
{
    private final String label;
    private final String alias;

    public Staff(String label, String alias)
    {
        this.label = label;
        this.alias = alias;
    }

    public String getAlias()
    {
        return alias;
    }

    @Override
    public String getLabel()
    {
        return label;
    }
}
