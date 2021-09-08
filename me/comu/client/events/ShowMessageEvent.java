package me.comu.client.events;

import me.comu.api.event.Event;

public class ShowMessageEvent extends Event
{
    private String message;

    public ShowMessageEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
