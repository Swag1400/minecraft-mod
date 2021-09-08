package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.SprintingAttackEvent;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class Sprint2 extends ToggleableModule
{
    private final Property<Boolean> multiDir = new Property<>(true, "Multi-Direction", "MultiDirection", "multi-dir", "multidir", "direction", "dir", "Multi");
    private final Property<Boolean> keepSprint = new Property<>(true, "KeepSprint", "sprint", "ks", "keep");
    private final Property<Boolean> auto = new Property<>(true, "Automatic", "auto", "a");
    private final Property<Boolean> clientSide = new Property<>(true, "Client-Sided", "client", "clientsided", "clientside", "sideclient", "cs", "sc");
    private final Property<Boolean> legit = new Property<>(false, "Legit", "l");
    
    

    public Sprint2()
    {
        super("Sprint", new String[] {"sprint", "autosprint", "as"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
        this.offerProperties(multiDir, keepSprint, legit, clientSide);
        this.listeners.add(new Listener<TickEvent>("sprint_motion_update_listener")
        {
            @Override
            public void call(TickEvent event)
            {
            	  if (minecraft.thePlayer != null && canSprint()) {
                      minecraft.thePlayer.setSprinting(true);
                  }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("sprint_sprinting_attack_listener") {

			@Override
			public void call(PacketEvent event) {
				if (clientSide.getValue() && event.getPacket() instanceof C0BPacketEntityAction) {
				       final C0BPacketEntityAction packet = (C0BPacketEntityAction)event.getPacket();
				       if (packet.func_180764_b() == C0BPacketEntityAction.Action.START_SPRINTING || packet.func_180764_b() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
				    	   event.setCanceled(true);
				       }
				}
				
			}
        	
        });
        this.listeners.add(new Listener<SprintingAttackEvent>("sprint_sprinting_attack_listener")
        {
            @Override
            public void call(SprintingAttackEvent event)
            {
            	if (canSprint()) {
            		minecraft.thePlayer.setSprinting(true);
            	}
            	if (keepSprint.getValue()) {
            		event.setCanceled(true);
            	}
            }
        });	
    }
    

    
    
    
    @Override
    protected void onDisable()
    {
        super.onDisable();
        minecraft.thePlayer.setSprinting(false);
    }

    public boolean canSprint()
    {
        NoSlow ns = (NoSlow) Gun.getInstance().getModuleManager().getModuleByAlias("noslow");
        return auto.getValue() && (!minecraft.thePlayer.isCollidedHorizontally && !minecraft.thePlayer.isSneaking() && (!legit.getValue() || ns.isRunning() || (legit.getValue() && minecraft.thePlayer.getFoodStats().getFoodLevel() > 5 && !minecraft.thePlayer.isUsingItem()))) && (multiDir.getValue() ? (minecraft.thePlayer.movementInput.moveForward != 0.0f || minecraft.thePlayer.movementInput.moveStrafe != 0.0f) : (minecraft.thePlayer.movementInput.moveForward > 0.0f));
    }
}
