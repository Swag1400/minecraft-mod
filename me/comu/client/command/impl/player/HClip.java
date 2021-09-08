package me.comu.client.command.impl.player;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;


public final class HClip extends Command
{
    public HClip()
    {
        super(new String[] {"hclip", "hc", "h"}, new Argument("blocks"));
    }

    @Override
    public String dispatch()
    {
        double blocks = Double.parseDouble(getArgument("blocks").getValue());
        double x = Math.cos(Math.toRadians(minecraft.thePlayer.rotationYaw + 90F));
        double z = Math.sin(Math.toRadians(minecraft.thePlayer.rotationYaw + 90F));
        minecraft.thePlayer.setPosition(minecraft.thePlayer.posX + (1F * blocks * x + 0F * blocks * z), minecraft.thePlayer.posY, minecraft.thePlayer.posZ + (1F * blocks * z - 0F * blocks * x));
        return String.format("Teleported %s &e%s&7 block(s).", blocks < 0 ? "back" : "forward", blocks);
    }
}
