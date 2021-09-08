package me.comu.client.module.impl.toggle.render.clickgui.item;

import me.comu.api.interfaces.Labeled;
import me.comu.api.minecraft.render.CustomFont;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.module.impl.toggle.render.clickgui.ClickGui;
import me.comu.client.module.impl.toggle.render.clickgui.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class Button extends Item implements Labeled
{
    private boolean state;

    public Button(String label)
    {
        super(label);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        RenderMethods.drawGradientRect(x, y , x + width, y + height, getState() ? (!isHovering(mouseX, mouseY) ? 0xbb3C2CFF : 0xbb3C2CFF) : !isHovering(mouseX, mouseY) ? 0x33555555 : 0x88555555, getState() ? (!isHovering(mouseX, mouseY) ? 0xbb3C2CFF  : 0x993C2CFF) : !isHovering(mouseX, mouseY) ? 0x55555555 : 0x99555555);
        ClickGui.getClickGui().guiFont.drawString(getLabel(), x + 2.3F, y - 2F, CustomFont.FontType.SHADOW_THIN, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            state = !state;
            toggle();
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
        }
    }

    public void toggle()
    {
    }

    public boolean getState()
    {
        return state;
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    protected boolean isHovering(int mouseX, int mouseY)
    {
        for (Panel panel : ClickGui.getClickGui().getPanels())
        {
            if (panel.drag)
            {
                return false;
            }
        }

        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}