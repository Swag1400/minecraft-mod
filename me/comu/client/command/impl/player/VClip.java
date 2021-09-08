package me.comu.client.command.impl.player;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;


public final class VClip extends Command
{
    public VClip()
    {
        super(new String[] {"vclip", "vc", "v"}, new Argument("blocks"));
    }

    @Override
    public String dispatch()
    {
        double blocks = Double.parseDouble(getArgument("blocks").getValue());
        minecraft.thePlayer.setBoundingBox(minecraft.thePlayer.getEntityBoundingBox().offset(0, blocks, 0));
        return String.format("Teleported %s &e%s&7 block(s).", blocks < 0 ? "down" : "up", blocks);
    }

}
