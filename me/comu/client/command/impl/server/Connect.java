package me.comu.client.command.impl.server;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

public final class Connect extends Command {
    public Connect() {
        super(new String[]{"connect", "c"}, new Argument("ip"));
    }

    @Override
    public String dispatch() {
        try {
            ServerData serverData = new ServerData("Minecraft Server", getArgument("ip").getValue());
            minecraft.theWorld.sendQuittingDisconnectingPacket();
            minecraft.loadWorld(null);
            minecraft.displayGuiScreen(new GuiConnecting(null, minecraft, serverData));
            return "Connecting...";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Something went wrong";
    }
}