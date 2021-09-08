package me.comu.client.module.impl.toggle.render.tabgui.item;

import me.comu.api.interfaces.Labeled;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.module.impl.toggle.render.tabgui.GuiTabHandler;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class GuiTab implements Labeled
{
    private final GuiTabHandler gui;
    private ArrayList<GuiItem> mods = new ArrayList<>();
    private ArrayList<GuiItem> properties = new ArrayList<>();
    private int menuHeight = 0, menuWidth = 0;
    private final String label;

    public GuiTab(GuiTabHandler gui, String label)
    {
        this.gui = gui;
        this.label = label;
    }

    public void drawTabMenu(Minecraft mc, int x, int y)
    {
        countMenuSize(mc);
        int boxY = y;
        RenderMethods.drawBorderedRectReliant(x, y - 0.4F, x + this.menuWidth + 4.5F, y + this.menuHeight + 0.4F, 1.5F, 0x66000000, 0x88000000);

        for (int i = 0; i < this.mods.size(); i++)
        {
            int transitionTop = this.gui.getTransition()
                    + (this.gui.getSelectedItem() == 0 && this.gui.getTransition() < 0 ? -this.gui.getTransition() : 0);
            int transitionBottom = this.gui.getTransition()
                    + (this.gui.getSelectedItem() == this.mods.size() - 1 && this.gui.getTransition() > 0
                    ? -this.gui.getTransition() : 0);

            if (this.gui.getSelectedItem() == i)
            {
                RenderMethods.drawGradientBorderedRectReliant(x, boxY + transitionTop - 0.3F, x + this.menuWidth + 4.5F, boxY + 12 + transitionBottom + 0.3F, 1.5F, 0x88000000, 0xFF610342, 0xFF610342);
            }

            mc.fontRenderer.drawStringWithShadow(this.mods.get(i).getToggleableModule().getLabel(), x + 2, y + this.gui.getTabHeight() * i + 2, this.mods.get(i).getToggleableModule().isRunning() ? 0xFF610372 : 0xFFFCFCFC);
            boxY += 12;
        }
    }

    private void countMenuSize(Minecraft mc)
    {
        int maxWidth = 0;

        for (GuiItem module : mods)
        {
            if (mc.fontRenderer.getStringWidth(module.getToggleableModule().getAliases()[0]) > maxWidth)
            {
                maxWidth = mc.fontRenderer.getStringWidth(module.getToggleableModule().getAliases()[0]) + 4;
            }
        }

        this.menuWidth = maxWidth;
        this.menuHeight = this.mods.size() * this.gui.getTabHeight();
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    public ArrayList<GuiItem> getMods()
    {
        return mods;
    }
    public ArrayList<GuiItem> getProperties()
    {
        return properties;
    }
}