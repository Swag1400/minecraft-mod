package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;



public final class AutoArmor extends ToggleableModule {
	private final Property<Boolean> autoSwitch = new Property<>(true, "AutoSwitch", "as","switch","best");
    private final NumberProperty<Long> delay = new NumberProperty<Long>(0L, 0L, 1000L,  50L, "Delay", "D");

    private final Stopwatch stopwatch = new Stopwatch();

    public AutoArmor() {
        super("AutoArmor", new String[]{"autoarmor", "aa", "armor"}, 0xFFADD490, ModuleType.COMBAT);
        this.offerProperties(delay, autoSwitch);
        this.listeners.add(new Listener<TickEvent>("auto_armor_tick_listener") {

            @Override
            public void call(TickEvent event) {
                if (!stopwatch.hasCompleted(delay.getValue()) || minecraft.thePlayer.capabilities.isCreativeMode || !(minecraft.currentScreen == null || minecraft.currentScreen instanceof GuiChat)) {
                    return;
                }

                for (byte b = 5; b <= 8; b++) {
                    if (equipArmor(b)) {
                        stopwatch.reset();
                        break;
                    }
                }
            }
        });
    }

    private boolean equipArmor(byte b) {
        int currentProtection = -1;
        byte slot = -1;

        ItemArmor current = null;
        if (minecraft.thePlayer.inventoryContainer.getSlot(b).getStack() != null && minecraft.thePlayer.inventoryContainer.getSlot(b).getStack().getItem() instanceof ItemArmor) {
            current = (ItemArmor) minecraft.thePlayer.inventoryContainer.getSlot(b).getStack().getItem();
            currentProtection = current.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.PROTECTION.effectId, minecraft.thePlayer.inventoryContainer.getSlot(b).getStack());
        }
        for (byte i = 9; i <= 44; i++) {
            ItemStack stack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    int armorProtection = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.PROTECTION.effectId, stack);
                    if (!autoSwitch.getValue())
                        armorProtection = -1;
                    if (checkArmor(armor, b) && (current == null || currentProtection < armorProtection)) {
                        current = armor;
                        slot = i;
                    }
                }
            }
        }

        if (slot != -1) {
            boolean isNull = minecraft.thePlayer.inventoryContainer.getSlot(b).getStack() == null;
            if (!isNull) {
                clickSlot(b, 0, false);
            }
            clickSlot(slot, 0, true);
            if (!isNull) {
                clickSlot(slot, 0, false);
            }
            return true;
        }

        return false;
    }

    private boolean checkArmor(ItemArmor item, byte b) {
        return b == 5 && item.getUnlocalizedName().startsWith("item.helmet") || b == 6 && item.getUnlocalizedName().startsWith("item.chestplate") || b == 7 && item.getUnlocalizedName().startsWith("item.leggings") || b == 8 && item.getUnlocalizedName().startsWith("item.boots");
    }

    private void clickSlot(int slot, int mouseButton, boolean shiftClick) {
        minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, minecraft.thePlayer);
    }
}

