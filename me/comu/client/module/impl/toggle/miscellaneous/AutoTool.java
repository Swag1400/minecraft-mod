package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.BlockClickedEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;


public final class AutoTool extends ToggleableModule
{
	private final Property<Boolean> weapons = new Property<>(true, "weapons", "weapon");
	private final Property<Boolean> tools = new Property<>(true, "tool", "tools");
	
    public AutoTool()
    {
        super("AutoTool", new String[] {"autotool", "at","auto-tool","tool"}, 0xFF4BCFE3, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<BlockClickedEvent>("auto_tool_?_event_listener") {

			@Override
			public void call(BlockClickedEvent event) {
				 minecraft.thePlayer.inventory.currentItem = PlayerHelper.getBestToolForBlock(event.getBlockPos());
				
			}
        });

    }
}


/*


import info.sigmaclient.Client;
import info.sigmaclient.event.Event;
import info.sigmaclient.event.RegisterEvent;
import info.sigmaclient.event.impl.EventUpdate;
import info.sigmaclient.module.Module;
import info.sigmaclient.module.data.ModuleData;
import info.sigmaclient.module.impl.player.InventoryCleaner;
import info.sigmaclient.util.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoSword extends Module {

    private Timer timer = new Timer();

    public AutoSword(ModuleData data) {
        super(data);
    }

    @RegisterEvent(events = {EventUpdate.class})
    public void onEvent(Event event) {
        EventUpdate em = (EventUpdate) event;
        if(Client.getModuleManager().get(InventoryCleaner.class).isEnabled() && InventoryCleaner.isCleaning())
            return;
        if (em.isPre() && (mc.currentScreen instanceof GuiInventory || mc.currentScreen == null)) {
            if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword && timer.delay(100)) {
                for (int i = 0; i < 45; i++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                        if (item instanceof ItemSword) {
                            float itemDamage = getAttackDamage(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
                            float currentDamage = getAttackDamage(mc.thePlayer.getCurrentEquippedItem());
                            if (itemDamage > currentDamage) {
                                swap(i, mc.thePlayer.inventory.currentItem);
                                timer.reset();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    protected void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }

    private float getAttackDamage(ItemStack stack) {
        if(!(stack.getItem() instanceof ItemSword)) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + ((ItemSword)stack.getItem()).getAttackDamage()
                + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
    }

}

 */