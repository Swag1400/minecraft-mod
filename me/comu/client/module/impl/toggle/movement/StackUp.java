package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public final class StackUp extends ToggleableModule {

    private EntityLivingBase entity;



    public StackUp() {
        super("StackUp", new String[]{"stack-up", "stackup", "sp", "stacku","stack"}, 0xFF3A35, ModuleType.MOVEMENT);
        listeners.add(new Listener<MotionUpdateEvent>("stack_up_event") {
            @Override
            public void call(MotionUpdateEvent event) {
                if (entity != null && minecraft.thePlayer.getDistanceToEntity(entity) >= 0.6) {
                    final float[] rotations = EntityHelper.getRotations(entity);
                    minecraft.thePlayer.rotationYaw = rotations[0];
                    minecraft.gameSettings.keyBindForward.pressed = true;
                } else {
                    minecraft.gameSettings.keyBindForward.pressed = false;
                }
                if (minecraft.thePlayer.isCollidedHorizontally && minecraft.thePlayer.onGround) {
                    minecraft.thePlayer.jump();
                }
                if (entity != null && minecraft.thePlayer.getDistanceToEntity(entity) > 4.0f && minecraft.thePlayer.onGround) {
                    minecraft.gameSettings.keyBindJump.pressed = true;
                } else if (entity != null && minecraft.thePlayer.getDistanceToEntity(entity) < 4.0f && minecraft.thePlayer.onGround) {
                    minecraft.gameSettings.keyBindJump.pressed = false;
                }

            }
        });
    }
    private EntityLivingBase findClosestEntity() {
        double distance = Double.MAX_VALUE;
        EntityLivingBase entity = null;
        for (final Object object : minecraft.theWorld.loadedEntityList) {
            if (object instanceof EntityLivingBase) {
                final EntityLivingBase e = (EntityLivingBase)object;
                if (e.getDistanceToEntity(minecraft.thePlayer) >= distance) {
                    continue;
                }
                if (!isValid(e)) {
                    continue;
                }
                entity = e;
                distance = e.getDistanceToEntity(minecraft.thePlayer);
            }
        }
        return entity;
    }

    private boolean isValid(final EntityLivingBase entity) {
            return entity != null && entity != minecraft.thePlayer && entity instanceof EntityPlayer && entity.getDistanceToEntity(minecraft.thePlayer) <= 1000.0f && entity.isEntityAlive() && (!entity.isInvisible() || entity.getTotalArmorValue() != 0);
        }
    @Override
    protected void onEnable() {
        super.onEnable();
        entity = findClosestEntity();
    }

}
