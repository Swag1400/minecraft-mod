package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.ViewmodelEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class FastBow extends ToggleableModule
{

    public FastBow()
    {
        super("FastBow", new String[] {"FastBow", "Fast-Bow", "fbow","fb","fast"}, 0xFF96D490, ModuleType.COMBAT);
        this.offerProperties();
        this.listeners.add(new Listener<MotionUpdateEvent>("fast_bow_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (minecraft.thePlayer.isDead) {
                    minecraft.getTimer().timeSyncAdjustment = 1.0;
                }
                if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && minecraft.gameSettings.keyBindUseItem.pressed && isRunning()) {
                    minecraft.getTimer().timeSyncAdjustment = 20.0;
                    if (minecraft.thePlayer.getItemInUseDuration() >= 21) {
                        minecraft.playerController.onStoppedUsingItem(minecraft.thePlayer);
                    }
                }
                if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && !minecraft.gameSettings.keyBindUseItem.pressed && !((ToggleableModule) Gun.getInstance().getModuleManager().getModuleByAlias("speed")).isRunning() && isRunning()) {
                    minecraft.getTimer().timeSyncAdjustment = 1.0;
                }
            }
        });
        this.listeners.add(new Listener<MovePlayerEvent>("jesus_packet_listener")
        {
			@Override
			public void call(MovePlayerEvent event) {
		        if (minecraft.getTimer().timeSyncAdjustment == 20.0 && !minecraft.thePlayer.isDead && minecraft.thePlayer.isEntityAlive()) {
		            event.setMotionX(0.0);
		            event.setMotionZ(0.0);
		        }
			}
			});
    this.listeners.add(new Listener<PacketEvent>("jesus_packet_listener")
        {
			@Override
			public void call(PacketEvent event) {
		        if(event.getPacket() instanceof C03PacketPlayer && isRunning()) {
		        	Packet packet = (C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket();
		        			 minecraft.func_175102_a().addToSendQueue(event.getPacket());
		        }
		        Gun.getInstance().getEventManager().unregister(this);
		        }
			
			});
    this.listeners.add(new Listener<ViewmodelEvent>("shit") {

		@Override
		public void call(ViewmodelEvent event) {
			if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && minecraft.gameSettings.keyBindUseItem.pressed) {
		event.setNoFov(true);
			//	event.setNoPitchLimit(true);
			} 
			
	
		}
    });
    }
    public void onDisable() {
    	minecraft.getTimer().timerSpeed = 1.0f;
    	minecraft.getTimer().timeSyncAdjustment = 1.0;
    }
    
}
