package me.comu.client.friend;

import com.google.gson.*;

import me.comu.api.registry.ListRegistry;
import me.comu.client.config.Config;
import me.comu.client.core.Gun;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
 	
public final class FriendManager extends ListRegistry<Friend>
{
    public FriendManager()
    {
        registry = new ArrayList<>();
        new Config("friends.json")
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

                JsonArray friends = (JsonArray) root;
                friends.forEach(node ->
                {
                    if (!(node instanceof JsonObject))
                    {
                        return;
                    }
                    try {
                        JsonObject friendNode = (JsonObject) node;
                        Gun.getInstance().getFriendManager().getRegistry().add(new Friend(
                            friendNode.get("friend-label").getAsString(), friendNode.get("friend-alias").getAsString()));
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

                if (Gun.getInstance().getFriendManager().getRegistry().isEmpty())
                {
                    return;
                }

                JsonArray friends = new JsonArray();
                Gun.getInstance().getFriendManager().getRegistry().forEach(friend ->
                {
                    try {
                        JsonObject friendObject = new JsonObject();
                        JsonObject properties = friendObject;
                        properties.addProperty("friend-label", friend.getLabel());
                        properties.addProperty("friend-alias", friend.getAlias());
                        friends.add(properties);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });

                try (FileWriter writer = new FileWriter(getFile()))
                {
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(friends));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    public Friend getFriendByAliasOrLabel(String aliasOrLabel)
    {
        for (Friend friend : registry)
        {
            if (aliasOrLabel.equalsIgnoreCase(friend.getLabel()) || aliasOrLabel.equalsIgnoreCase(friend.getAlias()))
            {
                return friend;
            }
        }

        return null;
    }

    public boolean isFriend(String aliasOrLabel)
    {
        for (Friend friend : registry)
        {
            if (aliasOrLabel.equalsIgnoreCase(friend.getLabel()) || aliasOrLabel.equalsIgnoreCase(friend.getAlias()))
            {
                return true;
            }
        }

        return false;
    }
}
