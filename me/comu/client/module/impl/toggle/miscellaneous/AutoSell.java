package me.comu.client.module.impl.toggle.miscellaneous;


import org.lwjgl.input.Mouse;

import me.comu.api.event.Listener;
import me.comu.client.events.EventTarget;
import me.comu.client.events.InputEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.Helper;

public final class AutoSell extends ToggleableModule
{

    public AutoSell()
    {
        super("AutoSell", new String[] {"autosell", "auto-sell", "sell","prison"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<InputEvent>("auto_eat_update_listener")
        {
            @EventTarget
            public void call(InputEvent e)
            {
            	if (minecraft.currentScreen == null) {
            		if (Mouse.isButtonDown(1)) {
            			Helper.player().sendChatMessage("/sellall");
            		}
            	}
            	
            	
               
            }
            	
        });

   		

        
  
    }
 
}

// TODO: get instance of limbo in chat then player send message for /pig walk forward and go right 