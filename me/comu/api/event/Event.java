package me.comu.api.event;

public class Event
{
    private boolean canceled = false;

    public boolean isCancelled()
    {
        return canceled;
    }

    public void setCanceled(boolean canceled)
    {
        this.canceled = canceled;
    }
}
