package me.comu.client.macro;


import me.comu.client.keybind.Action;
import net.minecraft.client.Minecraft;

public class MacroDispatchAction extends Action {

    private final String action;

    public MacroDispatchAction(String action) {
        this.action = action;
    }

    
    public void dispatch() {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(action);
    }

    public final String getAction() {
        return action;
    }
}
