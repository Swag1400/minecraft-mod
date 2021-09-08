package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;


public final class SetTitle extends Command
{
    public SetTitle()
    {
        super(new String[] {"settitle", "title","set-title"}, new Argument("title"));
    }

    @Override
    public String dispatch()
    {
        String title = (getArgument("title").getValue()).replaceAll("_"," ");
        {
            Gun.TITLE = title;
        }

        return String.format("Client title set to &e%s&7.", title);
    }
}
