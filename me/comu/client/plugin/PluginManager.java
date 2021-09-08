package me.comu.client.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.comu.client.core.Gun;

public class PluginManager extends ListManager<Plugin> implements PluginManagerImpl
{
    @Override
    public Optional<Plugin> get(String name)
    {
        for (Plugin plugin : getList())
        {
            if (plugin.getName().equalsIgnoreCase(name))
            {
                return Optional.of(plugin);
            }
        }

        return Optional.empty();
    }

    @Override
    public File getFile()
    {
        return new File(Gun.getInstance().getDirectory(), "plugins");
    }

    @Override
    public void onLoad() throws IOException
    {
        if (!getFile().exists())
        {
            getFile().mkdirs();
            getFile().mkdir();
        }

        File[] files = getFile().listFiles();

        if (files.length > 0) for (File file : files)
                if (file.isFile()) try
                    {
                        JarFile jarFile = new JarFile(file);
                        Enumeration<JarEntry> entries = jarFile.entries();

                        while (entries.hasMoreElements())
                        {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();

                            if (name.endsWith(".class"))
                            {
                                String className = name.replaceAll("/", ".");
                                className = className.substring(0, className.length() - 6);
                                ClassLoader classLoader = new URLClassLoader(new URL[] {new URL("file:///" + file.getAbsolutePath())});
                                Class<?> clazz = classLoader.loadClass(className);

                                if (clazz != null && clazz.getSuperclass().equals(Plugin.class))
                                {
                                    getList().add((Plugin) clazz.newInstance());
                                }
                            }
                        }
                    }
                    catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
    }

    @Override
    public boolean needsUpdate()
    {
        return false;
    }
}
