package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S03PacketTimeUpdate implements Packet
{
    private long totalWorldTime;
    private long worldTime;
    private static final String __OBFID = "CL_00001337";

    public S03PacketTimeUpdate()
    {
    }

    public S03PacketTimeUpdate(long p_i45230_1_, long p_i45230_3_, boolean p_i45230_5_)
    {
        this.totalWorldTime = p_i45230_1_;
        this.worldTime = p_i45230_3_;

        if (!p_i45230_5_)
        {
            this.worldTime = -this.worldTime;

            if (this.worldTime == 0L)
            {
                this.worldTime = -1L;
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer data) throws IOException
    {
        this.totalWorldTime = data.readLong();
        this.worldTime = data.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer data) throws IOException
    {
        data.writeLong(this.totalWorldTime);
        data.writeLong(this.worldTime);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleTimeUpdate(this);
    }

    public long getTotalWorldTime()
    {
        return this.totalWorldTime;
    }

    public long getWorldTime()
    {
        return this.worldTime;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient) handler);
    }
}
