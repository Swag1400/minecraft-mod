package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.InputEvent;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.events.RenderGameOverlayEvent.Type;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.module.impl.toggle.render.tabgui.GuiTabHandler;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public final class TabGui extends ToggleableModule {
    private static final ResourceLocation iconCombat = new ResourceLocation("textures/misc/combaticon");
    private final Property<Boolean> icons = new Property<>(true, "Icons", "icon", "ic", "i");
    private final EnumProperty<Theme> theme = new EnumProperty<>(Theme.EXETER, "Theme", "t");
    private GuiTabHandler guiTabHandler;

    public TabGui() {

        super("TabGui", new String[]{"tabgui", "tg"}, ModuleType.RENDER);
        this.offerProperties(theme, icons);
        this.listeners.add(new Listener<RenderGameOverlayEvent>("tab_gui_render_game_overlay_listener") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                if (event.getType() == Type.GUI) {
                    TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                    Property<Boolean> watermark = textGUI.getPropertyByAlias("Watermark");

                    if (guiTabHandler == null) {
                        guiTabHandler = new GuiTabHandler();
                    }

                    if (minecraft.gameSettings.showDebugInfo) {
                        return;
                    }

                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    guiTabHandler.drawGui(3, watermark.getValue() ? 13 : 3);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        });
        this.listeners.add(new Listener<InputEvent>("tab_gui_input_listener") {
            @Override
            public void call(InputEvent event) {
                if (event.getType() == InputEvent.Type.KEYBOARD_KEY_PRESS) {
                    switch (event.getKey()) {
                        case Keyboard.KEY_UP: {
                            if (guiTabHandler.visible) {
                                if (guiTabHandler.isInPropertyFocus)
                                {
                                    ToggleableModule module = ((guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().get(guiTabHandler.selectedItem)).getToggleableModule();
                                    Property property = module.getProperties().get(guiTabHandler.propertyIndex);
                                    if (property instanceof NumberProperty)
                                    {
                                        ((NumberProperty) property).increment();
                                    } else if (property instanceof EnumProperty)
                                    {
                                        ((EnumProperty) property).increment();
                                    } else {
                                        property.setValue(!(Boolean)property.getValue());
                                    }
                                    return;
                                }
                                if (guiTabHandler.shouldShowProperties)
                                {
                                    ToggleableModule module = ((guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().get(guiTabHandler.selectedItem)).getToggleableModule();
                                    if (guiTabHandler.propertyIndex == 0) {
                                        guiTabHandler.propertyIndex = module.getProperties().size()-1;
                                    } else {
                                        guiTabHandler.propertyIndex--;
                                    }
                                    return;
                                }
                                if (guiTabHandler.mainMenu) {
                                    guiTabHandler.selectedTab--;

                                    if (guiTabHandler.selectedTab < 0) {
                                        guiTabHandler.selectedTab = guiTabHandler.tabs.size() - 1;
                                    }

                                    guiTabHandler.transition = 11;
                                } else {
                                    guiTabHandler.selectedItem--;

                                    if (guiTabHandler.selectedItem < 0) {
                                        guiTabHandler.selectedItem = (guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().size() - 1;
                                    }

                                    if (guiTabHandler.tabs.get(guiTabHandler.selectedTab).getMods().size() > 1) {
                                        guiTabHandler.transition = 11;
                                    }
                                }
                            }

                            break;
                        }

                        case Keyboard.KEY_DOWN: {
                            if (guiTabHandler.visible) {
                                if (guiTabHandler.isInPropertyFocus)
                                {
                                    ToggleableModule module = ((guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().get(guiTabHandler.selectedItem)).getToggleableModule();
                                    Property property = module.getProperties().get(guiTabHandler.propertyIndex);
                                   if (property instanceof NumberProperty)
                                   {
                                    ((NumberProperty) property).decrement();
                                   } else if (property instanceof EnumProperty)
                                   {
                                    ((EnumProperty) property).decrement();
                                   } else {
                                       property.setValue(!(Boolean)property.getValue());
                                   }
                                    return;
                                }
                                if (guiTabHandler.shouldShowProperties)
                                {
                                        ToggleableModule module = ((guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().get(guiTabHandler.selectedItem)).getToggleableModule();
                                        if (guiTabHandler.propertyIndex == module.getProperties().size()-1) {
                                            guiTabHandler.propertyIndex = 0;
                                        } else {
                                            guiTabHandler.propertyIndex++;
                                        }
                                    return;
                                }
                                if (guiTabHandler.mainMenu) {
                                    guiTabHandler.selectedTab++;

                                    if (guiTabHandler.selectedTab > guiTabHandler.tabs.size() - 1) {
                                        guiTabHandler.selectedTab = 0;
                                    }

                                    guiTabHandler.transition = -11;
                                } else {
                                    guiTabHandler.selectedItem++;

                                    if (guiTabHandler.selectedItem > (guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().size() - 1) {
                                        guiTabHandler.selectedItem = 0;
                                    }

                                    if (guiTabHandler.tabs.get(guiTabHandler.selectedTab).getMods().size() > 1) {
                                        guiTabHandler.transition = -11;
                                    }
                                }
                            }

                            break;
                        }

                        case Keyboard.KEY_LEFT: {
                            if (guiTabHandler.isInPropertyFocus)
                            {
                                guiTabHandler.isInPropertyFocus = false;
                                return;
                            }
                            if (guiTabHandler.shouldShowProperties)
                            {
                                guiTabHandler.shouldShowProperties = false;
                                guiTabHandler.propertyIndex = 0;
                                return;
                            }
                            if (!guiTabHandler.mainMenu) {
                                Waypoints.shouldRender = true;
                                guiTabHandler.mainMenu = true;
                            }

                            break;
                        }

                        case Keyboard.KEY_RIGHT: {
                            if (guiTabHandler.isInPropertyFocus)
                            {
                                guiTabHandler.isInPropertyFocus = false;
                                return;
                            }
                            if (guiTabHandler.shouldShowProperties)
                            {
                                guiTabHandler.isInPropertyFocus = true;
                                return;
                            }
                            if (guiTabHandler.mainMenu) {
                                Waypoints.shouldRender = false;
                                guiTabHandler.mainMenu = false;
                                guiTabHandler.selectedItem = 0;
                            } else if (!guiTabHandler.visible) {
                                guiTabHandler.visible = true;
                                guiTabHandler.mainMenu = true;
                            } else {
                                ToggleableModule toggleSounds = (ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("modulesounds");
                                if (toggleSounds.isRunning())
                                    minecraft.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
                                ((guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().get(guiTabHandler.selectedItem)).getToggleableModule().toggle();
                            }

                            break;
                        }

                        case Keyboard.KEY_RETURN: {
                            if (guiTabHandler.isInPropertyFocus)
                            {
                                guiTabHandler.isInPropertyFocus = false;
                                return;
                            }
                            if (guiTabHandler.shouldShowProperties)
                            {
                                guiTabHandler.isInPropertyFocus = true;
                            }
                            if (!guiTabHandler.mainMenu && guiTabHandler.visible) {
                                ToggleableModule module = ((guiTabHandler.tabs.get(guiTabHandler.selectedTab)).getMods().get(guiTabHandler.selectedItem)).getToggleableModule();
                                if (module.getProperties().isEmpty()) {
                                    ToggleableModule toggleSounds = (ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("modulesounds");
                                    if (toggleSounds.isRunning())
                                        minecraft.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
                                    module.toggle();
                                } else {
                                    guiTabHandler.shouldShowProperties = true;
                                }
                            }

                            break;
                        }
                    }
                }
            }
        });
        this.listeners.add(new Listener<RenderGameOverlayEvent>("tab_gui_input_listener") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                ScaledResolution sr = event.getScaledResolution();
                if (icons.getValue()) {
                    //           getClassLoader().getResource("textures/misc/combaticon");
                    //         minecraft.getTextureManager().bindTexture(iconCombat);
                    //       minecraft.getTextureManager().bindTexture(iconExploits);
                    //     minecraft.getTextureManager().bindTexture(iconExploits);
                    //   minecraft.getTextureManager().bindTexture(iconMovement);
                    // minecraft.getTextureManager().bindTexture(iconRender);
                    //minecraft.getTextureManager().bindTexture(iconMovement);


                }
            }
        });
        setRunning(true);
    }
    private enum Theme {
    LUCID, EXETER
    }
}