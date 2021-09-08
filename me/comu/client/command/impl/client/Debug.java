package me.comu.client.command.impl.client;

import me.comu.client.command.Command;
/**
 * Created by comu on 9/9/2018
 */

public final class Debug extends Command
{
    public Debug()
    {
        super(new String[] {"debug"});

    }

    @Override
    public String dispatch() {
            minecraft.thePlayer.getFoodStats().setFoodLevel(1);
        return "Debugged";


    }
}
