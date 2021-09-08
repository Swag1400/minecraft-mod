package me.comu.client.module.impl.toggle.render.tabgui.item;

import me.comu.client.module.ToggleableModule;

public class GuiItem
{
    private final ToggleableModule toggleableModule;

    public GuiItem(ToggleableModule toggleableModule)
    {
        this.toggleableModule = toggleableModule;
        
    }

    public ToggleableModule getToggleableModule()
    {
        return toggleableModule;
    }
    
}
