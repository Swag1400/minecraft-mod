package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.input.Mouse;

public final class AutoClicker extends ToggleableModule {
    private final NumberProperty<Integer> cps = new NumberProperty<>(13, 1, 25, 1, "CPS", "clicks", "click");
    private final Property<Boolean> randomize = new Property<>(false, "Randomize", "random", "r");
    private final Property<Boolean> showCPS = new Property<>(false, "Show-CPS", "showcps", "scps");

    private final Stopwatch stopwatch = new Stopwatch();

    public AutoClicker() {
        super("AutoClicker", new String[]{"autoclicker", "ac", "clicker"}, 0xFFB990D4, ModuleType.COMBAT);
        this.offerProperties(cps, randomize);
        this.listeners.add(new Listener<TickEvent>("auto_clicker_tick_listener") {
            @Override
            public void call(TickEvent event) {
                if (minecraft.currentScreen == null) {
                    if (Mouse.isButtonDown(0)) {
                        if (randomize.getValue()) {
                            if (stopwatch.hasCompleted(1000L / ClientUtils.randomNum(cps.getValue()-3, cps.getValue()+1))) {
                                KeyBinding.setKeyBindState(-100, true);
                                KeyBinding.onTick(-100);
                                stopwatch.reset();
                            } else {
                                KeyBinding.setKeyBindState(-100, false);
                            }
                        } else {
                            if (stopwatch.hasCompleted(1000L / cps.getValue())) {
                                KeyBinding.setKeyBindState(-100, true);
                                KeyBinding.onTick(-100);
                                stopwatch.reset();
                            } else {
                                KeyBinding.setKeyBindState(-100, false);
                            }
                        }
                    }
                }
            }

        });
        this.listeners.add(new Listener<PacketEvent>("auto_clicker_packet_listener") {
            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof C02PacketUseEntity) {
                    C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity) event.getPacket();

                    if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                        if (c02PacketUseEntity.getEntityFromWorld(minecraft.theWorld) instanceof EntityPlayer) {
                            EntityPlayer entityPlayer = (EntityPlayer) c02PacketUseEntity.getEntityFromWorld(minecraft.theWorld);

                            if (Gun.getInstance().getFriendManager().isFriend(entityPlayer.getName())) {
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        });
    }




}
