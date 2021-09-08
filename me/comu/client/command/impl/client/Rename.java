package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.module.ToggleableModule;


/**
 * Created by comu on 12/31/2018
 */


public final class Rename extends Command
{
    public Rename()
    {
        super(new String[] {"rename", "renamemod","renamehack","hackrename","modrename"}, new Argument("module"), new Argument("name"));
    }

    @Override
    public String dispatch()
    {
        ToggleableModule module  = (ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias(getArgument("module").getValue());
        String name = getArgument("name").getValue();
        if (module != null)
        {
            String preName = module.getLabel();
            module.setTag(name);
            return "\247e" + preName + "\2477 has been renamed to \247e" + name +"\2477.";
        } else {
            return "\247e" + getArgument("module").getValue() + "\2477 is not a valid module\2477.";
        }
    }
}
