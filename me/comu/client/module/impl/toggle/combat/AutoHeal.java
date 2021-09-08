package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class AutoHeal extends ToggleableModule {
    private final NumberProperty<Float> health = new NumberProperty<>(9F, 1F, 20F, 1F, "Health", "h", "<3");
    private final NumberProperty<Long> delay = new NumberProperty<>(250L, 0L, 1000L, 50L,"Delay", "d");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.POTION, "Mode", "m");
    private final Property<Boolean> jump = new Property<>(false, "Jump", "j");
    private final Property<Boolean> oof = new Property<>(true, "oof", "o");

    private boolean potting = false, souping = false;

    private final Stopwatch stopwatch = new Stopwatch();
    private int lockedTicks = -1;
    private double x, y, z;

    public AutoHeal() {
        super("AutoHeal", new String[]{"autoheal", "autosoup", "autopot", "ah", "autopotion", "as", "ap", "heal", "potion", "pot", "soup"}, 0xFF1BCC8B, ModuleType.COMBAT);
        this.offerProperties(health, delay, mode, jump);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_heal_motion_update_listener")
                                   ///    		this.offsetPresets()

                           {
                               @Override
                               public void call(MotionUpdateEvent event) {
                                   setTag(String.format("Auto%s \2477%s", mode.getFixedValue(), countItems()));

                                   switch (event.getTime()) {
                                       case BEFORE:
                                           if (minecraft.thePlayer.getHealth() <= health.getValue()) {
                                               int currentItem = minecraft.thePlayer.inventory.currentItem;

                                               switch (mode.getValue()) {
                                                   case POTION:
                                                       if (hotbarHasItems() && !PlayerHelper.isInLiquid() && !PlayerHelper.isOnLiquid() && stopwatch.hasCompleted(delay.getValue())) {
                                                           potting = true;

                                                           if (jump.getValue() && minecraft.thePlayer.isCollidedVertically) {
                                                               event.setRotationPitch(-90F);
                                                           } else {
                                                               event.setRotationPitch(110F);
                                                           }

                                                           if (jump.getValue() && minecraft.thePlayer.isCollidedVertically) {
                                                               useItem();
                                                               jump();
                                                               x = minecraft.thePlayer.posX;
                                                               y = minecraft.thePlayer.posY + 1.24D;
                                                               z = minecraft.thePlayer.posZ;
                                                               System.out.println(lockedTicks + "before");
                                                               lockedTicks = 5;
                                                               potting = false;
                                                           }
                                                       } else if (!hotbarHasItems()) {
                                                           getItemsFromInventory();
                                                       }

                                                       if (lockedTicks >= 0 && jump.getValue()) {
                                                           event.setCanceled(true);
                                                       }

                                                       if (lockedTicks == 0 && jump.getValue()) {
                                                           minecraft.thePlayer.motionX = 0;
                                                           minecraft.thePlayer.motionZ = 0;
                                                           minecraft.thePlayer.setPositionAndUpdate(x, y, z);
                                                           minecraft.thePlayer.motionY = -0.08;
                                                           potting = false;
                                                           System.out.println(lockedTicks + "during");
                                                       }

                                                       lockedTicks -= 1;
                                                       System.out.println(lockedTicks + "after");
                                                       break;

                                                   case SOUP:
                                                       if (hotbarHasItems()) {
                                                           souping = true;
                                                           useItem();
                                                           souping = false;
                                                       } else {
                                                           getItemsFromInventory();
                                                           souping = false;
                                                       }

                                                       break;
                                               }

                                               minecraft.thePlayer.inventory.currentItem = currentItem;
                                           }

                                           souping = false;
                                           //      if (event.getTime().AFTER == MotionUpdateEvent.Time.AFTER && oof.getValue() && minecraft.thePlayer.getHealth() < 0.5) {
                                           // 	   stopwatch.hasCompleted(5000);
                                           //	   minecraft.thePlayer.sendChatMessage("oof");
                                           // }
                                           break;

                                       case AFTER:
                                           if (minecraft.thePlayer.getHealth() <= health.getValue()) {
                                               switch (mode.getValue()) {
                                                   case POTION:
                                                       if (hotbarHasItems() && potting && !PlayerHelper.isInLiquid() && !PlayerHelper.isOnLiquid() && !jump.getValue()) {
                                                           useItem();
                                                           potting = false;
                                                           break;
                                                       }

                                                       potting = false;
                                                       souping = false;
                                               }

                                               potting = false;
                                               souping = false;
                                           }
                                   }
                               }
                           }
        );
    }

    private int countItems() {
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

    @Override
    protected void onEnable() {
        super.onEnable();
        lockedTicks = -1;
        potting = false;
    }

    // For non mc.thePlayer.jump pot, good cause it's instant..
    private void jump() {
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.42D, minecraft.thePlayer.posZ, true));
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.75D, minecraft.thePlayer.posZ, true));
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0D, minecraft.thePlayer.posZ, true));
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.16D, minecraft.thePlayer.posZ, true));
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.24D, minecraft.thePlayer.posZ, true));
    }

    private void getItemsFromInventory() {
        int item = -1;
        boolean found = false;
        boolean splash = false;

        for (int index = 36; index >= 9; index--) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

            if (itemStack != null) {
                switch (mode.getValue()) {
                    case POTION:
                        if (isItemHealthPotion(itemStack)) {
                            //if (!hotbarHasItems() && !hotbarHasSlotAvaliable())
                            //  swap(index,6); // TODO:
                            item = index;
                            found = true;
                            splash = ItemPotion.isSplash(itemStack.getItemDamage());
                            break;
                        }

                        break;

                    case SOUP:
                        if (itemStack.getItem() instanceof ItemSoup) {
                            item = index;
                            found = true;
                            break;
                        }

                        break;
                }
            }
        }

        if (found) {
            if (!splash) {
                for (int index = 0; index < 45; index++) {
                    ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

                    if (itemStack != null) {
                        switch (mode.getValue()) {
                            case POTION:
                                if ((itemStack.getItem() == Items.glass_bottle) && (index >= 36) && (index <= 44)) {
                                    minecraft.playerController.windowClick(0, index, 0, 0, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                                }

                                break;

                            case SOUP:
                                if ((itemStack.getItem() == Items.bowl) && (index >= 36) && (index <= 44)) {
                                    minecraft.playerController.windowClick(0, index, 0, 0, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                                }

                                break;
                        }
                    }
                }
            }

            minecraft.playerController.windowClick(0, item, 0, 1, minecraft.thePlayer);
        }
    }

    private boolean hotbarHasItems() {
        boolean found = false;

        for (int index = 36; index < 45; index++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

            if (itemStack != null) {
                switch (mode.getValue()) {
                    case POTION:
                        if (isItemHealthPotion(itemStack)) {
                            found = true;
                        }

                        break;

                    case SOUP:
                        if (itemStack.getItem() instanceof ItemSoup) {
                            found = true;
                        }

                        break;
                }
            }
        }

        return found;
    }

    private boolean hotbarHasSlotAvaliable() {
        boolean found = false;

        for (int index = 0; index < 9; index++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null) {
                found = false;
            } else if (itemStack != null) {
                found = true;
            }


        }

        return found;
    }

    private void useItem() {
        int item = -1;
        boolean found = false;
        boolean splash = false;

        for (int index = 36; index < 45; index++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

            if (itemStack != null) {
                switch (mode.getValue()) {
                    case POTION:
                        if (isItemHealthPotion(itemStack)) {
                            item = index;
                            found = true;
                            splash = ItemPotion.isSplash(itemStack.getItemDamage());
                            break;
                        }

                        break;

                    case SOUP:
                        if (itemStack.getItem() instanceof ItemSoup) {
                            item = index;
                            found = true;
                            break;
                        }

                        break;
                }
            }
        }

        if (found) {
            switch (mode.getValue()) {
                case POTION:
                    if (splash) {
                        if (jump.getValue()) {
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(minecraft.thePlayer.rotationYaw, minecraft.thePlayer.moveForward == 0 ? minecraft.thePlayer.onGround ? -90 : -90 : -90, minecraft.thePlayer.onGround));
                        } else {
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(minecraft.thePlayer.rotationYaw, minecraft.thePlayer.moveForward == 0 ? minecraft.thePlayer.onGround ? 110 : 95 : 90, minecraft.thePlayer.onGround));
                        }

                        int lastSlot = minecraft.thePlayer.inventory.currentItem;
                        minecraft.func_175102_a().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        minecraft.func_175102_a().addToSendQueue(new C09PacketHeldItemChange(item - 36));
                        minecraft.playerController.updateController();
                        minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch, minecraft.thePlayer.onGround));
                        minecraft.func_175102_a().addToSendQueue(new C09PacketHeldItemChange(lastSlot));
                        stopwatch.reset();
                        break;
                    } else if (minecraft.thePlayer.onGround) {
                        for (int index = 0; index < 45; index++) {
                            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

                            if (itemStack != null) {
                                if ((itemStack.getItem() == Items.glass_bottle) && (index >= 36) && (index <= 44)) {
                                    minecraft.playerController.windowClick(0, index, 0, 0, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                                }
                            }
                        }

                        minecraft.thePlayer.inventory.currentItem = (item - 36);
                        minecraft.playerController.updateController();
                        minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                        minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));

                        for (int index = 0; index < 32; index++) {
                            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(minecraft.thePlayer.onGround));
                        }

                        minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        minecraft.thePlayer.stopUsingItem();
                    }

                    if (PlayerHelper.isInLiquid()) {
                        potting = true;
                    }

                    break;

                case SOUP:
                    for (int index = 0; index < 45; index++) {
                        ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

                        if (itemStack != null) {
                            if ((itemStack.getItem() == Items.bowl) && (index >= 36) && (index <= 44)) {
                                minecraft.playerController.windowClick(0, index, 0, 0, minecraft.thePlayer);
                                minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                            }
                        }
                    }
                    minecraft.thePlayer.inventory.currentItem = (item - 36);
                    minecraft.playerController.updateController();
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    minecraft.thePlayer.stopUsingItem();
                    break;
            }
        }
    }

    private boolean isItemHealthPotion(ItemStack itemStack) {
        if ((itemStack.getItem() instanceof ItemPotion)) {
            ItemPotion potion = (ItemPotion) itemStack.getItem();

            if (potion.hasEffect(itemStack)) {
                for (Object o : potion.getEffects(itemStack)) {
                    PotionEffect effect = (PotionEffect) o;

                    if (effect.getEffectName().equals("potion.heal")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void swap(final int slot, final int hotbarSlot) {
        minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, slot, hotbarSlot, 2, minecraft.thePlayer);
    }

    public boolean isPotting() {
        return potting;
    }

    boolean isSouping() {
        return souping;
    }

    public enum Mode {
        POTION, SOUP
    }
}
