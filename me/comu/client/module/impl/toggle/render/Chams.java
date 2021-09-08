package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.events.RenderChestEvent;
import me.comu.client.events.RenderEntityEvent;
import me.comu.client.events.RenderSkullEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import org.lwjgl.opengl.GL11;

public final class Chams extends ToggleableModule {
    private final Property<Boolean> entities = new Property<>(true, "Entities", "entity", "entitie", "e"), chests = new Property<>(true, "Chests", "chest", "c"), skull = new Property<>(true, "Skull","skulls","head","heads");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.CLEAR, "Mode", "m");

    public Chams() {
        super("Chams", new String[]{"chams", "cham","ch"}, 0xFDFF0C, ModuleType.RENDER);
        this.offerProperties(mode, entities, chests, skull);
        setDrawn(true);
        this.listeners.add(new Listener<RenderEntityEvent>("Chams_render_entity_listener") {
            @Override
            public void call(RenderEntityEvent event) {
                if (entities.getValue()) {
                    switch (event.getTime()) {
                        case BEFORE:
                            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                            GL11.glPolygonOffset(1F, -2000000F);
                            break;

                        case AFTER:
                            GL11.glPolygonOffset(1F, 2000000F);
                            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                            break;

                    }

                }
            }

        });
        this.listeners.add(new Listener<RenderChestEvent>("Chams_render_chest_listener") {
            @Override
            public void call(RenderChestEvent event) {
                if (chests.getValue()) {
                    switch (event.getTime()) {
                        case BEFORE:
                            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                            GL11.glPolygonOffset(1F, -2000000F);
                            break;

                        case AFTER:
                            GL11.glPolygonOffset(1F, 2000000F);
                            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                            break;
                    }
                }
            }
        });
        this.listeners.add(new Listener<RenderSkullEvent>("render_skull_event_listener") {
            @Override
            public void call(RenderSkullEvent event) {
                if (skull.getValue()) {
                    switch(event.getTime()) {
                        case BEFORE:
                            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                            GL11.glPolygonOffset(1F, -2000000F);
                            break;
                        case AFTER:
                            GL11.glPolygonOffset(1F, 2000000F);
                            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        break;
                    }
                }
            }
        });
    }

    public enum Mode {
        CLEAR, AMBER, AQUA, RAINBOW
    }

}
