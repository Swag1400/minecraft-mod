package me.comu.client.macro;


import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.InputEvent;
import me.comu.client.plugin.ElementManager;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public final class MacroManager extends ElementManager<Macro> {



    public MacroManager() {

        elements = new ArrayList<>();


        Gun.getInstance().getEventManager().register(new Listener<InputEvent>("macro_input_listener")
        {
            @Override
            public void call(InputEvent event)
            {
                if (event.getType() == InputEvent.Type.KEYBOARD_KEY_PRESS)
                {
                    elements.forEach(macro ->
                    {
                        if (macro.getKey() != Keyboard.KEY_NONE && macro.getKey() == event.getKey())
                        {
                            macro.dispatch();
                        }
                    });
                }
            }
        });
    }

    public Macro getUsingKey(int key) {
        for (Macro macro : elements)
            if (key == macro.getKey())
                return macro;
        return null;
    }

    public boolean isMacro(int key) {
        for (Macro macro : elements)
            if (key == macro.getKey())
                return true;
        return false;
    }

    public void remove(int key) {
        Macro macro = getUsingKey(key);
        if (macro != null)
            elements.remove(macro);
    }

    public void resetMacros()
    {
        elements.clear();
    }
}
