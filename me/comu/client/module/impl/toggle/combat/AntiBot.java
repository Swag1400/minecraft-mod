package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.client.events.PacketEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class AntiBot extends ToggleableModule
{
    private static List<EntityPlayer> invalid = new ArrayList();


    public AntiBot()
    {
        super("AntiBot", new String[] {"antibot", "bot", "ab"}, 0xFF1BCC8B, ModuleType.COMBAT);
        this.offerProperties();
        this.listeners.add(new Listener<PacketEvent>("antibot_player_packet_event")
        {
            public void call(PacketEvent event)
            {
                if ((event.getPacket() instanceof S0CPacketSpawnPlayer))
                {
                    S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
                    double entX = packet.func_148942_f() / 32;
                    double entY = packet.func_148949_g() / 32;
                    double entZ = packet.func_148946_h() / 32;
                    double posX = minecraft.thePlayer.posX;
                    double posY = minecraft.thePlayer.posY;
                    double posZ = minecraft.thePlayer.posZ;
                    double var7 = posX - entX;
                    double var9 = posY - entY;
                    double var11 = posZ - entZ;
                    float distance = MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);

                    if ((distance <= 17.0F) && (entY > minecraft.thePlayer.posY + 5D) && (minecraft.thePlayer.posX != entX) && (minecraft.thePlayer.posY != entY) && (minecraft.thePlayer.posZ != entZ))
                    {
                        Logger.getLogger().printToChat("Bot removed at " + entX + ", " + entY + ", " + entZ);
                        event.setCanceled(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        invalid.clear();
    }
}