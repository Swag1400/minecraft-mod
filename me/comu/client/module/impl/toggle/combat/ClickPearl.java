package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.Inventory;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public final class ClickPearl extends ToggleableModule
{
    private int oldSlot;
    public ClickPearl()
    {
        super("ClickPearl", new String[] {"ClickPearl", "cp", "cpearl","pearl","click-pearl"}, 0xFF96D490, ModuleType.COMBAT);

        this.listeners.add(new Listener<MotionUpdateEvent>("click_pearl_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (event.getTime() == Time.BEFORE) {
                    if ( ClickPearl.minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword && ClickPearl.minecraft.gameSettings.keyBindUseItem.pressed && Inventory.findHotbarItem(368, 1) != -1 && ClickPearl.minecraft.theWorld.getBlockState(ClickPearl.minecraft.objectMouseOver.getPos()).getBlock() instanceof BlockAir) {
                        ClickPearl.minecraft.gameSettings.keyBindUseItem.pressed = false;
                        oldSlot = ClickPearl.minecraft.thePlayer.inventory.currentItem;
                        ClickPearl.minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(Inventory.findHotbarItem(368, 1)));
                        ClickPearl.minecraft.playerController.sendUseItem(ClickPearl.minecraft.thePlayer, ClickPearl.minecraft.theWorld, ClickPearl.minecraft.thePlayer.inventory.getCurrentItem());
                        ClickPearl.minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));

                    }
                }
            }

        
    
        });

        
}
}
   // works but messes up speed 4 sum reason :thinking: 3/28/18 - randomly thought of adding this lol // messes up packets 08/05/19