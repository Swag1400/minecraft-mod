package me.comu.client.friend;

import me.comu.api.interfaces.Labeled;

public class Friend implements Labeled
{
    private final String label;
    private final String alias;

    public Friend(String label, String alias)
    {
        this.label = label;
        this.alias = alias;
    }

//    public Friend(String label) {
//        this.label = label;
//        this.alias = null;
//    }

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
