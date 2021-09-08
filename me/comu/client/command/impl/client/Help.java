package me.comu.client.command.impl.client;

import java.util.StringJoiner;

import me.comu.client.command.Command;
import me.comu.client.core.Gun;


public final class Help extends Command
{
    public Help()
    {
        super(new String[] {"help", "halp", "autism", "how"});
    }

    @Override
    public String dispatch()
    {
        StringJoiner stringJoiner = new StringJoiner(", ");
        Gun.getInstance().getCommandManager().getRegistry().forEach(command -> stringJoiner.add(command.getAliases()[0]));
        return String.format("Commands (%s) %s", Gun.getInstance().getCommandManager().getRegistry().size(), stringJoiner.toString());
    }
}
