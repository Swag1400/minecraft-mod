package me.comu.client.module.impl.toggle.render.tabgui;

import me.comu.api.interfaces.Toggleable;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.core.Gun;
import me.comu.client.module.Module;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.render.tabgui.item.GuiItem;
import me.comu.client.module.impl.toggle.render.tabgui.item.GuiTab;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GuiTabHandler {
    private Minecraft mc = Minecraft.getMinecraft();
    private float width = 0.7F;
    private int guiHeight = 0;
    public boolean mainMenu = true;
    public boolean shouldShowProperties = false;
    public boolean isInPropertyFocus = false;
    public int propertyIndex = 0;
    public int selectedItem = 0;
    public int selectedTab = 0;
    private int tabHeight = 12;
    public final ArrayList<GuiTab> tabs = new ArrayList<>();
    public int transition = 0;
    public boolean visible = true;

    public GuiTabHandler() {


        // TODO: add properties to tabgui


        List<Module> modules = Gun.getInstance().getModuleManager().getRegistry();
        modules.sort((mod1, mod2) -> mod1.getLabel().compareTo(mod2.getLabel()));


        if (selectedTab == 0 && selectedItem == 8) {
            Gui.drawRect(tabHeight, guiHeight, selectedTab, guiHeight, 255);
        }
        for (ModuleType moduleType : ModuleType.values()) {

            GuiTab guiTab = new GuiTab(this, moduleType.getLabel());
            modules.forEach(module ->

            {
                if (module instanceof Toggleable) {
                    ToggleableModule toggle = (ToggleableModule) module;
                    List<?> properties = module.getProperties();


                    if (toggle.getModuleType() == moduleType && toggle.getProperties() == properties && !toggle.getLabel().equalsIgnoreCase("Click Gui") && !toggle.getLabel().equalsIgnoreCase("Tab Gui")) {

                        guiTab.getMods().add(new GuiItem(toggle));
//                        guiTab.getProperties().add(new GuiPropertyItem(toggle.getProperties()));

                    }

                }
            });
            this.tabs.add(guiTab);
        }

        tabs.sort(Comparator.comparing(GuiTab::getLabel));
        this.guiHeight = this.tabs.size() * this.tabHeight;
    }

    public void drawGui(int x, int y) {
        if (!this.visible) {
            return;
        }

        int guiWidth = 73;
        RenderMethods.enableGL2D();
        RenderMethods.drawBorderedRectReliant(x, y - 0.4F, x + guiWidth - 2, y + this.guiHeight + 0.4F, 1.5F, 0x66000000, 0x88000000);

        for (int i = 0; i < tabs.size(); i++) {
            int transitionTop = !this.mainMenu ? 0 : this.transition + (this.selectedTab == 0 && this.transition < 0 ? -this.transition : 0);
            int transitionBottom = !this.mainMenu ? 0 : this.transition + (this.selectedTab == this.tabs.size() - 1 && this.transition > 0 ? -this.transition : 0);

            if (this.selectedTab == i) {
                RenderMethods.drawGradientBorderedRectReliant(x, i * 12 + y + transitionTop - 0.3F, x + guiWidth - 2.2F, i + (y + 12 + (i * 11)) + transitionBottom + 0.3F, 1.5F, 0x88000000, 0xFF610342, 0xFF610342);
            }
        }

        int yOff = y + 2;
        if (shouldShowProperties) {
            ToggleableModule module = ((tabs.get(selectedTab)).getMods().get(selectedItem)).getToggleableModule();
            switch (selectedTab) {
                case 0:
                    RenderMethods.drawBorderedRectReliant(2 * (x + guiWidth - 2.2F), y - 0.4F,
                            x + guiWidth * 3.7f,
                            y + module.getProperties().size() + (mc.fontRenderer.FONT_HEIGHT * module.getProperties().size()),
                            1.5F, 0x66000000, 0x88000000);
                    break;
                case 1:
                    RenderMethods.drawBorderedRectReliant(2 * (x + guiWidth + 8F), y + 11.5f,
                            x + guiWidth * 3.7f,
                            y + 13f + module.getProperties().size() + mc.fontRenderer.FONT_HEIGHT *module.getProperties().size(),
                            1.5F, 0x66000000, 0x88000000);
                    break;
                case 2:
                    RenderMethods.drawBorderedRectReliant(2 * (x + guiWidth + 10.5F), y + 24F,
                            x + guiWidth * 3.7f,
                             y + 25f + module.getProperties().size() + mc.fontRenderer.FONT_HEIGHT *module.getProperties().size(),
                            1.5F, 0x66000000, 0x88000000);
                    break;
                case 3:
                    RenderMethods.drawBorderedRectReliant(2 * (x + guiWidth - 4), y + 35.5f,
                            x + guiWidth * 3.7f,
                            y + 37f + module.getProperties().size() + mc.fontRenderer.FONT_HEIGHT *module.getProperties().size(),
                            1.5F, 0x66000000, 0x88000000);
                    break;
                case 4:
                    RenderMethods.drawBorderedRectReliant(2 * (x + guiWidth + .5f), y + 47.5f,
                            x + guiWidth * 3.7f,
                            y + 49f + module.getProperties().size() + mc.fontRenderer.FONT_HEIGHT *module.getProperties().size(),
                            1.5F, 0x66000000, 0x88000000);
                    break;
                case 5:
                    RenderMethods.drawBorderedRectReliant(2 * (x + guiWidth - 1.5F), y + 59.5F,
                            x + guiWidth * 3.7f,
                            y + 60f + module.getProperties().size() + mc.fontRenderer.FONT_HEIGHT *module.getProperties().size(),
                            1.5F, 0x66000000, 0x88000000);
                    break;

            }
//            RenderMethods.drawBorderedRectReliant(2*(x + guiWidth - 2.2F), y - 0.4F, x +guiWidth * 3.7f, y + this.guiHeight + module.getProperties().size()*7.45f, 1.5F, 0x66000000, 0x88000000);\
//            RenderMethods.drawGradientBorderedRectReliant(2*(x + guiWidth - 2.2F),  y-0.4f, getPropertyWidth(module) * 3.7f, y-3f + this.guiHeight/5.3f, 1.5F, 0x88000000, 0xFF610342, 0xFF610342);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        for (int index = 0; index < this.tabs.size(); index++) {
            GuiTab tab = this.tabs.get(index);
            mc.fontRenderer.drawStringWithShadow(tab.getLabel(), x + 2, yOff, 0xfdfeff);
            if (this.selectedTab == index && !this.mainMenu) {
                tab.drawTabMenu(this.mc, x + guiWidth, yOff - 2);
                if (shouldShowProperties) {
                    ToggleableModule module = ((tabs.get(selectedTab)).getMods().get(selectedItem)).getToggleableModule();
                    int yyOff = yOff - 11;
                    for (int index2 = 0; index2 < module.getProperties().size(); index2++) {
                        yyOff += 10;
                        Property<?> property = module.getProperties().get(index2);
                        switch (index) {
                            case 0:
                                if (property instanceof NumberProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + decimalFormat.format(property.getValue()) : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * 2, yyOff, 0xFFFCFCFC);
                                } else if (property instanceof EnumProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * 2, yyOff, 0xFFFCFCFC);
                                } else {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * 2, yyOff, 0xFFFCFCFC);
                                }
                                break;
                            case 1:
                                if (property instanceof NumberProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + decimalFormat.format(property.getValue()) : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index * 2.3f - 2, yyOff, 0xFFFCFCFC);
                                } else if (property instanceof EnumProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index * 2.3f - 2, yyOff, 0xFFFCFCFC);
                                } else {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index * 2.3f - 2, yyOff, 0xFFFCFCFC);
                                }
                                break;
                            case 2:
                                if (property instanceof NumberProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + decimalFormat.format(property.getValue()) : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index + 27, yyOff, 0xFFFCFCFC);
                                } else if (property instanceof EnumProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index + 27, yyOff, 0xFFFCFCFC);
                                } else {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index + 27, yyOff, 0xFFFCFCFC);
                                }
                                break;
                            case 3:
                                if (property instanceof NumberProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + decimalFormat.format(property.getValue()) : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2f + 34, yyOff, 0xFFFCFCFC);
                                } else if (property instanceof EnumProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2f + 34, yyOff, 0xFFFCFCFC);
                                } else {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2f + 34, yyOff, 0xFFFCFCFC);
                                }
                                break;
                            case 4:
                                if (property instanceof NumberProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + decimalFormat.format(property.getValue()) : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2f + 6, yyOff, 0xFFFCFCFC);
                                } else if (property instanceof EnumProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2f + 6, yyOff, 0xFFFCFCFC);
                                } else {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2f + 6, yyOff, 0xFFFCFCFC);
                                }
                                break;
                            case 5:
                                if (property instanceof NumberProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + decimalFormat.format(property.getValue()) : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2.47f, yyOff, 0xFFFCFCFC);
                                } else if (property instanceof EnumProperty) {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2.47f, yyOff, 0xFFFCFCFC);
                                } else {
                                    mc.fontRenderer.drawStringWithShadow((propertyIndex == index2) ? (isInPropertyFocus ? "\2475> \247f" : "> ") + property.getAliases()[0] + " : " + property.getValue() : property.getAliases()[0] + " : " + property.getValue(), x + guiWidth * index / 2.47f, yyOff, 0xFFFCFCFC);
                                }
                                break;
                        }
                    }


                }
            }

            yOff += this.tabHeight;

        }

        if (this.transition > 0) {
            this.transition -= 1;
        } else if (this.transition < 0) {
            this.transition += 1;
        }

        RenderMethods.disableGL2D();
    }

    public int getPropertyWidth(ToggleableModule module) {
        int maxWidth = 0;
        for (Property property : module.getProperties()) {
            if (mc.fontRenderer.getStringWidth(property.getAliases()[0]) > maxWidth) {
                maxWidth = mc.fontRenderer.getStringWidth(property.getAliases()[0]) + 4;
            }
        }
        return maxWidth;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public int getTabHeight() {
        return tabHeight;
    }

    public int getTransition() {
        return transition;
    }
}
