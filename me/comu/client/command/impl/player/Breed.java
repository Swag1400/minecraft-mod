package me.comu.client.command.impl.player;

import me.comu.client.command.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;


public final class Breed extends Command
{
    public Breed()
    {
        super(new String[] {"breed"});
    }

    @Override
    public String dispatch()
    {
        int counter = 0;

        for (Object object : minecraft.theWorld.getLoadedEntityList())
        {
            Entity entity = (Entity) object;

            if (entity instanceof EntityAnimal)
            {
                EntityAnimal entityAnimal = (EntityAnimal) entity;

                if (isTargetValid(entityAnimal))
                {
                    for (int index = 36; index < 45; index++)
                    {
                        ItemStack stack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

                        if (stack != null && entityAnimal.isBreedingItem(stack))
                        {
                            minecraft.func_175102_a().addToSendQueue(new C09PacketHeldItemChange(index - 36));
                            minecraft.func_175102_a().addToSendQueue(new C02PacketUseEntity(entityAnimal, C02PacketUseEntity.Action.INTERACT));
                            counter++;

                            if (minecraft.thePlayer.capabilities.isCreativeMode)
                            {
                                break;
                            }

                            if (--stack.stackSize <= 0)
                            {
                                minecraft.thePlayer.inventory.setInventorySlotContents(index, null);
                                break;
                            }

                            break;
                        }
                    }
                }
            }
        }

        this.minecraft.func_175102_a().addToSendQueue(new C09PacketHeldItemChange(this.minecraft.thePlayer.inventory.currentItem));
        return String.format("Bred %s animal%s.", counter, counter == 1 ? "" : "s");
    }

    private boolean isTargetValid(EntityAnimal animal)
    {
        return !animal.isChild() && !animal.isInLove() && animal.getGrowingAge() == 0 && this.minecraft.thePlayer.getDistanceToEntity(animal) < (this.minecraft.thePlayer.canEntityBeSeen(animal) ? 6 : 3);
    }
}
