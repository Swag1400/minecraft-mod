package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.EventTarget;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.utils.ClientUtils;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public final class AutoEat extends ToggleableModule
{
    private final NumberProperty<Double> hunger = new NumberProperty<>(8D, 0D, 10D, 0.5D, "Hunger", "hung");
    private final EnumProperty<Mode> mode = new EnumProperty<Mode>(Mode.VANILLA, "Mode","m");

    //event.getTime() == Time.BEFORE
    private final Stopwatch stopwatch = new Stopwatch();

    public AutoEat()
    {
        super("AutoEat", new String[] {"AutoEat", "ae"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.offerProperties(hunger,mode);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_eat_update_listener")
        {
            @EventTarget
            public void call(MotionUpdateEvent event)
            {
                if (event.getTime() == Time.BEFORE)
                {
                    final int foodSlot = this.getFoodSlotInHotbar();

                    if (foodSlot != -1 && ClientUtils.player().getFoodStats().getFoodLevel() < hunger.getValue() * 2.0 && ClientUtils.player().isCollidedVertically)
                    {
                        ClientUtils.player().sendQueue.addToSendQueue(new C09PacketHeldItemChange(foodSlot));
                        ClientUtils.player().sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(ClientUtils.player().inventory.mainInventory[foodSlot]));

                        for (int i = 0; i < 32; ++i)
                        {
                            ClientUtils.player().sendQueue.addToSendQueue(new C03PacketPlayer(false));
                        }

                        ClientUtils.player().stopUsingItem();
                        ClientUtils.player().sendQueue.addToSendQueue(new C09PacketHeldItemChange(ClientUtils.player().inventory.currentItem));
                    }
                }
            }
            private int getFoodSlotInHotbar()
            {
                for (int i = 0; i < 9; ++i)
                {
                    if (ClientUtils.player().inventory.mainInventory[i] != null && ClientUtils.player().inventory.mainInventory[i].getItem() != null && ClientUtils.player().inventory.mainInventory[i].getItem() instanceof ItemFood)
                    {
                        return i;
                    }
                }

                return -1;
            }
        });
    }
    private enum Mode {
    	VANILLA
    }
}
