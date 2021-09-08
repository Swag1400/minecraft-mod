package me.comu.client.macro;

import net.minecraft.client.Minecraft;

public class Macro {

    private final int key;
    private final MacroDispatchAction action;

    public Macro(int key, String action) {
        this.key = key;
        this.action = new MacroDispatchAction(action);
    }

    public final int getKey() {
        return key;
    }

    public final MacroDispatchAction getAction() {
        return action;
    }

    public void dispatch() {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(action.getAction().replace("_", " "));
    }

}
