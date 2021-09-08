package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.RenderGameInfoEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;

;

/*
 * made by comu 4/23/16
 */

public final class Scaffold extends ToggleableModule
{
	
	 private final Property<Boolean> safe = new Property<>(true, "Safewalk","safe","walk","safe-walk");
    private final Property<Boolean> amountRender = new Property<>(true, "Render-Amount","amount","render","blockrender","amountrender");
    private List<Block> invalid;
    private Stopwatch stopwatch = new Stopwatch();
    private BlockData blockData;


    public Scaffold()

    {
        super("Scaffold", new String[] {"Scaffold", "scaff"}, 0xFFD14D7E, ModuleType.WORLD);
        this.offerProperties(safe, amountRender);
        this.listeners.add(new Listener<MotionUpdateEvent>("scaffold_move_player_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (event.getTime() == Time.BEFORE)
                {
                    blockData = null;

                    if (!ClientUtils.player().isSneaking())
                    {
                        final BlockPos blockBelow1 = new BlockPos(ClientUtils.player().posX, ClientUtils.player().posY - 1.0, ClientUtils.player().posZ);

                        if (ClientUtils.world().getBlockState(blockBelow1).getBlock() == Blocks.air)
                        {
                            blockData = getBlockData(blockBelow1, invalid);

                            if (blockData != null && getBlockAmount() != 0)
                            {
                                final float yaw = aimAtLocation(blockData.position.getX(), blockData.position.getY(), blockData.position.getZ(), blockData.face)[0];
                                final float pitch = aimAtLocation(blockData.position.getX(), blockData.position.getY(), blockData.position.getZ(), blockData.face)[1];
                                event.setRotationYaw(yaw);
                                event.setRotationPitch(pitch);
                            }
                        }
                    }
                }
              

                if (event.getTime() == Time.AFTER && blockData != null && (stopwatch.hasCompleted(100L)))
                {
                    ClientUtils.mc().rightClickDelayTimer = 3;
                    final int heldItem = ClientUtils.player().inventory.currentItem;

                    for (int i = 0; i < 9; ++i)
                    {
                        if (ClientUtils.player().inventory.getStackInSlot(i) != null && ClientUtils.player().inventory.getStackInSlot(i).getItem() instanceof ItemBlock)
                        {
                            ClientUtils.player().inventory.currentItem = i;
                            break;
                        }
                    }

                    if (ClientUtils.playerController().func_178890_a(ClientUtils.player(), ClientUtils.world(), ClientUtils.player().getHeldItem(), blockData.position, blockData.face, new Vec3(blockData.position.getX(), blockData.position.getY(), blockData.position.getZ())))
                    {
                        ClientUtils.packet(new C0APacketAnimation());
                    }

                    // that resets the delay so it does it agian, try chaning the stopwatch to like 100

                    ClientUtils.player().inventory.currentItem = heldItem;
                    stopwatch.reset();
                }
            }
        });
        
        this.listeners.add(new Listener<MovePlayerEvent>("scaffold_move_player_listener") {

			@Override
			public void call(MovePlayerEvent event) {
                event.setSafe(safe.getValue());
				
			}
        	
        });
        this.listeners.add(new Listener<RenderGameInfoEvent>("scaffold_move_player_listener") {

            @Override
            public void call(RenderGameInfoEvent event) {
                String color = "\247";
                if (getBlockAmount() >= 64)
                    color += "a";
                else if (getBlockAmount() >= 32)
                    color += "2";
                else if (getBlockAmount() >= 16)
                    color += "e";
                else if (getBlockAmount() > 0)
                    color += "c";
                else color += "4";
                if (amountRender.getValue())
                    minecraft.fontRenderer.drawStringWithShadow(color + Integer.toString(getBlockAmount()), 650, 350, 0xFFFFF);

            }

        });
        Gun.getInstance().getEventManager().register(new Listener<RenderGameInfoEvent>("textgui_render_game_info_listener") {
            @Override
            public void call(RenderGameInfoEvent event) {
            int blocks = 0;
            }
        });

        this.invalid = Arrays.asList(Blocks.air, Blocks.water, Blocks.fire, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava);
    }

    private float[] aimAtLocation(final double x, final double y, final double z, final EnumFacing facing)
    {
        final EntitySnowball temp = new EntitySnowball(ClientUtils.world());
        temp.posX = x + 0.5D;
        temp.posY = y - 0.5D;
        temp.posZ = z + 0.5D;
        return this.aimAtLocation(temp.posX, temp.posY, temp.posZ);
    }

    private float[] aimAtLocation(final double positionX, final double positionY, final double positionZ)
    {
        final double x = positionX - ClientUtils.player().posX;
        final double y = positionY - ClientUtils.player().posY;
        final double z = positionZ - ClientUtils.player().posZ;
        final double distance = MathHelper.sqrt_double(x * x + z * z);
        return new float[] { (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(y, distance) * 180.0 / 3.141592653589793)) };
    }

    private BlockData getBlockData(final BlockPos pos, final List list)
    {
        return list.contains(ClientUtils.world().getBlockState(pos.add(0, -1, 0)).getBlock()) ? (list.contains(ClientUtils.world().getBlockState(pos.add(-1, 0, 0)).getBlock()) ? (list.contains(ClientUtils.world().getBlockState(pos.add(1, 0, 0)).getBlock()) ? (list.contains(ClientUtils.world().getBlockState(pos.add(0, 0, -1)).getBlock()) ? (list.contains(ClientUtils.world().getBlockState(pos.add(0, 0, 1)).getBlock()) ? null : new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH)) : new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH)) : new BlockData(pos.add(1, 0, 0), EnumFacing.WEST)) : new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST)) : new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
    }
    private int getBlockAmount() {
        if (!minecraft.thePlayer.isDead) {
            int blocks = 0;

            for (int index = 9; index < 45; index++)
            {
                ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

                if (itemStack != null && isItemBlock(itemStack)) {
                    blocks += itemStack.stackSize;

                }
            }

            return blocks;
        }
        return -1;
    }

    private boolean isItemBlock(ItemStack itemStack)
    {
        if ((itemStack.getItem() instanceof ItemBlock))
        {
           return true;

        }

        return false;
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
    public static Block getBlock(int x, int y, int z)
    {
        return ClientUtils.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }



    private int getBlockSlot()
    {
        for (int i = 36; i < 45; i++)
        {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();

            if ((itemStack != null) && ((itemStack.getItem() instanceof ItemBlock)))
            {
                return i - 36;
            }
        }

        return -1;
    }
}

