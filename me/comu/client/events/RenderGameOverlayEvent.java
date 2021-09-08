package me.comu.client.events;

import me.comu.api.event.Event;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.item.EntityItem;

public class RenderGameOverlayEvent extends Event
{
    private ScaledResolution scaledResolution;
    private EntityItem entityItem;
    private final Type type;
    private boolean renderPumpkin = false, renderItems = false, renderHurtcam = false, renderFire = false;

    public RenderGameOverlayEvent(Type type)
    {
        this.type = type;
    }

    public RenderGameOverlayEvent(EntityItem entityItem)
    {
        this.type = Type.ITEM;
        this.entityItem = entityItem;
    }

    public RenderGameOverlayEvent(ScaledResolution scaledResolution)
    {
        this.type = Type.IN_GAME;
        this.scaledResolution = scaledResolution;
    }

    public Type getType()
    {
        return type;
    }

    public EntityItem getEntityItem()
    {
        return entityItem;
    }

    public ScaledResolution getScaledResolution()
    {
        return this.scaledResolution;
    }

    public boolean isRenderFire()
    {
        return renderFire;
    }

    public void setRenderFire(boolean renderFire)
    {
        this.renderFire = renderFire;
    }

    public boolean isRenderPumpkin()
    {
        return renderPumpkin;
    }

    public void setRenderPumpkin(boolean renderPumpkin)
    {
        this.renderPumpkin = renderPumpkin;
    }

    public boolean isRenderItems()
    {
        return renderItems;
    }

    public void setRenderItems(boolean renderItems)
    {
        this.renderItems = renderItems;
    }

    public boolean isRenderHurtcam()
    {
        return renderHurtcam;
    }

    public void setRenderHurtcam(boolean renderHurtcam)
    {
        this.renderHurtcam = renderHurtcam;
    }

    public enum Type
    {
        IN_GAME, PUMPKIN, ITEM, HURTCAM, FIRE, GUI
    }
}
