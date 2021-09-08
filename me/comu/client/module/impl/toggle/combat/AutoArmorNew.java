package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public final class AutoArmorNew extends ToggleableModule
{
    // private final Property<Boolean> autoSwitch = new Property<>(true, "AutoSwitch", "as","switch","best");
    private final NumberProperty<Long> delay = new NumberProperty<>(500L, "Delay", "D");

    private final Stopwatch stopwatch = new Stopwatch();

    public AutoArmorNew()
    {
        super("AutoArmor", new String[] {"autoarmor", "aa", "armor"}, 0xFFADD490, ModuleType.COMBAT);
        this.offerProperties(delay);
        this.listeners.add(new Listener<TickEvent>("auto_armor_tick_listener")
        {
            @Override
            public void call(TickEvent event)
            {
                if (!stopwatch.hasCompleted(delay.getValue()) || minecraft.thePlayer != null && (minecraft.getMinecraft().currentScreen == null || minecraft.getMinecraft().currentScreen instanceof GuiInventory || !minecraft.getMinecraft().currentScreen.getClass().getName().contains("inventory")))
                {
                    int slotID = -1;
                    double maxProt = -1.0;

                    for (int i = 9; i < 45; ++i)
                    {
                        final ItemStack stack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();

                        if (stack != null && canEquip(stack))
                        {
                            final double protValue = getProtectionValue(stack);

                            if (protValue >= maxProt)
                            {
                                slotID = i;
                                maxProt = protValue;
                            }
                        }
                    }

                    if (slotID != -1)
                    {
                        minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, slotID, 0, 1, minecraft.thePlayer);
                    }
                }
            }
        });
    }

    private boolean canEquip(final ItemStack stack)
    {
        return (minecraft.thePlayer.getEquipmentInSlot(1) == null && stack.getUnlocalizedName().contains("boots")) || (minecraft.thePlayer.getEquipmentInSlot(2) == null && stack.getUnlocalizedName().contains("leggings")) || (minecraft.thePlayer.getEquipmentInSlot(3) == null && stack.getUnlocalizedName().contains("chestplate")) || (minecraft.thePlayer.getEquipmentInSlot(4) == null && stack.getUnlocalizedName().contains("helmet"));
    }

    private double getProtectionValue(final ItemStack stack)
    {
        return ((ItemArmor)stack.getItem()).damageReduceAmount + (100 - ((ItemArmor)stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.PROTECTION.effectId, stack) * 4 * 0.0075;
    }
}
