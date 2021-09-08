package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * Created by comu on 9/30/2018
 */
public class LeetSpeak extends ToggleableModule {

    private final Property<Boolean> message = new Property<>(true, "Message","m","msg","msgs","messages");

    public LeetSpeak()
    {
        super("1337Speak", new String[] {"leetspeak", "1337","leet","speak"}, 0x444A0, ModuleType.MISCELLANEOUS);
        this.offerProperties(message);
        this.listeners.add(new Listener<PacketEvent>("x_carry_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            { // FIX /MSG
                if (minecraft.currentScreen != null && event.getPacket() instanceof C01PacketChatMessage)
                {
                    C01PacketChatMessage c01PacketChatMessage = (C01PacketChatMessage) event.getPacket();
                    String[] args = c01PacketChatMessage.getMessage().split("\\s+");
//                    if (c01PacketChatMessage.getMessage().startsWith("/r") && message.getValue() || c01PacketChatMessage.getMessage().startsWith("/m") || c01PacketChatMessage.getMessage().startsWith("/msg") || c01PacketChatMessage.getMessage().startsWith("/pm") || c01PacketChatMessage.getMessage().startsWith("/tell") || c01PacketChatMessage.getMessage().startsWith("/whisper"))
//                    {
//                        final String messageMsg = c01PacketChatMessage.getMessage().replaceAll("A", "4").replaceAll("a", "4").replaceAll("E", "3").replaceAll("e", "3").replaceAll("I", "!").replaceAll("i", "!").replaceAll("O", "0").replaceAll("o", "0").replaceAll("S", "5").replaceAll("s", "5");
//                        ((C01PacketChatMessage) event.getPacket()).setMessage(messageMsg);
//                    }
//                    if (args[0].equalsIgnoreCase("/r") && message.getValue() || args[0].equalsIgnoreCase("/m") || args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/pm") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/whisper"))
//                    {
//                        final String messageMsg = c01PacketChatMessage.getMessage().replaceAll("A", "4").replaceAll("a", "4").replaceAll("E", "3").replaceAll("e", "3").replaceAll("I", "!").replaceAll("i", "!").replaceAll("O", "0").replaceAll("o", "0").replaceAll("S", "5").replaceAll("s", "5");
//                        ((C01PacketChatMessage) event.getPacket()).setMessage(args[1] + " " + messageMsg.replace(args[1], " "));
//                    }
                    if (c01PacketChatMessage.getMessage().startsWith("/r") && message.getValue() || c01PacketChatMessage.getMessage().startsWith("/m") || c01PacketChatMessage.getMessage().startsWith("/msg") || c01PacketChatMessage.getMessage().startsWith("/pm") || c01PacketChatMessage.getMessage().startsWith("/tell") || c01PacketChatMessage.getMessage().startsWith("/whisper"))
                    {
                        final String messageMsg = c01PacketChatMessage.getMessage().replaceAll("A", "4").replaceAll("a", "4").replaceAll("E", "3").replaceAll("e", "3").replaceAll("I", "!").replaceAll("i", "!").replaceAll("O", "0").replaceAll("o", "0");
                        ((C01PacketChatMessage) event.getPacket()).setMessage(messageMsg);
                    }
                    if (c01PacketChatMessage.getMessage().startsWith("/") || c01PacketChatMessage.getMessage().startsWith(Gun.getInstance().getCommandManager().getPrefix()))
                    {
                        return;
                    }
                    final String newMsg = c01PacketChatMessage.getMessage().replaceAll("A", "4").replaceAll("a", "4").replaceAll("E", "3").replaceAll("e", "3").replaceAll("I", "!").replaceAll("i", "!").replaceAll("O", "0").replaceAll("o", "0").replaceAll("S", "5").replaceAll("s", "5");
                    c01PacketChatMessage.setMessage(newMsg);
                }
            }
//            @Override
//            public void call(PacketEvent e)
//            {
//                if (e.getMessage().startsWith("/") || e.getMessage().startsWith(".")) {
//                    return;
//
//                }
//                final String newMsg = e.getMessage().replaceAll("A", "4").replaceAll("a", "4").replaceAll("E", "3").replaceAll("e", "3").replaceAll("I", "!").replaceAll("i", "!").replaceAll("O", "0").replaceAll("o", "0").replaceAll("S", "5").replaceAll("s", "5");
//                minecraft.getNetHandler().addToSendQueue(new C01PacketChatMessage(newMsg));
//            }
        });
    }
}


