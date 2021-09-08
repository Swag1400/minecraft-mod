package me.comu.api.event.basic;

import me.comu.api.event.Event;
import me.comu.api.event.EventManager;
import me.comu.api.event.Listener;
import me.comu.api.event.filter.Filter;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BasicEventManager implements EventManager
{
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void register(Listener listener)
    {
        if (!has(listener) && listener != null)
        {
            listeners.add(listener);
        }
    }

    @Override
    public void unregister(Listener listener)
    {
        if (has(listener) && listener != null)
        {
            listeners.remove(listener);
        }
    }

    @Override
    public void clear()
    {
        if (!listeners.isEmpty())
        {
            listeners.clear();
        }
    }

    @Override
    public void dispatch(Event event)
    {
        listeners.forEach(listener ->
        {
            if (filter(listener, event) && listener.getEvent() == event.getClass())
            {
                if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null)
                {
                    listener.call(event);
                }
            }
        });
    }

    @Override
    public List<Listener> getListeners()
    {
        return listeners;
    }

    public Listener getListener(String identifier)
    {
        if (!listeners.isEmpty())
        {
            for (Listener listener : listeners)
            {
                if (listener.getIdentifier().equalsIgnoreCase(identifier))
                {
                    return listener;
                }
            }
        }

        return null;
    }

    private boolean filter(Listener listener, Event event)
    {
        List<Filter> filters = listener.getFilters();

        if (!filters.isEmpty())
        {
            for (Filter filter : filters)
            {
                if (!filter.filter(listener, event))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean has(Listener listener)
    {
        return listeners.contains(listener);
    }
}
