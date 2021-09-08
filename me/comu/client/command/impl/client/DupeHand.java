package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import java.util.Random;
/**
 * Created by comu on 9/29/2018
 */

public final class DupeHand extends Command
{
    public DupeHand()
    {
        super(new String[] {"dupehand"}, new Argument("amount"));
    }

    @Override
    public String dispatch() {
        int argument2 = Integer.parseInt(getArgument("Amount").getValue());
        ItemStack item = new ItemStack(minecraft.thePlayer.getHeldItem().getItem());
        if (minecraft.thePlayer.getHeldItem().isItemEnchanted() || minecraft.thePlayer.getHeldItem().hasEffect()) {
        item.addEnchantment(Enchantment.SHARPNESS, 2);
        }
        minecraft.thePlayer.dropItem(item.getItem(), argument2);
        Random randy = new Random();
        return "Duping C0IPacketHandshake Buffer @" + randy.nextInt(500);


    }
}
