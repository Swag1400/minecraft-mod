package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;
/**
 * Created by comu on 9/9/2018
 */

public final class Dupe extends Command
{
    public Dupe()
    {
        super(new String[] {"dupe"}, new Argument("item"), new Argument("amount"));
    }

    @Override
    public String dispatch() {
        String argument = getArgument("Item").getValue();
        int argument2 = Integer.parseInt(getArgument("Amount").getValue());
            Item items = Item.getByNameOrId(argument);
            ItemStack item = new ItemStack(items);
            if (!(item instanceof ItemStack)) {
                return getArgument("item") + " not found!";
            }
            minecraft.thePlayer.dropItem(item.getItem(), argument2);
            Random randy = new Random();
            return "Duping C0IPacketHandshake Buffer @" + randy.nextInt(500);


    }
}
