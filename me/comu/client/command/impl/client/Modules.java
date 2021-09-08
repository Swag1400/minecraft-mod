package me.comu.client.command.impl.client;

import java.util.List;
import java.util.StringJoiner;

import me.comu.api.interfaces.Toggleable;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.module.Module;
import me.comu.client.module.ToggleableModule;


public final class Modules extends Command
{
    public Modules()
    {
        super(new String[] {"modules", "mods", "ms", "ml", "lm"});
    }

    @Override
    public String dispatch()
    {
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Module> modules = Gun.getInstance().getModuleManager().getRegistry();
        modules.sort((mod1, mod2) -> mod1.getLabel().compareTo(mod2.getLabel()));
        modules.forEach(module ->
        {
            if (module instanceof Toggleable)
            {
                ToggleableModule toggleableModule = (ToggleableModule) module;
                stringJoiner.add(String.format("%s%s&7", toggleableModule.isRunning() ?  "&a" : "&c", toggleableModule.getLabel()));
            }
        });
        return String.format("Modules (%s) %s", Gun.getInstance().getModuleManager().getRegistry().size(), stringJoiner.toString());
    }
}
