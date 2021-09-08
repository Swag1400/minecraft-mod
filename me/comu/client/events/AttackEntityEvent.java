package me.comu.client.events;

import net.minecraft.entity.*;

public class AttackEntityEvent extends Event
{
    private final Entity entity;
    private boolean cancelled;
    
    public AttackEntityEvent(final Entity entity) {
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
