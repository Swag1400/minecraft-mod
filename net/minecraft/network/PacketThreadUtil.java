package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    public static void func_180031_a(final Packet packet, final INetHandler netHandler, IThreadListener threadListener)
    {

        if (!threadListener.isCallingFromMinecraftThread())
        {
            threadListener.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    packet.processPacket(netHandler);

                }
            });
            throw ThreadQuickExitException.field_179886_a;
        }
    }
}
