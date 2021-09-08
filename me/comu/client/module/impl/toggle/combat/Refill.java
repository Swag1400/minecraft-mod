package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.InventoryUtils;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public final class Refill extends ToggleableModule
{
    private final NumberProperty<Long> delay = new NumberProperty<>(110L, 0L, 1000L, 50L, "Delay", "d");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.POTION, "Mode", "m");
    private final Stopwatch stopwatch = new Stopwatch();

    public Refill() {
        super("Refill", new String[]{"refill", "rfill", "rf", "fill"},  0xc6d43c, ModuleType.COMBAT);
        this.offerProperties(mode, delay);
        this.listeners.add(new Listener<MotionUpdateEvent>("refill_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                setDrawn(true);
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
                if (sf.getValue()) {
                    setTag(String.format("Refill " + mode.getFixedValue() +  " \2477" +  countItems()));
                } else if (!sf.getValue()) {
                    setTag(String.format("Refill " +  " \2477" +  countItems()));
                }
                if (event.getTime() == Time.BEFORE) {
                    for (int i = 0; i < 9; ++i) {
                        if (mode.getValue() == Mode.POTION && !InventoryUtils.hotbarIsFull() && InventoryUtils.inventoryHasPotion(Potion.heal, true)) {
                            if (stopwatch.hasCompleted(delay.getValue())) {
                                InventoryUtils.shiftClickPotion(Potion.heal, true);
                                stopwatch.reset();
                            }
                        } else if (mode.getValue() == Mode.SOUP && !InventoryUtils.hotbarIsFull() && InventoryUtils.inventoryHasSoup()) {
                            if (stopwatch.hasCompleted(delay.getValue())) {
                                InventoryUtils.shiftClickSoup();
                                stopwatch.reset();
                            }

                        }

                    }
                }
            }
            private int countItems()
            {
                int items = 0;

                for (int index = 9; index < 45; index++) {
                    ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

                    if (itemStack != null) {
                        switch (mode.getValue()) {
                            case POTION:
                                if (isItemHealthPotion(itemStack)) {
                                    items += itemStack.stackSize;
                                }

                                break;

                            case SOUP:
                                if (itemStack.getItem() instanceof ItemSoup) {
                                    items += itemStack.stackSize;
                                }

                                break;
                        }
                    }
                }

                return items;
            }
            private boolean isItemHealthPotion(ItemStack itemStack)
            {
                if ((itemStack.getItem() instanceof ItemPotion))
                {
                    ItemPotion potion = (ItemPotion) itemStack.getItem();

                    if (potion.hasEffect(itemStack))
                    {
                        for (Object o : potion.getEffects(itemStack))
                        {
                            PotionEffect effect = (PotionEffect) o;

                            if (effect.getEffectName().equals("potion.heal"))
                            {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        });



    }
    private enum Mode {
    	POTION, SOUP
    }
}
