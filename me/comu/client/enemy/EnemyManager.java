package me.comu.client.enemy;

import com.google.gson.*;

import me.comu.api.registry.ListRegistry;
import me.comu.client.config.Config;
import me.comu.client.core.Gun;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class EnemyManager extends ListRegistry<Enemy>
{
    public EnemyManager()
    {
        registry = new ArrayList<>();
        new Config("enemies.json")
        {
            @Override
            public void load(Object... source)
            {
                try
                {
                    if (!getFile().exists())
                    {
                        getFile().createNewFile();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (!getFile().exists())
                {
                    return;
                }

                JsonElement root;

                try (FileReader reader = new FileReader(getFile()))
                {
                    root = new JsonParser().parse(reader);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return;
                }

                if (!(root instanceof JsonArray))
                {
                    return;
                }

                JsonArray enemies = (JsonArray) root;
                enemies.forEach(node ->
                {
                    if (!(node instanceof JsonObject))
                    {
                        return;
                    }
                    try {
                        JsonObject enemyNode = (JsonObject) node;
                        Gun.getInstance().getEnemyManager().getRegistry().add(new Enemy(
                            enemyNode.get("enemy-label").getAsString(), enemyNode.get("enemy-alias").getAsString()));
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void save(Object... destination)
            {
                if (getFile().exists())
                {
                    getFile().delete();
                }

                if (Gun.getInstance().getEnemyManager().getRegistry().isEmpty())
                {
                    return;
                }

                JsonArray enemy = new JsonArray();
                Gun.getInstance().getEnemyManager().getRegistry().forEach(enemies ->
                {
                    try {
                        JsonObject enemyObject = new JsonObject();
                        JsonObject properties = enemyObject;
                        properties.addProperty("enemy-label", enemies.getLabel());
                        properties.addProperty("enemy-alias", enemies.getAlias());
                        enemy.add(properties);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });

                try (FileWriter writer = new FileWriter(getFile()))
                {
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(enemy));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    public Enemy getEnemyByAliasOrLabel(String aliasOrLabel)
    {
        for (Enemy enemy : registry)
        {
            if (aliasOrLabel.equalsIgnoreCase(enemy.getLabel()) || aliasOrLabel.equalsIgnoreCase(enemy.getAlias()))
            {
                return enemy;
            }
        }

        return null;
    }

    public boolean isEnemy(String aliasOrLabel)
    {
        for (Enemy enemy : registry)
        {
            if (aliasOrLabel.equalsIgnoreCase(enemy.getLabel()) || aliasOrLabel.equalsIgnoreCase(enemy.getAlias()))
            {
                return true;
            }
        }

        return false;
    }
}
