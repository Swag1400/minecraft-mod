package me.comu.client.keybind;

import me.comu.api.event.Listener;
import me.comu.api.registry.ListRegistry;
import me.comu.client.core.Gun;
import me.comu.client.events.InputEvent;
import me.comu.client.module.ToggleableModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public final class KeybindManager extends ListRegistry<Keybind>
{
    public KeybindManager()
    {
        registry = new ArrayList<>();
        Gun.getInstance().getEventManager().register(new Listener<InputEvent>("keybinds_input_listener")
        {
            @Override
            public void call(InputEvent event)
            {
                if (event.getType() == InputEvent.Type.KEYBOARD_KEY_PRESS)
                {
                    registry.forEach(keybind ->
                    {
                        if (keybind.getKey() != Keyboard.KEY_NONE && keybind.getKey() == event.getKey())
                        {
                            ToggleableModule toggleSounds = (ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("modulesounds");
                            if (toggleSounds.isRunning())
                                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("random.click"), 1F));
                            keybind.onPressed();
                        }
                    });
                }
            }
        });
    }

    public Keybind getKeybindByLabel(String label)
    {
        for (Keybind keybind : registry)
        {
            if (label.equalsIgnoreCase(keybind.getLabel()))
            {
                return keybind;
            }
        }

        return null;
    }
}
