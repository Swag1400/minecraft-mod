package me.comu.client.plugin;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ListManager<T> extends Manager<T>
{
    protected static final Minecraft mc = Minecraft.getMinecraft();
    private List<T> list;

    public ListManager(List<T> list)
    {
        this.list = list;
    }

    public ListManager()
    {
        this(new ArrayList<T>());
    }

    public List<T> getList()
    {
        return list;
    }

    public Optional<T> get(String name)
    {
        return Optional.empty();
    }

    public void register(T... types)
    {
        if (types.length > 0)
            for (T type : types)
                if (type != null && !this.getList().contains(type))
                {
                    this.getList().add(type);
                }
    }

    public void unregister(T... types)
    {
        if (types.length > 0)
            for (T type : types)
                if (type != null && this.getList().contains(type))
                {
                    this.getList().remove(type);
                }
    }

    public boolean has(T type)
    {
        return getList().contains(type);
    }
}
