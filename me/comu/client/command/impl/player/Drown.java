package me.comu.client.command.impl.player;

import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.command.Command;

public final class Drown extends Command
{
    public Drown()
    {
        super(new String[] {"drown"});
    }

    @Override
    public String dispatch()
    {
        PlayerHelper.drownPlayer();
        return "Drowning...";
    }
}
