package me.comu.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;

public class Helper
{
    private static InventoryUtils inventoryUtils = new InventoryUtils();

    public static Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }
    public static EntityPlayerSP player()
    {
        return mc().thePlayer;
    }
    public static WorldClient world()
    {
        return mc().theWorld;
    }
    public static void sendPacket(Packet p)
    {
        mc().func_175102_a().addToSendQueue(p);
    }
}
