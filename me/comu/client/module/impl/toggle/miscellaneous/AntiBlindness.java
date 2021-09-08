package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.BlindnessEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.potion.Potion;

/**
 * Created by comu on 4/20/2018
 */
public final class AntiBlindness extends ToggleableModule {

    Potion potion = Potion.blindness;

    public AntiBlindness() {
        super("AntiBlindness", new String[] {"antiblind", "noblind"}, 0xFFC690D4, ModuleType.RENDER);
        this.listeners.add(new Listener<BlindnessEvent>("anti_command_packet_listener") {

        @Override
        public void call(BlindnessEvent event) {
        event.setCanceled(true);
        }
    });
    }

}
