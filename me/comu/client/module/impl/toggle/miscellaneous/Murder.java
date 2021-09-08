package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.StringUtils;

public class Murder extends ToggleableModule
{
    private static final String MINIGAME_BEGIN = "your secret identity is";
    private static final String[] MINIGAME_END = new String[] {"murderer was", "murderer has been", "you were killed", "has killed everyone"};
    private static final String MINIGAME_RESET = "murderer has left";
    private EntityPlayer murderer;
    private boolean looking;

    public Murder()
    {
        super("Murder", new String[] {"murder"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("murderer_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                // Check if there is a murderer
                if (murderer != null || !looking)
                {
                    // Don't do anything
                    return;
                }

                // Create a local player entity
                EntityPlayerSP player = minecraft.thePlayer;

                // Loop through every player entity
                for (Object object : player.getEntityWorld().playerEntities)
                {
                    EntityPlayer target = (EntityPlayer) object;

                    // Check if the target is you or dead
                    if (target == player || !target.isEntityAlive())
                    {
                        // Don't do anything
                        continue;
                    }

                    // Check if the target is sprinting
                    if (isMurderer(target))
                    {
                        // Set the murderer as the target
                        murderer = target;
                        // No longer look for a murderer
                        looking = false;
                        // Alert
                        Logger.getLogger().printToChat(minecraft.thePlayer + "The murderer is \247c%s\2477." + target.getCommandSenderEntity());
                        //    return (minecraft.thePlayer, "The murderer is \247c%s\2477.", target.getCommandSenderEntity());
                        break;
                    }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("murderer_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {

                // Create a castest S02PacketChat instance
                S02PacketChat packet = (S02PacketChat) event.getPacket();
                // Create a local instance of the message
                String message = StringUtils.stripControlCodes(packet.getChatComponent().getUnformattedText()).toLowerCase();

                // Check if the packet contains the beginning message
                if (message.contains(MINIGAME_BEGIN))
                {
                    // Set the murderer to null
                    murderer = null;
                    // Set up the mod for the beginning of the game
                    looking = true;
                    // Don't do anything else
                    return;
                }

                // Check if it's a message from the server
                if (!message.startsWith("murder"))
                {
                    // It's not from the server so don't do anything
                    return;
                }

                // Check if it's a reset message
                if (message.contains(MINIGAME_RESET))
                {
                    // Set the murderer to null and break
                    murderer = null;
                    // Set looking to false
                    looking = true;
                    // Stop anything else
                    return;
                }

                // Loop through every ending message
                for (String ending : MINIGAME_END)
                {
                    // Check if the message contains it
                    if (message.contains(ending))
                    {
                        // Set the murderer to null and break
                        murderer = null;
                        // Set looking to false
                        looking = false;
                    }
                }
            }
        });
    }

    private boolean isMurderer(EntityPlayer player)
    {
        // Check if the player is sprinting
        if (player.isSprinting())
        {
            return true;
        }

        // Check if the player is holding a sword
        if (player.getHeldItem() != null)
        {
            // Get the current held item
            ItemStack itemStack = player.getHeldItem();

            // Check if it's an instance of a sword
            if (itemStack.getItem() instanceof ItemSword)
            {
                // Return true; they're the murderer
                return true;
            }
        }

        // Return false
        return false;
    }
}
