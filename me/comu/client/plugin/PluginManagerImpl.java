package me.comu.client.plugin;

import java.io.File;
import java.io.IOException;

public interface PluginManagerImpl<T>
{
    File getFile();

    void onLoad() throws IOException;

    boolean needsUpdate();

default boolean needsUpdate(T type)
    {
        return needsUpdate();
    }

default void onLoad(T type) throws IOException
        {
            onLoad();
        }
}
