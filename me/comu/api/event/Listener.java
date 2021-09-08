package me.comu.api.event;

import me.comu.api.event.filter.Filter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Listener<E extends Event>
{
    private final String identifier;
    private Class<E> event;

    private final List<Filter> filters = new CopyOnWriteArrayList<>();

    public Listener(String identifier)
    {
        this.identifier = identifier;
        Type generic = getClass().getGenericSuperclass();

        if (generic instanceof ParameterizedType)
        {
            for (Type type : ((ParameterizedType) generic).getActualTypeArguments())
            {
                if (type instanceof Class && Event.class.isAssignableFrom((Class<?>) type))
                {
                    event = (Class<E>) type;
                    break;
                }
            }
        }
    }

    public Class<E> getEvent()
    {
        return event;
    }

    public final String getIdentifier()
    {
        return identifier;
    }

    public final List<Filter> getFilters()
    {
        return filters;
    }

    public void addFilters(Filter... filters)
    {
        for (Filter filter : filters)
        {
            this.filters.add(filter);
        }
    }

    public abstract void call(E event);
}
