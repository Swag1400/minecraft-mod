package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class Chest extends ToggleableModule
{
    private final NumberProperty<Long> delay = new NumberProperty<>(150L, 10L, 250L, 10L, "Delay", "D");
    private final NumberProperty<Long> auraDelay = new NumberProperty<>(2000L, 100L, 5000L, 100L, "Aura-Delay", "auradelay","adelay","ad","chestauradelay","aurad");
    private final Property<Boolean> close = new Property<>(true, "Auto-Close","done","close","c","finished","finish");
    private final Property<Boolean> aura = new Property<>(true, "Chest-Aura","aura","chestaura","ca");
    private final Property<Boolean> minigames = new Property<>(false, "Minigames","minigame","mini","games","mg","hypixel","skywars","hungergames","mineplex");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.STEAL, "Mode", "m");
    private final Stopwatch stopwatch = new Stopwatch();
    private boolean doing = true;
    public Chest()
    {
        super("Chest", new String[] {"chest", "steal", "cheststealer","cheststeal", "stealer","drop", "store"}, 0xFFFA8D61, ModuleType.WORLD);
        this.offerProperties(delay, mode, close, aura, auraDelay, minigames);
        this.listeners.add(new Listener<MotionUpdateEvent>("chest_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                setTag(mode.getFixedValue());
                if (aura.getValue()) {
                    if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                        for (final Object o : minecraft.theWorld.loadedTileEntityList) {
                            if (o instanceof TileEntityChest) {
                                final TileEntityChest chest = (TileEntityChest) o;
                                final float x = (float) chest.getPos().getX();
                                final float y = (float) chest.getPos().getY();
                                final float z = (float) chest.getPos().getZ();
                                if (minecraft.thePlayer.getDistance(x, y, z) < 4.0 && stopwatch.hasCompleted(auraDelay.getValue()) && minecraft.currentScreen == null) {
                                    minecraft.func_175102_a().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(chest.getPos(), getFacingDirection(chest.getPos()).getIndex(), minecraft.thePlayer.getCurrentEquippedItem(), x, y, z));
                                    stopwatch.reset();
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                }

                if (!(minecraft.currentScreen instanceof GuiChest))
                {
                    return;
                }

                GuiChest chest = (GuiChest) minecraft.currentScreen;
                 if (close.getValue()) {
                    if (isContainerEmpty((chest))) {
                        minecraft.thePlayer.closeScreen();
                    }
                }
                for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); index++)
                {
                    ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);

                    if (stack == null)
                    {
                        continue;
                    }
                    if (minigames.getValue())
                    {
                        if (isBad(stack))
                            continue;

                    }

                    if (stopwatch.hasCompleted(delay.getValue()))
                    {
                        switch (mode.getValue())
                        {

                            case STEAL:
                                minecraft.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, minecraft.thePlayer);
                                break;

                            case DROP:
                                minecraft.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 0, (EntityPlayer)minecraft.thePlayer);
                                minecraft.playerController.windowClick(chest.inventorySlots.windowId, -999, 0, 0, minecraft.thePlayer);
                                stopwatch.reset();
                                break;
                            case STORE: //broken
                                for (int i = 9; i < 45; i++) {
                                    ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                                        itemStack.getItem();
                                    minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, i, 0, 1, minecraft.thePlayer);
                                    minecraft.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, minecraft.thePlayer);

                                }
                        }

                        stopwatch.reset();
                    }
                    if (close.getValue()) {
                    if (isContainerEmpty((chest))) {
                        minecraft.thePlayer.closeScreen();
                    }
                    }
                }
            }
        });
    }

    private boolean isBad(final ItemStack item) {
        ItemStack is = null;
        float lastDamage = -1.0f;
        for (int i = 9; i < 45; ++i) {
            if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is2 = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is2.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword && lastDamage < ClientUtils.getDamage(is2)) {
                    lastDamage = ClientUtils.getDamage(is2);
                    is = is2;
                }
            }
        }
        if (is != null && is.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword) {
            final float currentDamage = ClientUtils.getDamage(is);
            final float itemDamage = ClientUtils.getDamage(item);
            if (itemDamage > currentDamage) {
                return false;
            }
        }
        return item != null && (item.getItem().getUnlocalizedName().contains("tnt") || item.getItem().getUnlocalizedName().contains("stick") || (item.getItem().getUnlocalizedName().contains("egg") && !item.getItem().getUnlocalizedName().contains("leg")) || item.getItem().getUnlocalizedName().contains("string") || item.getItem().getUnlocalizedName().contains("flint") || item.getItem().getUnlocalizedName().contains("compass") || item.getItem().getUnlocalizedName().contains("feather") || item.getItem().getUnlocalizedName().contains("bucket") || item.getItem().getUnlocalizedName().contains("snow") || item.getItem().getUnlocalizedName().contains("fish") || item.getItem().getUnlocalizedName().contains("enchant") || item.getItem().getUnlocalizedName().contains("exp") || item.getItem().getUnlocalizedName().contains("shears") || item.getItem().getUnlocalizedName().contains("anvil") || item.getItem().getUnlocalizedName().contains("torch") || item.getItem().getUnlocalizedName().contains("seeds") || item.getItem().getUnlocalizedName().contains("leather") || item.getItem() instanceof ItemPickaxe || item.getItem() instanceof ItemGlassBottle || item.getItem() instanceof ItemTool || item.getItem().getUnlocalizedName().contains("piston") || (item.getItem().getUnlocalizedName().contains("potion") && this.isBadPotion(item)));
    }

    private boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion)stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect)o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isContainerEmpty(final GuiChest chest) {
        boolean temp = true;
        for (int i = 0, slotAmount = (chest.inventorySlots.inventorySlots.size() == 90) ? 54 : 27; i < slotAmount; ++i) {
            if (chest.inventorySlots.getSlot(i).getHasStack()) {
                temp = false;
            }
        }
        return temp;
    }
    public boolean isContainerEmptyForBadItems(final GuiChest chest) {
        boolean temp = false;
        for (int i = 0, slotAmount = (chest.inventorySlots.inventorySlots.size() == 90) ? 54 : 27; i < slotAmount; ++i) {
            if (isBad(chest.inventorySlots.getSlot(i).getStack())) {
                temp = true;
            }
        }
        return temp;
    }

    private EnumFacing getFacingDirection(final BlockPos pos) {
        EnumFacing direction = null;
        if (!minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.UP;
        }
        final MovingObjectPosition rayResult = minecraft.theWorld.rayTraceBlocks(new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight(), minecraft.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null) {
            return rayResult.field_178784_b;
        }
        return direction;
    }

    public enum Mode
    {
        STEAL, DROP, STORE
    }

}
