package me.comu.client.module.impl.toggle.world;


import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Tower extends ToggleableModule {

    private final Property<Boolean> slowMode = new Property<>(false, "Slow","slowmode","slowm","slow-mode","sm","s");
    private static final Stopwatch stopwatch = new Stopwatch();
    private float[] rotations;

    public Tower() {
        super("Tower", new String[]{"tower","tow","towers","fastup","towerup"}, 0xFFC48F, ModuleType.WORLD);
        this.offerProperties(slowMode);
        this.listeners.add(new Listener<MotionUpdateEvent>("motion_update_event") {
            @Override
            public void call(MotionUpdateEvent event) {

            if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                final BlockPos sent = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 1.0, minecraft.thePlayer.posZ);
                final EnumFacing player = this.getFacingDirection(sent);
            try {
                if (stopwatch.hasCompleted(slowMode.getValue() ? 150 : 75) && minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
                    minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.1, minecraft.thePlayer.posZ);
                    rotations = ClientUtils.getBlockRotations(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 1.0, minecraft.thePlayer.posZ);
                    event.setRotationYaw(rotations[0]);
                    event.setRotationPitch(rotations[1]);
                    if (!minecraft.thePlayer.onGround) {
                        minecraft.playerController.func_178890_a(minecraft.thePlayer, minecraft.theWorld, minecraft.thePlayer.getCurrentEquippedItem(), sent, player, new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 1.0, minecraft.thePlayer.posZ));
                        minecraft.thePlayer.swingItem();
                    }
                   stopwatch.reset();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
            }

            }
            private EnumFacing getFacingDirection(final BlockPos pos) {
                EnumFacing direction = null;
                if (!minecraft.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube()) {
                    direction = EnumFacing.UP;
                }
                else if (!minecraft.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isSolidFullCube()) {
                    direction = EnumFacing.DOWN;
                }
                else if (!minecraft.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isSolidFullCube()) {
                    direction = EnumFacing.EAST;
                }
                else if (!minecraft.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isSolidFullCube()) {
                    direction = EnumFacing.WEST;
                }
                else if (!minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube()) {
                    direction = EnumFacing.SOUTH;
                }
                else if (!minecraft.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube()) {
                    direction = EnumFacing.NORTH;
                }
                return direction;
            }
        });

    }
}
