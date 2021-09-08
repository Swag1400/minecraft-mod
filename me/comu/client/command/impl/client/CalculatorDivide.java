package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public final class CalculatorDivide extends Command
{
    public CalculatorDivide()
    {
        super(new String[] {"calcdivide","calcdiv","cdiv","calculatordivide","csub"}, new Argument("x"), new Argument("y"));
    }

    @Override
    public String dispatch() {
        float x = Integer.parseInt(getArgument("x").getValue());
        float y = Integer.parseInt(getArgument("y").getValue());
        float xy = x / y;
        String Stringxy = Float.toString(xy);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Stringxy), null);
        return getArgument("x").getValue() + " / " + getArgument("y").getValue() + " = " + Float.toString(xy);
    }

}
