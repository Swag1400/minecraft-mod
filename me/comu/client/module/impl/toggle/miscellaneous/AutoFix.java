package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.EventTarget;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.item.ItemStack;

/**
 * Created by comu on 12/24/2018
 */
public class AutoFix extends ToggleableModule {

    private final Stopwatch stopwatch = new Stopwatch();
    int var = 0;
    public AutoFix() {
        super("AutoFix", new String[]{"AutoFix", "fx", "fixhand", "auto-fixhand"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_eat_update_listener") {
            @EventTarget
            public void call(MotionUpdateEvent e) {
                if (minecraft.currentScreen != null) {
                    ItemStack item = minecraft.thePlayer.getCurrentEquippedItem();
                    if (item.getItemDamage() < 10) {
                        minecraft.thePlayer.sendChatMessage("/fix hand");
                    }
                }


            }

        });
    }
}
