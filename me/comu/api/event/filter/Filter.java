package me.comu.api.event.filter;

import me.comu.api.event.Event;
import me.comu.api.event.Listener;

public interface Filter
{
    boolean filter(Listener listener, Event event);
}
