package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

public final class AntiSpam extends ToggleableModule
{

    public AntiSpam()
    {
        super("AntiSpam", new String[] {"antispam", "anti-spam"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<PacketEvent>("auto_accept_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof S02PacketChat)
                {
                    S02PacketChat packet = new S02PacketChat();
                    IChatComponent chatComponent = packet.getChatComponent();
                    if (chatComponent.getUnformattedText().contains("Math Problem:"))
                    {

                    }

                }
            }
        });
    }
}
