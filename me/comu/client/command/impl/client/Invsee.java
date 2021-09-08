package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.utils.Helper;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;

public final class Invsee extends Command
{
    public Invsee()
    {
        super(new String[] {"invsee", "inv", "isee", "inventory"}, new Argument("player"));
    }

    @Override
    public String dispatch()
    {
        String playerName = getArgument("Player").getValue();
        boolean found = false;
        for (final Object entity : Helper.world().loadedEntityList) {
            if (entity instanceof EntityOtherPlayerMP) {
                final EntityOtherPlayerMP player = (EntityOtherPlayerMP)entity;
                if (!player.getName().equals(playerName)) {
                    continue;
                }
                minecraft.displayGuiScreen(new GuiInventory(player));
                found = true;
                return "Showing inventory of \247e" + playerName + "\2477.";

            }
        }
        if (!found) {
            return "\247e" + playerName + "\2477 not found.";
        }


    return "Displaying inventory of \247e" + playerName +"\2477.";
    }
}
