package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;

public final class InventoryCleaner  extends ToggleableModule
{
    private final NumberProperty<Long> delay = new NumberProperty<>(40L, 0L, 1000L, 50L, "Delay","d");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.DEFAULT, "Mode", "m");
    private final Stopwatch stopwatch = new Stopwatch();

    public InventoryCleaner()
    {
        super("InventoryCleaner", new String[] {"inventorycleaner", "invcleaner","invclean"}, 0xFF696969, ModuleType.MISCELLANEOUS);
        this.offerProperties(delay, mode);
        this.listeners.add(new Listener<MotionUpdateEvent>("inventory_cleaner_input_motion_update_event_listener")
        {
            @Override
            public void call(MotionUpdateEvent event) {
                setDrawn(true);
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
                if (sf.getValue()) {
                    setTag(String.format("InvCleaner \2477" + mode.getFixedValue()));
                }
                if (event.getTime() == MotionUpdateEvent.Time.BEFORE) ;

                {
                switch (mode.getValue()) {
                    case DEFAULT:
                        for (int i = 9; i < 45; i++) {
                            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (itemStack != null) {
                                itemStack.getItem(); // this like makes the items in ur inv stuck so u have to keep pressing e to get it out its gay cause ur gay shitty time

                                if (stopwatch.hasCompleted(delay.getValue())) {
                                    // its suppposed to drop everything out of your inventory it works fine on client
                                    minecraft.playerController.windowClick(0, i, 1, 4, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, 64537, 1, 4, minecraft.thePlayer);
                                    stopwatch.reset();
                                }
                            }
                        }
                        break;
                    case POTION:
                        for (int i = 9; i < 45; i++) {
                            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (itemStack != null && itemStack.getItem() instanceof ItemPotion) {
                                if (stopwatch.hasCompleted(delay.getValue())) {
                                    minecraft.playerController.windowClick(0, i, 1, 4, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, 64537, 1, 4, minecraft.thePlayer);
                                    stopwatch.reset();
                                }

                            }
                        }
                        break;

                    case PURPLE:
                        for (int i = 9; i < 45; i++) {
                            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            ItemStack itemStack2 = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            ItemStack itemStack3 = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (itemStack != null && (itemStack.getItem() instanceof ItemAxe || itemStack2.getItem() instanceof ItemArmor || itemStack3.getItem() instanceof ItemAppleGold)) {
                                boolean canDrop = false;
                                int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SHARPNESS.effectId, itemStack);
                                int thornsLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.THORNS.effectId, itemStack2);
                                if (sharpnessLevel >= 80 || thornsLevel >= 2)
                                    canDrop = true;
                                if (stopwatch.hasCompleted(delay.getValue()) && canDrop) {
                                    minecraft.playerController.windowClick(0, i, 1, 4, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, 64537, 1, 4, minecraft.thePlayer);
                                    stopwatch.reset();
                            }
                            }
                        }
                        break;
                    case COMBAT:
                        for (int i = 9; i < 45; i++) {
                            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (itemStack != null && !(itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemEnderPearl|| itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPotion || itemStack.getItem() instanceof ItemAppleGold || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemSeedFood || itemStack.getItem() instanceof ItemFishFood)) {
                                if (stopwatch.hasCompleted(delay.getValue())) {
                                    minecraft.playerController.windowClick(0, i, 1, 4, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, 64537, 1, 4, minecraft.thePlayer);
                                    stopwatch.reset();
                                }

                            }
                        }
                        break;
                    case MINIGAMES:
                        for (int i = 9; i < 45; i++) {
                            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (itemStack != null && !(itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemEnderPearl|| itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemPotion) || itemStack.getItem() instanceof ItemAppleGold || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemSeedFood) {
                                if (stopwatch.hasCompleted(delay.getValue())) {
                                    minecraft.playerController.windowClick(0, i, 1, 4, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, 64537, 1, 4, minecraft.thePlayer);
                                    stopwatch.reset();
                                }

                            }
                        }
                        break;
                }

                }
            }
        });
    }

    public enum Mode
    {
        DEFAULT, POTION, PURPLE, COMBAT, MINIGAMES
    }
}
