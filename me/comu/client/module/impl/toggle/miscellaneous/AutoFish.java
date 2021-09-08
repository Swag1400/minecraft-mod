package me.comu.client.module.impl.toggle.miscellaneous;


import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.EventTarget;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S29PacketSoundEffect;

/**
 * Created by comu on 5/1/2018
 */

    public final class AutoFish extends ToggleableModule {

    private final Stopwatch stopwatch = new Stopwatch();
    private int timer;

    public AutoFish() {
        super("AutoFish", new String[]{"AutoFish", "afish", "fish", "auto-fish"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_fish_motion_update_listener") {
            @EventTarget
            public void call(MotionUpdateEvent event) {
                int rodInHotbar = -1;
                for (int i = 0; i < 9; ++i) {
                    final ItemStack stack = minecraft.thePlayer.inventory.getStackInSlot(i);
                    if (stack == null && stack.getItem() instanceof ItemFishingRod) {
                        rodInHotbar = i;
                        break;
                    }
                }
                if (rodInHotbar != -1) {
                    if (minecraft.thePlayer.inventory.currentItem != rodInHotbar) {
                        minecraft.thePlayer.inventory.currentItem = rodInHotbar;
                        return;
                    }
                    if (timer > 0) {
                        --timer;
                        return;
                    }
                    if (minecraft.thePlayer.fishEntity != null) {
                        return;
                    }
                    rightClick();
                }
                else {
                    int rodInInventory = -1;
                    for (int j = 9; j < 36; ++j) {
                        final ItemStack stack2 = minecraft.thePlayer.inventory.getStackInSlot(j);
                        if (!(stack2 == null) && stack2.getItem() instanceof ItemFishingRod) {
                            rodInInventory = j;
                            break;
                        }
                    }
                    if (rodInInventory == -1) {
                        Logger.getLogger().printToChat("Out of Fishing Rods.");
                        setRunning(false);
                        return;
                    }
                    int hotbarSlot = -1;
                    for (int k = 0; k < 9; ++k) {
//                        if (InventoryUtils.isSlotEmpty(k)) {
//                            hotbarSlot = k;
//                            break;
//                        }
                    }
                    boolean swap = false;
                    if (hotbarSlot == -1) {
                        hotbarSlot = minecraft.thePlayer.inventory.currentItem;
                        swap = true;
                    }
                    Minecraft.getMinecraft().playerController.windowClick(rodInInventory, 0 ,0 , 0 ,minecraft.thePlayer);
                    minecraft.playerController.windowClick(0 , 36  + hotbarSlot, 0 , 0, minecraft.thePlayer);
                    if (swap) {
                        minecraft.playerController.windowClick(0, rodInInventory, 0 ,0 ,minecraft.thePlayer);
                    }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("auto_fish_motion_update_listener") {
            @EventTarget
            public void call(PacketEvent event) {
                if (!(event.getPacket() instanceof S29PacketSoundEffect)) {
                    return;
                }
              //  if (!SoundEvents.isBobberSplash((S29PacketSoundEffect)event.getPacket()))
              //      return;


                rightClick();
            }
            });
        }
    @Override
    public void onDisable() {
        timer = 0;
    }
    private void rightClick() {
        final ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
        if (stack == null || !(stack.getItem() instanceof ItemFishingRod)) {
            return;
        }
    }
}