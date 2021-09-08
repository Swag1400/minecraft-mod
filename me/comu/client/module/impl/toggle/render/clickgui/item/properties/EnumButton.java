package me.comu.client.module.impl.toggle.render.clickgui.item.properties;

import me.comu.api.minecraft.render.CustomFont;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.module.impl.toggle.render.clickgui.ClickGui;
import me.comu.client.module.impl.toggle.render.clickgui.item.Button;
import me.comu.client.properties.EnumProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class EnumButton extends Button
{
    private EnumProperty property;

    public EnumButton(EnumProperty property)
    {
        super(property.getAliases()[0]);
        this.property = property;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        RenderMethods.drawRect(x, y, x + width + 7.4F, y + height, getState() ? (!isHovering(mouseX, mouseY) ? 0xbb3C2CFF : 0x994242bb) : !isHovering(mouseX, mouseY) ? 0x11333333 : 0x88333333);
        ClickGui.getClickGui().guiFont.drawString(String.format("%s\2477 %s", getLabel(), property.getFixedValue()), x + 2.3F, y - 1F, CustomFont.FontType.SHADOW_THIN, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (isHovering(mouseX, mouseY))
        {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));

            if (mouseButton == 0)
            {
                property.increment();
            }
            else if (mouseButton == 1)
            {
                property.decrement();
            }
        }
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    public void toggle()
    {
    }

    public boolean getState()
    {
        return true;
    }
}
