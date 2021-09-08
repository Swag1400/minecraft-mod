package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.Helper;
import me.comu.client.utils.RotationUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Comu on 5/25/2016.
 */
public final class MLG extends ToggleableModule
{

    private double fallStartY;
    private Stopwatch stopwatch = new Stopwatch();
    private BlockData blockBelowData;
    private boolean nextPlaceWater;
    private boolean nextRemoveWater;

    @Override
    protected void onEnable() {
        super.onEnable();
        this.fallStartY = 0.0;
        this.nextPlaceWater = false;
        this.nextRemoveWater = false;
    }

    public MLG()
    {
        super("MLG", new String[] {"MLG", "waterbucket","mlgwaterbucket"}, 0xFFC690D4, ModuleType.WORLD);
        this.listeners.add(new Listener<MotionUpdateEvent>("anti_command_packet_listener")
        {
            @Override
            public void call(MotionUpdateEvent event) {
             {
                if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                        if (!minecraft.thePlayer.onGround && minecraft.thePlayer.motionY < 0.0) {
                            if (fallStartY < minecraft.thePlayer.posY) {
                                fallStartY = minecraft.thePlayer.posY;
                            }
                            if (fallStartY - minecraft.thePlayer.posY > 2.0) {
                                final double x = minecraft.thePlayer.posX + minecraft.thePlayer.motionX * 1.25;
                                final double y = minecraft.thePlayer.posY - minecraft.thePlayer.getEyeHeight();
                                final double z = minecraft.thePlayer.posZ + minecraft.thePlayer.motionZ * 1.25;
                                final BlockPos blockBelow = new BlockPos(x, y, z);
                                final IBlockState blockState = minecraft.theWorld.getBlockState(blockBelow);
                                final IBlockState underBlockState = minecraft.theWorld.getBlockState(blockBelow.offsetDown());
                                if (underBlockState.getBlock().isSolidFullCube() && !minecraft.thePlayer.isSneaking() && (blockState.getBlock() == Blocks.air || blockState.getBlock() == Blocks.snow_layer || blockState.getBlock() == Blocks.tallgrass) && stopwatch.hasCompleted(100)) {
                                    stopwatch.reset();
                                    blockBelowData = getBlockData(blockBelow);
                                    if (blockBelowData != null) {
                                        nextPlaceWater = true;
                                        nextRemoveWater = false;
                                        final float[] rotations = RotationUtils.getRotationsBlock(blockBelowData.position, blockBelowData.face);
                                        event.setRotationYaw(rotations[0]);
                                        event.setRotationPitch(rotations[1]);
                                    }
                                }
                            }
                        } else {
                            fallStartY = minecraft.thePlayer.posY;
                        }
                        if (blockBelowData != null && minecraft.thePlayer.isInWater()) {
                            nextRemoveWater = true;
                            final float[] rotations2 = RotationUtils.getRotationsBlock(blockBelowData.position, blockBelowData.face);
                            event.setRotationYaw(rotations2[0]);
                            event.setRotationPitch(rotations2[1]);
                        }
                }
                else if (blockBelowData != null && nextPlaceWater) {
                    placeWater();
                }
                else if (blockBelowData != null && nextRemoveWater) {
                    getWaterBack();
                }
                }
           }

        });
    }

    private void getWaterBack() {
        for (final Map.Entry<Integer, Item> item : this.getHotbarItems().entrySet()) {
            if (item.getValue().equals(Items.bucket)) {
                final int currentItem = this.swapToItem(item.getKey());
                Helper.sendPacket(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                minecraft.thePlayer.inventory.currentItem = currentItem;
                minecraft.playerController.updateController();
                break;
            }
        }
        this.blockBelowData = null;
        this.nextRemoveWater = false;
    }
    
    private HashMap<Integer, Item> getHotbarItems() {
        final HashMap<Integer, Item> items = new HashMap<Integer, Item>();
        for (int i = 36; i < 45; ++i) {
            if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                items.put(i, itemStack.getItem());
            }
        }
        return items;
    }
    
    private int swapToItem(final int item) {
        minecraft.rightClickDelayTimer = 2;
        final int currentItem = minecraft.thePlayer.inventory.currentItem;
       Helper.sendPacket(new C09PacketHeldItemChange(item - 36));
        minecraft.thePlayer.inventory.currentItem = item - 36;
        minecraft.playerController.updateController();
        return currentItem;
    }
    
    private void placeWater() {
        for (final Map.Entry<Integer, Item> item : getHotbarItems().entrySet()) {
            if (item.getValue().equals(Items.water_bucket)) {
                final int currentItem = swapToItem(item.getKey());
                Helper.sendPacket(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                minecraft.thePlayer.inventory.currentItem = currentItem;
                minecraft.playerController.updateController();
                break;
            }
        }
        this.nextPlaceWater = false;
    }
    
    private BlockData getBlockData(final BlockPos pos) {
        if (!PlayerHelper.getBlacklistedBlocks().contains(minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        return null;
    }

    private class BlockData
    {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(final BlockPos position, final EnumFacing face)
        {
            this.position = position;
            this.face = face;
        }
    }
}
