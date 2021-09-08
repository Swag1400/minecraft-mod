package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.CloseInventoryEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.network.play.client.C0DPacketCloseWindow;

public final class XCarry extends ToggleableModule
{
    public XCarry()
    {
        super("XCarry", new String[] {"xcarry", "morecarry"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<PacketEvent>("x_carry_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof C0DPacketCloseWindow)
                {
                    C0DPacketCloseWindow packet = (C0DPacketCloseWindow) event.getPacket();
                    event.setCanceled(packet.getWindowId() == 0);
                }
            }
        });
        this.listeners.add(new Listener<CloseInventoryEvent>("xcarry_close_inventory_listener")
        {
            @Override
            public void call(CloseInventoryEvent event)
            {
                event.setCanceled(true);
            }
        });
    }
}
