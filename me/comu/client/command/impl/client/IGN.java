package me.comu.client.command.impl.client;

import me.comu.client.command.Command;

import java.awt.*;
import java.awt.datatransfer.StringSelection;


/**
 * Created by comu on 12/31/2018
 */


public final class IGN extends Command
{
    public IGN()
    {
        super(new String[] {"ign", "name"});
    }

    @Override
    public String dispatch()
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(minecraft.thePlayer.getName()), null);
        return minecraft.thePlayer.getName() + " (Copied to clipboard)";
    }
}
