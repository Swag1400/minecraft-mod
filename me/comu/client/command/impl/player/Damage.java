package me.comu.client.command.impl.player;

import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.command.Command;

public final class Damage extends Command
{
    public Damage()
    {
        super(new String[] {"damage", "dmg", "td"});
    }

    @Override
    public String dispatch()
    {
        if (!PlayerHelper.isInLiquid())
        {
            PlayerHelper.damagePlayer();
        }
        else
        {
            PlayerHelper.drownPlayer();
        }

        return "Damaged.";
    }
}
