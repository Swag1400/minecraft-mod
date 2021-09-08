package me.comu.client.enemy;

import me.comu.api.interfaces.Labeled;


public class Enemy implements Labeled
{
    private final String label;
    private final String alias;

    public Enemy(String label, String alias)
    {
        this.label = label;
        this.alias = alias;
    }

//    public Enemy(String label) {
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
