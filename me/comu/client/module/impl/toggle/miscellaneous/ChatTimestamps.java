package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class ChatTimestamps extends ToggleableModule
{

    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.TWELVE, "Mode", "m");

    public ChatTimestamps()
    {
        super("ChatTimestamps", new String[] {"chatstamps", "chatstamp","timestamp","timestamps"},0xFF4BCFE3, ModuleType.MISCELLANEOUS);
        this.offerProperties(mode);
        this.listeners.add(new Listener<PacketEvent>("chat_time_stamp_packet_listener") {
            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof S02PacketChat) {
                    final S02PacketChat packet = (S02PacketChat) event.getPacket();


                        final IChatComponent component = packet.getChatComponent();
                    if (component instanceof ChatComponentText) {
                        String date = "";

                        switch (mode.getValue()) {
                            case TWELVE:
                                date = new SimpleDateFormat("h:mm a").format(new Date());
                                break;
                            case TWENTY_FOUR:
                                date = new SimpleDateFormat("k:mm").format(new Date());
                                break;
                        }
                        String format = "\2477[" + date + "]\247r ";
                        if (component.getSiblings().size() > 0) {
                            component.getSiblings().add(0, new ChatComponentText(format));
                        }
                    }


                }
            }

        });
    }
    private enum Mode {
        TWELVE, TWENTY_FOUR
    }

}