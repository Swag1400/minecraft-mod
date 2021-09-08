package me.comu.client.module.impl.toggle.render.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

import me.comu.api.interfaces.Labeled;
import me.comu.api.minecraft.render.CustomFont;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.module.impl.toggle.render.clickgui.item.Button;
import me.comu.client.module.impl.toggle.render.clickgui.item.Item;

public abstract class Panel implements Labeled
{
    private final String label;
    private int x, y, x2, y2, width, height;
    private boolean open;
    public boolean drag;
    private final ArrayList<Item> items = new ArrayList<>();

    public Panel(String label, int x, int y, boolean open)
    {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 18;
        this.open = open;
        setupItems();
    }

    /**
     * dont remove, actually has a use (ClickGui.java)
     */
    public abstract void setupItems();

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drag(mouseX, mouseY);
        float totalItemHeight = open ? getTotalItemHeight() - 2F : 0F;
        RenderMethods.drawGradientRect(x, y - 1.5F, (x + width), y + height - 6, 0xFF888888, 0xFF999999);

        if (open)
        {
            RenderMethods.drawRect(x, y + 12.5F, (x + width), open ? (y + height + totalItemHeight) : y + height - 1, 0x77000000);
        }

        ClickGui.getClickGui().guiFont.drawString(getLabel(), x + 3F, y - 4F, CustomFont.FontType.SHADOW_THIN, 0xFFFFFFFF);

        if (open)
        {
            float y = getY() + getHeight() - 3F;

            for (Item item : getItems())
            {
                item.setLocation(x + 2F, y);
                item.setWidth(getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += item.getHeight() + 1.5F;
            }
        }
    }

    private void drag(int mouseX, int mouseY)
    {
        if (!drag)
        {
            return;
        }

        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && isHovering(mouseX, mouseY))
        {
            x2 = x - mouseX;
            y2 = y - mouseY;
            ClickGui.getClickGui().getPanels().forEach(panel ->
            {
                if (panel.drag)
                {
                    panel.drag = false;
                }
            });
            drag = true;
            return;
        }

        if (mouseButton == 1 && isHovering(mouseX, mouseY))
        {
            open = !open;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
            return;
        }

        if (!open)
        {
            return;
        }

        getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void addButton(Button button)
    {
        items.add(button);
    }

    public void mouseReleased(final int mouseX, int mouseY, int releaseButton)
    {
        if (releaseButton == 0)
        {
            drag = false;
        }

        if (!open)
        {
            return;
        }

        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    public final String getLabel()
    {
        return label;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setOpen(boolean open)
    {
        this.open = open;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean getOpen()
    {
        return open;
    }

    public final ArrayList<Item> getItems()
    {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY)
    {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight()
    {
        float height = 0;

        for (Item item : getItems())
        {
            height += item.getHeight() + 1.5F;
        }

        return height;
    }

    public void setX(int dragX)
    {
        this.x = dragX;
    }

    public void setY(int dragY)
    {
        this.y = dragY;
    }
}
