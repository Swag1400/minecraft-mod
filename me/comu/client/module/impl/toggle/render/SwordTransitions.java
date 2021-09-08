package me.comu.client.module.impl.toggle.render;

import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;

public class SwordTransitions extends ToggleableModule
{
    private double xx, yy, zz;
    private final NumberProperty<Double>  x = new NumberProperty<Double>(xx, 1.0, 2.0, 0.1, "z");
    private final NumberProperty<Double> y = new NumberProperty<Double>(yy, 1.0, 2.0, 0.1, "x");
    private final NumberProperty<Double> z = new NumberProperty<Double>(zz, 1.0, 2.0, 0.1,"y");

    public SwordTransitions()
    {
        super("SwordTransitions", new String[] {"swordtransitions", "stransitions", "swordtrans","st"}, ModuleType.RENDER);;
        this.offerProperties(x,y,z);
    }

    public double getX()
    {
        return this.xx;
    }

    public double getY()
    {
        return this.yy;
    }

    public double getZ()
    {
        return this.zz;
    }
}
