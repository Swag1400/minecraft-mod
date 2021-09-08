package me.comu.client.module.impl.toggle.render;

import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

public final class ClickGui extends ToggleableModule
{
    public ClickGui()
    {
        super("Click Gui", new String[] {"clickgui","gui"}, ModuleType.RENDER);
    }
    
 //   private final ResourceLocation img = new ResourceLocation("textures/gui/title/minecraft.png");
    
    @Override
    protected void onEnable()
    {
        super.onEnable();
        minecraft.displayGuiScreen(me.comu.client.module.impl.toggle.render.clickgui.ClickGui.getClickGui());
        setRunning(false);
        
    }
}
