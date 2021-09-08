package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

public final class AutoBuild extends ToggleableModule {

    public AutoBuild() {
        super("AutoBuild", new String[]{"autobuild", "ab"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_accept_packet_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                
            }
        });
    }
    @Override
    public void onEnable()
    {
        minecraft.gameSettings.keyBindUseItem.pressed = true;
    }
    @Override
    public void onDisable()
    {
        minecraft.gameSettings.keyBindUseItem.pressed = false;
    }
}
