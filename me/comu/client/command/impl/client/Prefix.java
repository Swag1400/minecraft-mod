package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;


public final class Prefix extends Command
{
    public Prefix()
    {
        super(new String[] {"prefix"}, new Argument("character"));
    }

    @Override
    public String dispatch()
    {
        String prefix = getArgument("character").getValue();
        Gun.getInstance().getCommandManager().setPrefix(prefix);
        return String.format("&e%s&7 is now your prefix.", prefix);
    }
}
