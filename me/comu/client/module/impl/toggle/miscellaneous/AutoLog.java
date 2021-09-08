package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.utils.Helper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoLog extends ToggleableModule {

    private NumberProperty<Float> hearts = new NumberProperty<>(2f, 0.5f, 10f, 0.5f, "Hearts", "health", "h", "heart");
    private NumberProperty<Integer> distance = new NumberProperty<>(15, 1, 128, 2, "Distance", "blocks", "d", "b");
    private EnumProperty<Mode> mode = new EnumProperty<>(Mode.HEALTH, "Mode", "m");


    public AutoLog() {
        super("AutoLog", new String[]{"autolog", "autodc", "autodisconnect"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.offerProperties(hearts, mode, distance);
        this.listeners.add(new Listener<MotionUpdateEvent>("bruh") {
            @Override
            public void call(MotionUpdateEvent event) {

                if (mode.getValue() == Mode.HEALTH) {
                    if (minecraft.thePlayer.getHealth() / 2 <= hearts.getValue()) {
                        Helper.sendPacket(new C09PacketHeldItemChange(420));
                        toggle();
                    }
                } else if (mode.getValue() == Mode.ENEMY) {
                    EntityPlayer player = getClosestPlayer(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, distance.getValue());
                    if (player != null && !Gun.getInstance().getFriendManager().isFriend(player.getName())) {
                        Helper.sendPacket(new C09PacketHeldItemChange(420));
                        toggle();
                    }
                }
            }
        });
    }

    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        double var9 = -1.0D;
        EntityPlayer var11 = null;

        for (int var12 = 0; var12 < minecraft.theWorld.playerEntities.size(); ++var12) {
            EntityPlayer var13 = (EntityPlayer) minecraft.theWorld.playerEntities.get(var12);
            if (var13 == minecraft.thePlayer)
                continue;
            if (IEntitySelector.field_180132_d.apply(var13)) {
                double var14 = var13.getDistanceSq(x, y, z);
                if ((distance < 0.0D || var14 < distance * distance) && (var9 == -1.0D || var14 < var9)) {
                    var9 = var14;
                    var11 = var13;
                }
            }
        }

        return var11;
    }

    private enum Mode {HEALTH, ENEMY}
}
