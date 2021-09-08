package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.BlindnessEvent;
import me.comu.client.events.NauseaEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;

public class AntiDebuff extends ToggleableModule {

    private final Property<Boolean> blindness = new Property<>(true, "Blindness","blind","b");
    private final Property<Boolean> nausea = new Property<>(true, "Nausea","confusion","n", "c");

    public AntiDebuff() {
        super("AntiDebuff", new String[] {"AntiDebuff", "antidebuffs","anti-debuffs","antiblindness","antiblind","blind","noblind","noblindness","antinausea","nonausea","nausea"}, 0xFFC690D4, ModuleType.RENDER);
        this.offerProperties(blindness, nausea);
        this.listeners.add(new Listener<BlindnessEvent>("anti_command_packet_listener") {
            @Override
            public void call(BlindnessEvent event) {
                if (blindness.getValue())
                event.setCanceled(true);
            }
        });
        this.listeners.add(new Listener<NauseaEvent>("anti_command_packet_listener") {
            @Override
            public void call(NauseaEvent event) {
                if (nausea.getValue())
                event.setCanceled(true);
            }
        });
    }

}
