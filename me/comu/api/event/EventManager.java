package me.comu.api.event;

import java.util.List;


public interface EventManager
{
    void register(Listener listener);

    void unregister(Listener listener);

    void clear();

    void dispatch(Event event);

    List<Listener> getListeners();
}
