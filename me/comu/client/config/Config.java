package me.comu.client.config;

import java.io.File;

import me.comu.api.interfaces.Labeled;
import me.comu.client.core.Gun;


public abstract class Config implements Labeled
{
    private final String label;
    private final File file, directory;

    public Config(String label)
    {
        this.label = label;
        this.directory = Gun.getInstance().getDirectory();
        this.file = new File(directory, label);
        Gun.getInstance().getConfigManager().register(this);
    }

    public Config(String label, File directory)
    {
        this.label = label;
        this.directory = directory;
        this.file = new File(directory, label);
        Gun.getInstance().getConfigManager().register(this);
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    public File getDirectory()
    {
        return directory;
    }

    public File getFile()
    {
        return file;
    }

    public abstract void load(Object... source);

    public abstract void save(Object... destination);
}
