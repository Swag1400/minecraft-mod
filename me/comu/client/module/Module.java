package me.comu.client.module;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import me.comu.api.interfaces.Labeled;
import me.comu.client.core.Gun;
import me.comu.client.presets.Preset;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Module implements Labeled
{
    private final String label;
    private String tag;
    private final String[] aliases;

    private final List<Property> properties = new ArrayList<>();
    private final List<Preset> presets = new ArrayList<>();

    protected static Minecraft minecraft = Minecraft.getMinecraft();

    protected Module(String label, String[] aliases)
    {
        this.label = tag = label;
        this.aliases = aliases;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public List<Property> getProperties()
    {
        return properties;
    }
 // Use above method to make an arraylist of all properties to make editable values on tabgui \\ Directory: me.comu.client.module.impl.toggle.render.tabgui.item.GuiTab
    protected void offerProperties(Property... properties)
    {
        for (Property property : properties)
        {
            this.properties.add(property);
        }

        this.properties.sort((p1, p2) -> p1.getAliases()[0].compareTo(p2.getAliases()[0]));
    }

    public Property getPropertyByAlias(String alias)
    {
        for (Property property : properties)
        {
            for (String propertyAlias : property.getAliases())
            {
                if (alias.equalsIgnoreCase(propertyAlias))
                {
                    return property;
                }
            }
        }

        return null;
    }

    public List<Preset> getPresets()
    {
        return presets;
    }

    protected void offsetPresets(Preset... presets)
    {
        for (Preset preset : presets)
        {
            this.presets.add(preset);
        }

        this.presets.sort((p1, p2) -> p1.getLabel().compareTo(p2.getLabel()));
    }

    public Preset getPresetByLabel(String label)
    {
        for (Preset preset : presets)
        {
            if (label.equalsIgnoreCase(preset.getLabel()))
            {
                return preset;
            }
        }

        return null;
    }

    public void loadConfig(JsonObject node)
    {
        File modsFolder = new File(Gun.getInstance().getDirectory(), "modules");

        if (!modsFolder.exists())
        {
            modsFolder.mkdir();
        }

        node.entrySet().forEach(entry ->
        {
            Optional<Property> property1 = null;

            for (Property prop : this.getProperties())
            {
                if (property1 == null)
                {
                    if (prop.getAliases()[0].equalsIgnoreCase(entry.getKey().toLowerCase()))
                    {
                        property1 = Optional.ofNullable(prop);
                    }
                }
            }
            if (property1 != null)
            {
                if (property1.isPresent())
                {
                    Object type = (entry.getValue()).getAsString();

                    if (property1.get().getValue() instanceof Number)
                    {
                        if (property1.get().getValue() instanceof Integer)
                        {
                            type = (entry.getValue()).getAsJsonPrimitive().getAsInt();
                        }
                        else if (property1.get().getValue() instanceof Long)
                        {
                            type = (entry.getValue()).getAsJsonPrimitive().getAsLong();
                        }
                        else if (property1.get().getValue() instanceof Boolean)
                        {
                            type = (entry.getValue()).getAsJsonPrimitive().getAsBoolean();
                        }
                        else if (property1.get().getValue() instanceof Double)
                        {
                            type = (entry.getValue()).getAsJsonPrimitive().getAsDouble();
                        }
                        else if (property1.get().getValue() instanceof Float)
                        {
                            type = (entry.getValue()).getAsJsonPrimitive().getAsFloat();
                        }
                    }
                    else if (property1.get().getValue() instanceof Enum)
                    {
                        type = (entry.getValue()).getAsJsonPrimitive().getAsString();
                        ((EnumProperty) property1.get()).setValue(type.toString());
                        return;
                    }
                    else if (property1.get().getValue() instanceof Boolean)
                    {
                        type = (entry.getValue()).getAsJsonPrimitive().getAsBoolean();
                    }
                    else if (property1.get().getValue() instanceof String)
                    {
                        type = (entry.getValue()).getAsJsonPrimitive().getAsString();
                    }

                    property1.get().setValue(type);
                }
            }
        });
    }

    public void saveConfig()
    {
        File modsFolder = new File(Gun.getInstance().getDirectory(), "modules");

        if (!modsFolder.exists())
        {
            modsFolder.mkdir();
        }

        if (this.getProperties().size() < 1)
        {
            return;
        }

        File jsonFile = new File(modsFolder, getLabel().toLowerCase().replace(" ", "") + ".json");

        if (jsonFile.exists())
        {
            jsonFile.delete();
        }
        else
        {
            try
            {
                jsonFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        File file = jsonFile;
        JsonObject node = new JsonObject();
        Collection<Property> settings1 = Collections.unmodifiableCollection(this.getProperties());
        settings1.forEach(setting ->
        {
            if (setting instanceof NumberProperty)
            {
                return;
            }
            node.addProperty(setting.getAliases()[0], setting.getValue().toString());
        });

        if (node.entrySet().isEmpty())
        {
            return;
        }

        try
        {
            file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            FileWriter writer = new FileWriter(file);
            Throwable throwable = null;

            try
            {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(node));
            }
            catch (Throwable var6_9)
            {
                throwable = var6_9;
                throw var6_9;
            }
            finally
            {
                if (throwable != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (Throwable var6_8)
                    {
                        throwable.addSuppressed(var6_8);
                    }
                }
                else
                {
                    writer.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            file.delete();
        }
    }
   /* private int getColor()
    {
        final double S = 0.9;
        final double B = 0.9;
        final double H = Module.colorhue;
        return Color.HSBtoRGB((float)H, (float)S, (float)B);
    }
    */
}
