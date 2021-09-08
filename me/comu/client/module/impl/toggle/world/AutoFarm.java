package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import net.minecraft.block.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public final class AutoFarm extends ToggleableModule
{
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.HARVEST, "Mode", "m");
    private final Property<Boolean> silent = new Property<>(true, "Silent", "s"), box = new Property<>(true, "Box","b","esp");
    private BlockPos focus;
    private boolean shouldRender;

    public AutoFarm()
    {
        super("AutoFarm", new String[] {"autoharvest", "autoplant","autofarm", "af"}, 245820, ModuleType.WORLD);
        this.offerProperties(mode, silent, box);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_farm_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                setTag(String.format("Auto %s", mode.getFixedValue()));

                for (double y = event.getPositionY() + 6; y > event.getPositionY() - 6; y--)
                {
                    for (double x = event.getPositionX() - 6; x < event.getPositionX() + 6; x++)
                    {
                        for (double z = event.getPositionZ() - 6; z < event.getPositionZ() + 6; z++)
                        {
                            BlockPos position = new BlockPos(x, y, z); // Target a specific block

                            if (isBlockValid(position))
                            {
                                if (focus == null)
                                {
                                    focus = position; // Set the first block found if the current target is null
                                }
                                else if (minecraft.thePlayer.getDistance(focus.getX(), focus.getY(), focus.getZ()) > minecraft.thePlayer.getDistance(x, y, z))
                                {
                                    focus = position; // Check to see if there is a closer block that can be targeted
                                }
                            }
                        }
                    }
                }

                // Make sure there is a potential target and the held item is plantable
                if (focus == null || (mode.getValue() == Mode.PLANT && !isStackValid(minecraft.thePlayer.getCurrentEquippedItem())))
                {
                    return;
                }

                // Aim at the block
                float[] rotations = getRotations(focus, getFacingDirectionToPosition(focus));
                event.setLockview(!silent.getValue());

                if (silent.getValue())
                {
                    event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0]));
                    event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1]));
                }
                else
                {
                    minecraft.thePlayer.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0]);
                    minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(rotations[1]);
                }

                if (focus != null)   // Makes sure the target is still selected
                {
                    if (isBlockValid(focus))   // Makes sure the target is still valid
                    {
                        if (event.getTime() == MotionUpdateEvent.Time.AFTER)
                        {
                            if (mode.getValue() == Mode.PLANT)
                            {
                                // Plant the item
                                if (minecraft.playerController.func_178890_a(minecraft.thePlayer, minecraft.theWorld, minecraft.thePlayer.getCurrentEquippedItem(), focus, getFacingDirectionToPosition(focus), new Vec3(focus.getX(), focus.getY(), focus.getZ())))
                                {
                                    shouldRender = true;
                                    minecraft.thePlayer.swingItem();
                                }
                            }
                            else if (mode.getValue() == Mode.HARVEST)
                            {
                                // Break the crop
                                if (minecraft.playerController.func_180511_b(focus, getFacingDirectionToPosition(focus)))
                                {
                                    shouldRender = true;
                                    minecraft.thePlayer.swingItem();
                                }
                            } else if (mode.getValue() == Mode.TILL) {
                            if (minecraft.playerController.func_178890_a(minecraft.thePlayer, minecraft.theWorld, minecraft.thePlayer.getCurrentEquippedItem(), focus, getFacingDirectionToPosition(focus), new Vec3(focus.getX(), focus.getY(), focus.getZ())))
                                {
                                    shouldRender = true;
                                    minecraft.thePlayer.swingItem();
                                }
                            }
                        }
                    }
                    else
                    {
                        focus = null;
                    }
                }
            }
        });
        this.listeners.add(new Listener<RenderEvent>("auto_farm_motion_update_listener") {
            @Override
            public void call(RenderEvent event) {
        if (box.getValue()) {
            if (focus != null && shouldRender)
            {
                RenderMethods.enableGL3D();
                double x = focus.getX() - minecraft.getRenderManager().viewerPosX;
                double y = focus.getY() - minecraft.getRenderManager().viewerPosY;
                double z = focus.getZ() - minecraft.getRenderManager().viewerPosZ;
                AxisAlignedBB boundingBox = AxisAlignedBB.fromBounds(x, (mode.getValue() == Mode.PLANT || mode.getValue() == Mode.TILL) ? (y + 1) : y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                if (mode.getValue() == Mode.HARVEST)
                    GlStateManager.color(1.0F, 0.0F, 0.0F, 0.3F);
                else
                    GlStateManager.color(0.0F, 1.0F, 0.0F, 0.3F);
                    RenderGlobal.drawOutlinedBoundingBox(boundingBox, -1);
                    if (mode.getValue() == Mode.HARVEST)
                        GlStateManager.color(1.0F, 0.0f, 0.0F, 0.2F);
                    else if (mode.getValue() == Mode.TILL) {
                        GlStateManager.color(0.458F, 0.3F, 0.0F, 0.3F);
                    } else
                        GlStateManager.color(0.0F, 1.0f, 0.0F, 0.2F);

                RenderMethods.drawBox(boundingBox);
                RenderMethods.disableGL3D();
            }
        }
            }
        });

    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        this.focus = null;
    }

    /**
     * Ensures that the block targeted is valid.
     * If the {@link Mode} is set to Plant, the farmland must be moist and empty.
     * If the {@link Mode} is set to Harvest, the plant must be fully grown.
     * Also ensures the block is within reach.
     *
     * @param position the position of the block
     * @return true if the block is valid, else false
     */
    private boolean isBlockValid(BlockPos position)
    {
        boolean valid = false;
        Block target = this.minecraft.theWorld.getBlockState(position).getBlock();

        if (this.mode.getValue() == Mode.PLANT)
        {
            if (target instanceof BlockFarmland)
            {
                BlockFarmland farmland = (BlockFarmland) target;

                if (!(this.minecraft.theWorld.getBlockState(position.offsetUp()).getBlock() instanceof BlockCrops))   // Ensures there is nothing already planted
                {
                    if (farmland.getMetaFromState(minecraft.theWorld.getBlockState(position)) > -1)   // Ensures the crop is fully moisturised
                    {
                        valid = true;
                    }
                }
            }
            else if (target instanceof BlockSoulSand)
            {
                if (!(this.minecraft.theWorld.getBlockState(position.offsetUp()).getBlock() instanceof BlockNetherWart))
                {
                    valid = true;
                }
            }
            else if ((target instanceof BlockDirt || target instanceof BlockGrass || target instanceof BlockSand) && !(this.minecraft.theWorld.getBlockState(position.offsetUp()).getBlock() instanceof BlockCrops) && !(this.minecraft.theWorld.getBlockState(position.offsetUp()).getBlock() instanceof BlockReed))
            {
                if (minecraft.theWorld.isAnyLiquid(target.getCollisionBoundingBox(minecraft.theWorld, position, minecraft.theWorld.getBlockState(position))))   // Ensures the dirt/grass/sand is touching water
                {
                    valid = true;
                }
            }
        }
        else if (this.mode.getValue() == Mode.HARVEST)
        {
            if (target instanceof BlockCrops)
            {
                BlockCrops crops = (BlockCrops) target;
                valid = crops.getMetaFromState(minecraft.theWorld.getBlockState(position)) == 7; // Ensures the crop is fully grown
            }
            else if (target instanceof BlockNetherWart)
            {
                BlockNetherWart wart = (BlockNetherWart) target;
                valid = wart.getMetaFromState(minecraft.theWorld.getBlockState(position)) == 3; // Ensures the wart is fully grown
            }
            else if (target instanceof BlockReed)
            {
                valid = this.minecraft.theWorld.getBlockState(position.offsetDown()).getBlock() instanceof BlockReed; // Ensures the reed is above another reed, meaning it has grown
            }
        }
        else if (this.mode.getValue() == Mode.TILL) {
            if  (minecraft.thePlayer.getHeldItem().getItem() instanceof ItemHoe){
                    if (target instanceof BlockGrass || target instanceof BlockDirt) {
                        valid = true;
                    }
            } else {
                valid = false;
            }
        }

        return valid && this.getFacingDirectionToPosition(position) != null && minecraft.thePlayer.getDistance(position.getX(), position.getY(), position.getZ()) < minecraft.playerController.getBlockReachDistance() - 1.0D;
    }

    /**
     * Checks if the specified stack is a valid plantable item.
     *
     * @param stack the item stack to check
     * @return true if the stack is valid, else false
     */
    private boolean isStackValid(ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }

        Block block = this.minecraft.theWorld.getBlockState(this.focus).getBlock();

        if (block instanceof BlockFarmland)
        {
            if (stack.getItem() instanceof ItemSeeds || stack.getItem() == Items.carrot || stack.getItem() == Items.potato)
            {
                return true;
            }
        }
        else if (block instanceof BlockSoulSand)
        {
            if (stack.getItem() == Items.nether_wart)
            {
                return true;
            }
        }
        else if (block instanceof BlockDirt || block instanceof BlockGrass || block instanceof BlockSand)
        {
            if (this.minecraft.theWorld.isAnyLiquid(block.getCollisionBoundingBox(this.minecraft.theWorld, this.focus, this.minecraft.theWorld.getBlockState(this.focus))))   // Ensures the dirt/grass/sand is touching water
            {
                if (stack.getItem() instanceof ItemReed)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the player's rotations compared to the specified location.
     *
     * @param position the position of the block
     * @param facing the facing of the block
     * @return yaw and pitch
     */
    private float[] getRotations(BlockPos position, EnumFacing facing)
    {
        double xDifference = (position.getX() + 0.5D + facing.getDirectionVec().getX() * 0.25D) - this.minecraft.thePlayer.posX;
        double yDifference = (position.getY() + 0.5D + facing.getDirectionVec().getY() * 0.25D) - this.minecraft.thePlayer.posY;
        double zDifference = (position.getZ() + 0.5D + facing.getDirectionVec().getZ() * 0.25D) - this.minecraft.thePlayer.posZ;
        double positions = MathHelper.sqrt_double(xDifference * xDifference + zDifference * zDifference);
        float yaw = (float)(Math.atan2(zDifference, xDifference) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) - (Math.atan2(yDifference, positions) * 180.0D / Math.PI);
        return new float[] {yaw, pitch};
    }

    /**
     * Returns the direction that a block is facing based on its position.
     *
     * @param position the position of the block
     * @return the direction that the block is facing
     */
    private EnumFacing getFacingDirectionToPosition(BlockPos position)
    {
        EnumFacing direction = null;

        if (!this.minecraft.theWorld.getBlockState(position.add(0, 1, 0)).getBlock().isSolidFullCube())
        {
            direction = EnumFacing.UP;
        }
        else if (!this.minecraft.theWorld.getBlockState(position.add(0, -1, 0)).getBlock().isSolidFullCube())
        {
            direction = EnumFacing.DOWN;
        }
        else if (!this.minecraft.theWorld.getBlockState(position.add(1, 0, 0)).getBlock().isSolidFullCube())
        {
            direction = EnumFacing.EAST;
        }
        else if (!this.minecraft.theWorld.getBlockState(position.add(-1, 0, 0)).getBlock().isSolidFullCube())
        {
            direction = EnumFacing.WEST;
        }
        else if (!this.minecraft.theWorld.getBlockState(position.add(0, 0, 1)).getBlock().isSolidFullCube())
        {
            direction = EnumFacing.SOUTH;
        }
        else if (!this.minecraft.theWorld.getBlockState(position.add(0, 0, 1)).getBlock().isSolidFullCube())
        {
            direction = EnumFacing.NORTH;
        }

        return direction;
    }



    public enum Mode
    {
        PLANT, HARVEST, TILL
    }
}
