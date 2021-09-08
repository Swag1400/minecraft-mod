package me.comu.client.command.impl.player;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;


public final class Grab extends Command
{
    public Grab()
    {
        super(new String[] {"grab", "grabip", "grabcoords"}, new Argument("ip|coords"));
    }

    @Override
    public String dispatch()
    {
        String type = getArgument("ip|coords").getValue();

        switch (type)
        {
            case "ip":
            case "i":
                String address = minecraft.getCurrentServerData().serverIP;
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(address), null);
                break;

            case "coords":
            case "coord":
            case "coordinates":
            case "coordinate":
            case "c":
                String coords = String.format("X: %s, Y: %s, Z: %s", (int) minecraft.thePlayer.posX, (int) minecraft.thePlayer.posY, (int) minecraft.thePlayer.posZ);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(coords), null);
                break;

            default:
                return "Incorrect type.";
        }

        return "Copied the selected type.";
    }
}
