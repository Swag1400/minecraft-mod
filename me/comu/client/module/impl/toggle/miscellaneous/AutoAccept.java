package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.network.play.server.S02PacketChat;

public final class AutoAccept extends ToggleableModule
{
    private final Property<Boolean> factions = new Property<>(true, "Factions", "f");
    private final Property<Boolean> friends = new Property<>(true, "Friend", "frnd");
    private final Stopwatch stopwatch = new Stopwatch();

    public AutoAccept()
    {
        super("AutoAccept", new String[] {"autoaccept", "aa", "tpaccept"}, ModuleType.MISCELLANEOUS);
        this.offerProperties(factions, friends);
        this.listeners.add(new Listener<PacketEvent>("auto_accept_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof S02PacketChat)
                {
                    S02PacketChat chat = (S02PacketChat) event.getPacket();
                    String message = chat.getChatComponent().getUnformattedText();
                    Gun.getInstance().getFriendManager().getRegistry().forEach(friend -> {

                            if ((message.contains(friend.getLabel()) || message.contains(friend.getAlias()) && friends.getValue()))
                {
                    if (stopwatch.hasCompleted(500))
                        {
                            if (message.contains("has requested to teleport") && stopwatch.hasCompleted(500))
                            {
                                minecraft.thePlayer.sendChatMessage("/tpyes " + friend.getLabel());
                            }

                            if (message.contains("invited you to") && factions.getValue())
                            {
                                minecraft.thePlayer.sendChatMessage("/f join " + friend.getLabel());
                            }
                        }

                        stopwatch.reset();
                    }
                                                                                            });
                    if (!friends.getValue()) {
                        if (stopwatch.hasCompleted(500))
                        {
                            if (message.contains("has requested to teleport") && stopwatch.hasCompleted(500))
                            {
                                minecraft.thePlayer.sendChatMessage("/tpyes");
                        }

                        stopwatch.reset();
                    }
                    }
                }
            }
        });
    }
}
