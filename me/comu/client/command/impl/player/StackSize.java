package me.comu.client.command.impl.player;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;


public final class StackSize extends Command
{
    public StackSize()
    {
        super(new String[] {"stacksize", "size", "staks","stack","ssize"}, new Argument("size"));
    }

    @Override
    public String dispatch()
    {
        Integer size = Integer.parseInt(getArgument("size").getValue());

        if (!minecraft.thePlayer.capabilities.isCreativeMode)
        {
            return "Must be in creative mode.";
        }

        if (minecraft.thePlayer.inventory.getCurrentItem() == null)
        {
            return "Invalid item.";
        }

        minecraft.thePlayer.inventory.getCurrentItem().stackSize = size;
        return String.format("Set item stack size to &e%s&7.", size);
    }
}
