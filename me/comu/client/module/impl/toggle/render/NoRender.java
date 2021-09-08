package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.events.AntiLagEvent;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;

public final class NoRender extends ToggleableModule {
    private final Property<Boolean> items = new Property<>(true, "Items", "noitem", "noitems", "no-items", "no-item", "item", "ni");
    private final Property<Boolean> players = new Property<>(false, "Players", "player", "p", "entities", "people");
    private final Property<Boolean> monsters = new Property<>(false, "Monsters", "monster", "mon", "m", "mob", "mobs");
    private final Property<Boolean> animals = new Property<>(false, "Animals", "animal", "ign", "hidename", "name", "a");
    private final Property<Boolean> tnt = new Property<>(true, "TnT", "dynamite");
    private final Property<Boolean> blocks = new Property<>(true, "Blocks","block","Sand", "s", "gravel", "g");
    private final Property<Boolean> spawner = new Property<>(true, "Spawners", "spawner");
    private final Property<Boolean> piston = new Property<>(true, "Pistons", "piston", "stickypiston", "stickypistons");
    private final Property<Boolean> explosions = new Property<>(true, "Explosions", "explosion", "ex", "e");
    private final Property<Boolean> rain = new Property<>(false, "Rain", "toggledownfall", "raindrops","r");
    private final Property<Boolean> redstone = new Property<>(true, "Redstone", "red", "stone","rstone");
    private final Property<Boolean> signs = new Property<>(true, "Signs", "sign");

    public NoRender() {
        super("NoRender", new String[]{"norender", "no-render", "noitem", "entityrender", "nolag", "antilag", "anti-lag", "no-lag"}, 0xFFFA8D61, ModuleType.RENDER);
        this.offerProperties(items, players, monsters, animals, tnt, blocks, spawner, piston, explosions, rain, redstone, signs);
        this.listeners.add(new Listener<RenderGameOverlayEvent>("name_protect_show_message_listener") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                if (event.getType().equals(RenderGameOverlayEvent.Type.ITEM)) {
                    event.setRenderItems(items.getValue());
                    if (items.getValue()) {
                        minecraft.theWorld.removeEntity(event.getEntityItem());
                    }
                }
            }
        });
        this.listeners.add(new Listener<AntiLagEvent>("name_protect_show_message_listener") {

            @Override
            public void call(AntiLagEvent event) {
                event.setRain(rain.getValue());
                event.setPlayers(players.getValue());
                event.setMonsters(monsters.getValue());
                event.setAnimals(animals.getValue());
                event.setTnt(tnt.getValue());
                event.setBlocks(blocks.getValue());
                event.setSpawner(spawner.getValue());
                event.setPiston(piston.getValue());
                event.setExplosions(explosions.getValue());
                event.setSigns(signs.getValue());

            }
        });
    }
}
