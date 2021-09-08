package me.comu.client.command.impl.server;

import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.command.Command;
import me.comu.client.utils.Helper;
import net.minecraft.network.play.client.C14PacketTabComplete;

public final class Plugins extends Command {

    private final Stopwatch stopwatch = new Stopwatch();

    public Plugins() {
        super(new String[]{"plugins", "pl"});
    }


    @Override
    public String dispatch() {
        Helper.sendPacket(new C14PacketTabComplete("/"));
        return "Listening for a S3APacketTabComplete for 20s.";
    }

}
