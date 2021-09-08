package me.comu.client.staff;

import com.google.gson.*;

import me.comu.api.registry.ListRegistry;
import me.comu.client.config.Config;
import me.comu.client.core.Gun;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class StaffManager extends ListRegistry<Staff>
{
    public StaffManager()
    {
        registry = new ArrayList<>();
        new Config("staff.json")
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

                JsonArray staff = (JsonArray) root;
                staff.forEach(node ->
                {
                    if (!(node instanceof JsonObject))
                    {
                        return;
                    }
                    try {
                        JsonObject staffNode = (JsonObject) node;
                        Gun.getInstance().getStaffManager().getRegistry().add(new Staff(
                                staffNode.get("staff-label").getAsString(), staffNode.get("staff-alias").getAsString()));
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

                if (Gun.getInstance().getStaffManager().getRegistry().isEmpty())
                {
                    return;
                }

                JsonArray staff = new JsonArray();
                Gun.getInstance().getStaffManager().getRegistry().forEach(staffs ->
                {
                    try {
                        JsonObject staffObject = new JsonObject();
                        JsonObject properties = staffObject;
                        properties.addProperty("staff-label", staffs.getLabel());
                        properties.addProperty("staff-alias", staffs.getAlias());
                        staff.add(properties);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });

                try (FileWriter writer = new FileWriter(getFile()))
                {
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(staff));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    public Staff getStaffByAliasOrLabel(String aliasOrLabel)
    {
        for (Staff staff : registry)
        {
            if (aliasOrLabel.equalsIgnoreCase(staff.getLabel()) || aliasOrLabel.equalsIgnoreCase(staff.getAlias()))
            {
                return staff;
            }
        }

        return null;
    }

    public boolean isStaff(String aliasOrLabel)
    {
        for (Staff staff : registry)
        {
            if (aliasOrLabel.equalsIgnoreCase(staff.getLabel()) || aliasOrLabel.equalsIgnoreCase(staff.getAlias()))
            {
                return true;
            }
        }

        return false;
    }
}
