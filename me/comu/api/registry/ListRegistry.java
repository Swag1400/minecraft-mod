package me.comu.api.registry;

import java.util.List;


public class ListRegistry<T>
{
    protected List<T> registry;

    public void register(T element)
    {
        registry.add(element);

    }

    public void unregister(T element)
    {
        registry.remove(element);
    }

    public void clear()
    {
        registry.clear();
    }

    public List<T> getRegistry()
    {
        return registry;
    }
}
