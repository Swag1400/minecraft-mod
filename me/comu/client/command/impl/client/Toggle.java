package me.comu.client.command.impl.client;

import me.comu.api.interfaces.Toggleable;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.module.Module;
import me.comu.client.module.ToggleableModule;


public final class Toggle extends Command
{
    public Toggle()
    {
        super(new String[] {"toggle", "t"}, new Argument("module"));
    }

    @Override
    public String dispatch()
    {
        Module module = Gun.getInstance().getModuleManager().getModuleByAlias(getArgument("module").getValue());

        if (module == null)
        {
            return "No such module exists.";
        }

        if (!(module instanceof Toggleable))
        {
            return "That module is not toggleable.";
        }

        ToggleableModule toggleableModule = (ToggleableModule) module;
        toggleableModule.toggle();
        return String.format("&e%s&7 has been %s&7.", toggleableModule.getLabel(), toggleableModule.isRunning() ? "&aenabled" : "&cdisabled");
    }
}
