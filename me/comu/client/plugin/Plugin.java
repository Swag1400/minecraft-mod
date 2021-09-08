package me.comu.client.plugin;

public abstract class Plugin
{
    private final String name;
    private boolean running;

    public Plugin(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean state)
    {
        boolean update = this.running != state;
        this.running = state;

        if (update)
        {
            if (isRunning())
            {
                startPlugin();
            }
            else
            {
                stopPlugin();
            }
        }
    }

    public abstract void startPlugin();

    public abstract void stopPlugin();
}
