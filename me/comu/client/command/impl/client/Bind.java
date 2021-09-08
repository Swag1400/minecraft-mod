package me.comu.client.command.impl.client;

import me.comu.client.core.Gun;
import org.lwjgl.input.Keyboard;

import me.comu.api.interfaces.Toggleable;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.module.Module;
import me.comu.client.module.ToggleableModule;


public final class Bind extends Command
{
    public Bind()
    {
        super(new String[] {"bind"}, new Argument("module"), new Argument("key"));
    }

    @Override
    public String dispatch()
    {
        Module module = Gun.getInstance().getModuleManager().getModuleByAlias(getArgument("module").getValue());
        int key = Keyboard.getKeyIndex(getArgument("key").getValue().toUpperCase());

        if (module == null)
        {
            return "No such module exists.";
        }

        if (!(module instanceof Toggleable))
        {
            return "That module is not toggleable.";
        }

        ToggleableModule toggleableModule = (ToggleableModule) module;
        Gun.getInstance().getKeybindManager().getKeybindByLabel(toggleableModule.getLabel()).setKey(key);
        return String.format("&e%s&7 has been bound to &e%s&7.", toggleableModule.getLabel(), Keyboard.getKeyName(key).toUpperCase());
    }
}
