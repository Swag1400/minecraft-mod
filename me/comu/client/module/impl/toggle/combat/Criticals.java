package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.movement.Speed;
import me.comu.client.properties.EnumProperty;
import me.comu.client.utils.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class Criticals extends ToggleableModule
{
	private final EnumProperty<Mode> mode;
	private static boolean alt;

	public Criticals() {
		super("Criticals", new String[] { "criticals", "crit", "crits", "critical" }, -2380220, ModuleType.COMBAT);
		this.mode = new EnumProperty<Mode>(Mode.PACKET, new String[] { "Mode", "m" });
		this.offerProperties(this.mode);
		this.listeners.add(new Listener<PacketEvent>("criticals_packet_listener") {
			@Override
			public void call(final PacketEvent event) {
				if (event.getPacket() instanceof C02PacketUseEntity) {
					final C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)event.getPacket();
					if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                        final Speed speed = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
                        final EnumProperty<Speed.Mode> speedMode = (EnumProperty<Speed.Mode>) speed.getPropertyByAlias("Mode");
                        if ((speed != null && speed.isRunning() && speedMode.getValue() == Speed.Mode.HOP) || (speed != null && speed.isRunning() && speedMode.getValue() == Speed.Mode.NCPHOP) || (speed != null && speed.isRunning() && speedMode.getValue() == Speed.Mode.YPORT)) {
                            return;
                        }
                        if (!KillAura.shouldCrit) {
                            switch (mode.getValue()) {
                                case PACKET:
                                    crit();
                                    break;
                                case VANILLA:
                                    crit();
                                    break;
                                case JUMP:
                                    if (minecraft.thePlayer.onGround)
                                    	minecraft.thePlayer.jump();
                                    break;
                                case NEW:
                                    newCrit();
                                    break;
                                case ALTERNATE:
                                    alternateCrit();
                                    break;
                                case TEST:
                                        testCrit();
                                    break;
                                case STILL:
                                    if (!PlayerHelper.isMoving()) {
                                        crit();
                                    }
                                    break;
                                case MINI:
                                    miniCrit();
                                    break;
							}

						}
					}
				}
			}
		});
	}

	public static void crit() {
		if (Criticals.minecraft.thePlayer.isCollidedVertically) {
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.0625, minecraft.thePlayer.posZ, false));
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.11E-4, minecraft.thePlayer.posZ, false));
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
		}
	}
	public static void newCrit() {
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.1625, minecraft.thePlayer.posZ, false));
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 4.0E-6, minecraft.thePlayer.posZ, false));
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-6, minecraft.thePlayer.posZ, false));
        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));

		}
		public static void alternateCrit() {
	    if (alt) {
            Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.07, minecraft.thePlayer.posZ, false));
            Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
            Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.012511, minecraft.thePlayer.posZ, false));
            Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
            alt = true;
        }
        alt = false;
        }
    public static void testCrit() {
        Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.05, minecraft.thePlayer.posZ, false));
        Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
        Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.15, minecraft.thePlayer.posZ, false));
        Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
    }
    
    public static void miniCrit()
    {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch, true));
    }


	public enum Mode
	{
		PACKET, VANILLA, JUMP, NEW, ALTERNATE, STILL, MINI, TEST
	}
}
