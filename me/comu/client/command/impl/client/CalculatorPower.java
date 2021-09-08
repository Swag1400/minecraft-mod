package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public final class CalculatorPower extends Command
{
    public CalculatorPower()
    {
        super(new String[] {"calcpower","cpower","cpow","calculatorpower"}, new Argument("x"), new Argument("power"));
    }

    @Override
    public String dispatch() {
        int x = Integer.parseInt(getArgument("x").getValue());
        int y = Integer.parseInt(getArgument("power").getValue());
        int xy = (int) Math.pow(x, y);
        String Stringxy = Integer.toString(xy);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Stringxy), null);
        return getArgument("x").getValue() + "^" + getArgument("y").getValue() + " = " + Integer.toString(xy);
    }

}
