package me.comu.client.command.impl.player;

import me.comu.client.command.Command;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class Suicide extends Command
{
    public Suicide()
    {
        super(new String[] {"suicide", "suicide", "kill"});
        
    }

	@Override
	public String dispatch() {
		for (int i = 0; i < 80.0 + 40.0 * (10 - 0.5); ++i) {
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.06, minecraft.thePlayer.posZ, false));
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
        }
		if (2 + 3 == 5) {
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.02, minecraft.thePlayer.posZ, false));
		} 
        return "Bye Bitch.";	
	}
	
}