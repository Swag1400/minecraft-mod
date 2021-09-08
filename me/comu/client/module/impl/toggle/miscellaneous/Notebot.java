package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public final class Notebot extends ToggleableModule {
    public Notebot() {
        super("Notebot", new String[]{"notebot", "nb", "note", "bot"}, 0xFFB2E665, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("note_bot_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                int x = minecraft.thePlayer.getPosition().getX();
                int y = minecraft.thePlayer.getPosition().getY() - 1;
                int z = minecraft.thePlayer.getPosition().getZ();

                if (getBlock(x, y, z) instanceof BlockNote) {
                    playNoteblock(minecraft.thePlayer.getPosition());
                }
            }
        });
    }

    private void playNoteblock(BlockPos blockPos) {
        minecraft.func_175102_a().addToSendQueue(new C08PacketPlayerBlockPlacement(blockPos, -1, minecraft.thePlayer.getCurrentEquippedItem(), -1, -1, -1));
        minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch, minecraft.thePlayer.onGround));
        Logger.getLogger().printToChat("Played Noteblock");
    }

    public static Block getBlock(int x, int y, int z) {
        return ClientUtils.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }
}
