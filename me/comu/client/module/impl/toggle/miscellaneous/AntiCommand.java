package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.network.play.client.C01PacketChatMessage;

public final class AntiCommand extends ToggleableModule
{
    public AntiCommand()
    {
        super("AntiCommand", new String[] {"anticommand", "antic"}, 0xFFC690D4, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<PacketEvent>("anti_command_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof C01PacketChatMessage)
                {
                    C01PacketChatMessage c01PacketChatMessage = (C01PacketChatMessage) event.getPacket();

                    if (c01PacketChatMessage.getMessage().startsWith("/"))
                    {
                        String message = String.format("\u08C7%s", c01PacketChatMessage.getMessage());
                        c01PacketChatMessage.setMessage(message);
                    }
                }
            }
        });
    }
}
