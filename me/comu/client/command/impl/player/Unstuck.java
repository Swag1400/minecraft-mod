package me.comu.client.command.impl.player;

import me.comu.client.command.Command;
import me.comu.client.utils.ClientUtils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Created by comu on 10/28/2018
 *  ClientUtils.packet(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
 */
public class Unstuck extends Command {

    public Unstuck()
    {
        super(new String[] {"unstuck", "stuck"});
    }

    @Override
    public String dispatch()
    {
        for (int i = 0; i < 10; i++)
        ClientUtils.packet(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        return "Attempted to unstuck you";
    }



}
