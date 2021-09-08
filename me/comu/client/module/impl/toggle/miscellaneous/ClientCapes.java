package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

public final class ClientCapes extends ToggleableModule
{
    public ClientCapes()
    {
        super("Capes", new String[] {"capes", "cape","cloaks","cloak","clientcapes","clientcape"},0xFF4BCFE3, ModuleType.RENDER);
        
    }
}