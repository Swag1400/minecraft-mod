package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.notification.Notification;
import me.comu.client.notification.NotificationManager;
import me.comu.client.notification.NotificationType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Created by august on 11/18/2018
 */

public final class AutoHeal2 extends ToggleableModule {
    private final NumberProperty<Float> health = new NumberProperty<>(8F, 1F, 20F,1F, "Health", "h", "<3");
    private final NumberProperty<Long> delay = new NumberProperty<>(400L, 0L, 1000L,50L, "Delay", "d");
    private final Property<Boolean> predict = new Property<Boolean>(true, "Predict","P","pred","motion");
    private final Property<Boolean> eatCheck = new Property<Boolean>(true, "Eat-Check","eatcheck","checkeat","eat");
    private final Property<Boolean> ladderCheck = new Property<Boolean>(true, "Ladder-Check","laddercheck","ladder","ladders","ladderscheck");
    public boolean isPotting;
    public boolean doPot;
    private Stopwatch stopwatch = new Stopwatch();


    public AutoHeal2() {
        super("AutoPot2", new String[]{"autoheal2", "autopot2","ap2"}, 0xFFB990D4, ModuleType.COMBAT);
        this.offerProperties(health, ladderCheck, delay, predict, eatCheck);
        doPot = false;
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_clicker_tick_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                    setTag("AutoPotion \2477" + getCount());
                        final ItemThing potSlot = getHealingItemFromInventory();
                        if (stopwatch.hasCompleted(delay.getValue()) && minecraft.thePlayer.getHealth() <= health.getValue() && potSlot.getSlot() != -1) {
                           // event.setCanceled(true);
                            event.setRotationPitch(90.0f);
                            if (predict.getValue()) {
                                final double movedPosX = ClientUtils.mc().thePlayer.posX + ClientUtils.mc().thePlayer.motionX * 16.0;
                                final double movedPosY = ClientUtils.mc().thePlayer.boundingBox.minY - 3.6;
                                final double movedPosZ = ClientUtils.mc().thePlayer.posZ + ClientUtils.mc().thePlayer.motionZ * 16.0;
                                final float yaw = ClientUtils.getRotationFromPosition(movedPosX, movedPosZ, movedPosY)[0];
                                final float pitch = ClientUtils.getRotationFromPosition(movedPosX, movedPosZ, movedPosY)[1];
                                event.setRotationYaw(yaw);
                                event.setRotationPitch(pitch);
                            }
                            if (eatCheck.getValue()) {
                                if (!minecraft.thePlayer.isEating()) {
                                    isPotting = true;
                                    minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    Logger.getLogger().printToChat("ayoooo");
                                    NotificationManager.notify(new Notification(NotificationType.INFO, "\247n" + getLabel(), "ayoooo", 1));
                                    doPot = true;
                                } else if (ladderCheck.getValue())
                                {
                                    if (minecraft.thePlayer.isOnLadder())
                                    {
                                        if (!minecraft.thePlayer.isOnLadder())
                                        {
                                            isPotting = true;
                                            minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                            Logger.getLogger().printToChat("ayoooo");
                                            NotificationManager.notify(new Notification(NotificationType.INFO, "\247n" + getLabel(), "ayoooo", 1));
                                            doPot = true;
                                        }
                                    }
                                }
                            } else {
                                isPotting = true;
                                minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                Logger.getLogger().printToChat("ayoooo");
                                NotificationManager.notify(new Notification(NotificationType.INFO, "\247n" + getLabel(), "ayoooo", 1));
                                doPot = true;
                            }
                        }
                } else if (doPot) {
                    final ItemThing potSlot = getHealingItemFromInventory();
                    if (potSlot.getSlot() == -1) {
                        return;
                    }
                    if ((potSlot.getSlot() < 0 || potSlot.getSlot() > 9 || minecraft.thePlayer.inventory.currentItem < 0 || minecraft.thePlayer.inventory.currentItem > 9) && doPot) {
                        if (potSlot.getSlot() < 9) {
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot.getSlot()));
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
                        }
                        else {
                            swap(potSlot.getSlot(), 5);
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(5));
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                            if (potSlot.isSoup()) {
                                minecraft.func_175102_a().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            }
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
                        }
                        stopwatch.reset();
                       // event.setCanceled(true);
                        doPot = false;
                        isPotting = false;
                    }
                }
            }
        });
    }
   public int getCount() {
        final int pot = -1;
        int counter = 0;
        for (int i = 9; i < 45; ++i) {
            if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    final ItemPotion potion = (ItemPotion)item;
                    if (potion.getEffects(is) != null) {
                        for (final Object o : potion.getEffects(is)) {
                            final PotionEffect effect = (PotionEffect)o;
                            if (effect.getPotionID() == Potion.heal.id && ItemPotion.isSplash(is.getItemDamage())) {
                                ++counter;
                            }
                        }
                    }
                }
                if (item instanceof ItemSoup) {
                    ++counter;
                }
            }
        }
        return counter;
    }


    private ItemThing getHealingItemFromInventory() {
        int itemSlot = -1;
        int counter = 0;
        boolean soup = false;
        for (int i = 9; i < 45; ++i) {
            if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    final ItemPotion potion = (ItemPotion) item;
                    if (potion.getEffects(is) != null) {
                        for (final Object o : potion.getEffects(is)) {
                            final PotionEffect effect = (PotionEffect) o;
                            if (effect.getPotionID() == Potion.heal.id && ItemPotion.isSplash(is.getItemDamage())) {
                                ++counter;
                                itemSlot = i;
                                soup = false;
                            }
                        }
                    }
                }
                if (item instanceof ItemSoup) {
                    ++counter;
                    itemSlot = i;
                    soup = true;
                }
            }
        }
        return new ItemThing(itemSlot, soup);
    }

    private void swap(final int slot, final int hotbarSlot) {
        minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, slot, hotbarSlot, 2, minecraft.thePlayer);
    }
}
 class ItemThing
{
    private int slot;
    private boolean soup;

    public ItemThing(final int slot, final boolean soup) {
        this.slot = slot;
        this.soup = soup;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean isSoup() {
        return this.soup;
    }


}