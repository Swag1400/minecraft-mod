package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.client.settings.KeyBinding;

public final class AutoMine extends ToggleableModule {

    public AutoMine() {
        super("AutoMine", new String[]{"automine", "am"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<TickEvent>("auto_accept_packet_listener") {
            public void call(TickEvent event) {
                if (minecraft.currentScreen == null) {
                    KeyBinding.setKeyBindState(-100, true);
                    KeyBinding.onTick(-100);
                } else {
                    KeyBinding.setKeyBindState(-100, false);
                }
            }

        });
    }

}
