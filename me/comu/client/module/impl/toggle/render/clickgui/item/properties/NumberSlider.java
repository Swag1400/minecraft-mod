package me.comu.client.module.impl.toggle.render.clickgui.item.properties;

import me.comu.api.minecraft.render.CustomFont;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.module.impl.toggle.render.clickgui.ClickGui;
import me.comu.client.module.impl.toggle.render.clickgui.Panel;
import me.comu.client.module.impl.toggle.render.clickgui.item.Item;
import me.comu.client.properties.NumberProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.text.DecimalFormat;

public class NumberSlider extends Item
{
    private NumberProperty numberProperty;
    DecimalFormat decimalFormat = new DecimalFormat("#.####");

    public NumberSlider(NumberProperty numberProperty)
    {
        super(numberProperty.getAliases()[0]);
        this.numberProperty = numberProperty;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        RenderMethods.drawRect(x, y, x + getValueWidth(), y + height, !isHovering(mouseX, mouseY) ? 0xbb3C2CFF : 0x994242bb);
        ClickGui.getClickGui().guiFont.drawString(String.format("%s\2477 %s", getLabel(), decimalFormat.format(numberProperty.getValue())), x + 2.3F, y - 1F, CustomFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
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
                numberProperty.increment();
            }
            if (mouseButton == 1)
            {
                numberProperty.decrement();
            }
        }
    }

    @Override
    public int getHeight()
    {
        return 14;
    }

    private boolean isHovering(int mouseX, int mouseY)
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

    private float getValueWidth()
    {
        return (float)((this.getWidth() - 2.0) * ((numberProperty.getDoubleValue() - numberProperty.getDoubleMinimum()) / (numberProperty.getDoubleMaximum() - numberProperty.getDoubleMinimum())));
    }

}
