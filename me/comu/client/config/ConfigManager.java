package me.comu.client.config;

import java.util.ArrayList;

import me.comu.api.registry.ListRegistry;

public final class ConfigManager extends ListRegistry<Config>
{
    public ConfigManager()
    {
        registry = new ArrayList<>();
    }
}
