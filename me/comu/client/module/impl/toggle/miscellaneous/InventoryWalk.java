package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.render.clickgui.ClickGui;
import me.comu.client.properties.Property;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public final class InventoryWalk extends ToggleableModule {

    private final Property<Boolean> chat = new Property<Boolean>(false, "Chat");


    // net.minecraft.utilMovementInputFromOptions

    public InventoryWalk() {
        super("InventoryWalk", new String[]{"inventorywalk", "iw", "invmove", "invwalk"}, ModuleType.MISCELLANEOUS);
        this.offerProperties(chat);
        this.listeners.add(new Listener<MotionUpdateEvent>("inventory_walk_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                KeyBinding[] keys = {minecraft.gameSettings.keyBindForward, minecraft.gameSettings.keyBindBack,
                        minecraft.gameSettings.keyBindLeft, minecraft.gameSettings.keyBindRight, minecraft.gameSettings.keyBindJump
                };
                if (minecraft.currentScreen instanceof GuiContainer || minecraft.currentScreen instanceof ClickGui /*|| chat.getValue() ? minecraft.currentScreen instanceof GuiChat : false*/) {
                    for (KeyBinding bind : keys) {
                        KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                    }

                    event.setLockview(true);

                    if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                        minecraft.thePlayer.rotationPitch -= 4F;
                    }

                    if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                        minecraft.thePlayer.rotationPitch += 4F;
                    }

                    if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                        minecraft.thePlayer.rotationYaw -= 5F;
                    }

                    if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                        minecraft.thePlayer.rotationYaw += 5F;
                    }

                    if (minecraft.thePlayer.rotationPitch > 90F) {
                        minecraft.thePlayer.rotationPitch = 90F;
                    }

                    if (minecraft.thePlayer.rotationPitch < -90F) {
                        minecraft.thePlayer.rotationPitch = -90F;
                    }
                } else if (minecraft.currentScreen == null) {
                    for (KeyBinding bind : keys) {
                        if (!Keyboard.isKeyDown(bind.getKeyCode())) {
                            KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                        }
                    }
                }

            }

        });
    }
}
