package me.comu.client.module.impl.toggle.render.clickgui;

import me.comu.api.interfaces.Toggleable;
import me.comu.api.minecraft.render.CustomFont;
import me.comu.client.core.Gun;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.render.clickgui.item.ModuleButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;

public final class ClickGui extends GuiScreen
{
    private static ClickGui clickGui;
    public final CustomFont guiFont = new CustomFont("Segoe UI", 18);
    private final ArrayList<Panel> panels = new ArrayList<>();

    public ClickGui()
    {
        if (getPanels().isEmpty())
        {
            load();
        }
    }

    public static ClickGui getClickGui()
    {
        return clickGui == null ? clickGui = new ClickGui() : clickGui;
    }

    private void load()
    {
        int x = -84;//TODO fix everything

        for (ModuleType moduleType : ModuleType.values())
            panels.add(new Panel(moduleType.getLabel(), x += 90, 4, true)
        {
            @Override
            public void setupItems()
            {
                Gun.getInstance().getModuleManager().getRegistry().forEach(module ->
                {
                    if (module instanceof Toggleable && !module.getLabel().equalsIgnoreCase("Tab Gui") && !module.getLabel().equalsIgnoreCase("Click Gui"))
                    {
                        ToggleableModule toggleableModule = (ToggleableModule) module;

                        if (toggleableModule.getModuleType().equals(moduleType))
                        {
                            addButton(new ModuleButton(toggleableModule));
                        }
                    }
                });
            }
        });
        panels.add(new Panel("Always Active", x += 90, 4, true)
        {
            @Override
            public void setupItems()
            {
                Gun.getInstance().getModuleManager().getRegistry().forEach(module ->
                {
                    if (!(module instanceof Toggleable) && !module.getLabel().equalsIgnoreCase("Tab Gui") && !module.getLabel().equalsIgnoreCase("Click Gui"))
                    {
                        addButton(new ModuleButton(module));
                    }
                });
            }
        });
        panels.forEach(panel -> panel.getItems().sort((item1, item2) -> item1.getLabel().compareTo(item2.getLabel())));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        panels.forEach(panel -> panel.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton)
    {
        panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton)
    {
        panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public final ArrayList<Panel> getPanels()
    {
        return panels;
    }
    public void hoveringText() {
    }
}
