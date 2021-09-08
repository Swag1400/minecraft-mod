package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.module.ToggleableModule;


/**
 * Created by comu on 12/31/2018
 */


public final class Visible extends Command
{
    public Visible()
    {
        super(new String[] {"visible", "shown","show","vis", "hidden","hide"}, new Argument("Module"));
    }

    @Override
    public String dispatch()
    {
        ToggleableModule module  = (ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias(getArgument("module").getValue());
        if (module != null)
        {
            module.setDrawn(!module.isDrawn());
            return "\247e" + module.getLabel() + "\2477 is now \247e" + (module.isDrawn() ? "shown\2477." : "hidden\2477.");
        } else {
            return "\247e" + getArgument("module").getValue() + "\2477 is not a valid module\2477.";
        }
    }
}
