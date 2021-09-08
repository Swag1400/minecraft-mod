package me.comu.client.command.impl.server;

import me.comu.client.command.Command;

public final class Disconnect extends Command
{
    public Disconnect()
    {
        super(new String[] {"disconnect", "dc"});
    }
    @Override
    public String dispatch()
    {
        minecraft.theWorld.sendQuittingDisconnectingPacket();
        return null;
    }
}