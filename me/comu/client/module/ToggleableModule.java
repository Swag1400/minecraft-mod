package me.comu.client.module;

import me.comu.api.event.Listener;
import me.comu.api.interfaces.Toggleable;
import me.comu.client.core.Gun;
import me.comu.client.keybind.Keybind;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ToggleableModule extends Module implements Toggleable
{
    private boolean running;
    private boolean drawn;
    private final int color;
    private final ModuleType moduleType;

    protected final List<Listener<?>> listeners = new ArrayList<>();

    private ToggleableModule(String label, String[] aliases, boolean drawn, int color, ModuleType moduleType)
    {
        super(label, aliases);
        this.drawn = drawn;
        this.color = color;
        this.moduleType = moduleType;
        Gun.getInstance().getKeybindManager().register(new Keybind(label, Keyboard.KEY_NONE)
        {
            @Override
            public void onPressed()
            {
                toggle();
            }
        });
    }

    protected ToggleableModule(String label, String[] aliases, int color, ModuleType moduleType)
    {
        this(label, aliases, true, color, moduleType);
    }

    protected ToggleableModule(String label, String[] aliases, ModuleType moduleType)
    {
        this(label, aliases, false, 0, moduleType);
    }

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void setRunning(boolean running)
    {
        this.running = running;

        if (isRunning())
        {
            onEnable();
        }
        else
        {
            onDisable();
        }
    }

    @Override
    public void toggle()
    {
        setRunning(!running);
    }

    public boolean isDrawn()
    {
        return drawn;
    }

    public void setDrawn(boolean drawn)
    {
        this.drawn = drawn;
    }

    public int getColor()
    {
        return color;
    }

    public ModuleType getModuleType()
    {
        return moduleType;
    }

    protected void onEnable()
    {

        listeners.forEach(listener -> Gun.getInstance().getEventManager().register(listener));
    }

    protected void onDisable()
    {
        listeners.forEach(listener -> Gun.getInstance().getEventManager().unregister(listener));
    }
}
