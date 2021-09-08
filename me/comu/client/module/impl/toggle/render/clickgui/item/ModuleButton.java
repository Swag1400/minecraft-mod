package me.comu.client.module.impl.toggle.render.clickgui.item;

import me.comu.api.minecraft.render.CustomFont;
import me.comu.client.module.Module;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.render.clickgui.ClickGui;
import me.comu.client.module.impl.toggle.render.clickgui.item.properties.BooleanButton;
import me.comu.client.module.impl.toggle.render.clickgui.item.properties.EnumButton;
import me.comu.client.module.impl.toggle.render.clickgui.item.properties.NumberSlider;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button {
    private final Module module;
    private List<Item> items = new ArrayList<>();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getLabel());
        this.module = module;

        if (!module.getProperties().isEmpty()) {
            for (Property property : module.getProperties()) {
                if (property.getValue() instanceof Boolean) {
                    items.add(new BooleanButton(property));
                } else if (property instanceof EnumProperty) {
                    items.add(new EnumButton((EnumProperty) property));
                } else /*(property.getValue() instanceof NumberProperty)*/ {
                    items.add(new NumberSlider((NumberProperty) property));
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!items.isEmpty()) {
            ClickGui.getClickGui().guiFont.drawString("...", x - 1F + width - 8F, y - 2F, CustomFont.FontType.SHADOW_THIN, 0xFFFFFFFF);

            if (subOpen) {
                float height = 1;

                for (Item item : items) {
                    height += 15F;
                    item.setLocation(x + 1, y + height);
                    item.setHeight(15);
                    item.setWidth(width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!items.isEmpty()) {
            if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
                subOpen = !subOpen;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
            }

            if (subOpen) {
                for (Item item : items) {
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public int getHeight() {
        if (subOpen) {
            int height = 14;

            for (Item item : items) {
                height += item.getHeight() + 1;
            }

            return height + 2;
        } else {
            return 14;
        }
    }

    public void toggle() {
        if (module instanceof ToggleableModule) {
            ((ToggleableModule) module).toggle();
        }
    }

    public boolean getState() {
        if (module instanceof ToggleableModule) {
            return ((ToggleableModule) module).isRunning();
        } else {
            return true;
        }
    }
}
