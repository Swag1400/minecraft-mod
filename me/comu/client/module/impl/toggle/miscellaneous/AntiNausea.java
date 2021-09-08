package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.NauseaEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

/**
 * Created by comu on 4/20/2018
 */
public final class AntiNausea extends ToggleableModule {


    public AntiNausea() {
        super("AntiNausea", new String[] {"antidizzy", "antidaze","nausea","nonausea"}, 0xFFC690D4, ModuleType.RENDER);
        this.listeners.add(new Listener<NauseaEvent>("anti_command_packet_listener") {

        @Override
        public void call(NauseaEvent event) {
        event.setCanceled(true);
        }
    });
    }

}
