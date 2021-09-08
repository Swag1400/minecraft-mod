package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.item.ItemBow;
import net.minecraft.potion.Potion;

public final class BowBoost extends ToggleableModule
{
	private final Potion[] potions = new Potion[]{Potion.moveSpeed};
    private final Property<Boolean> silent = new Property<>(false, "Silent", "s");
    public BowBoost()
    {
        super("BowBoost", new String[] {"bowboost", "boostbow", "bboost","bb","bow-boost"}, 0xFF96D490, ModuleType.COMBAT);

        this.listeners.add(new Listener<MotionUpdateEvent>("tapper")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
         //   	for(Potion p : potions) {
           // 		minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(0);
            //	}
            	if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && minecraft.thePlayer.isUsingItem()) {
                    if (silent.getValue())
                    {
                     //   event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0]));
                       // event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1] + (minecraft.thePlayer.getDistanceToEntity(target) * -0.15F)));
                    }
                    else
                    {
                        //minecraft.thePlayer.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0]);
                            minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(-30);

                        if (minecraft.gameSettings.keyBindJump.pressed = true && minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && minecraft.thePlayer.isUsingItem()) {

                        }
                        	/*if (silent.getValue()) {
                        		
                        	} else {
                        	*/
                        	//minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(90);
                       // 	Logger.getLogger().printToChat("Wrapped angle to 90 degrees");

                        
                    
            	}
                    
                }
                
            }

        
    
        });
     //   this.listeners.add(new Listener<InputEvent>("auto_eat_update_listener") {
       //     @EventTarget
         //   public void call(InputEvent e) {
           //     if (minecraft.currentScreen == null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
             //       if (Mouse.isButtonDown(1)) {
               //         Logger.getLogger().printToChat("Wrapped player pitch angle to 30 degrees");
                //    }
                //}
           // }
        //});
}
}
   