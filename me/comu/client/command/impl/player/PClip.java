package me.comu.client.command.impl.player;

import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.utils.Helper;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;


public final class PClip extends Command
{
	Stopwatch sp = new Stopwatch();
	
    public PClip()
    {
        super(new String[] {"pclip", "pc", "p"}, new Argument("blocks"));
    }

    @Override
    public String dispatch()
    {
    	 final float blocks = Float.parseFloat(getArgument("blocks").getValue());
         minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + blocks, minecraft.thePlayer.posZ, false));
         minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + blocks, minecraft.thePlayer.posZ);
         if(sp.hasCompleted(3l)) {
        	if(minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemEnderPearl);
        	Helper.sendPacket(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getCurrentEquippedItem()));
         }
         if (!(minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemEnderPearl))
        	 return "put a pearl in your hand bruh";
        return String.format("Teleported %s &e%s&7 block(s).", blocks < 0 ? "down" : "up", blocks);
    }
    
}
