package me.comu.client.module;

public enum ModuleType
{
    COMBAT("Combat"), EXPLOITS("Exploits"), MISCELLANEOUS("Miscellaneous"), MOVEMENT("Movement"), RENDER("Render"), WORLD("World");

    private final String label;

    ModuleType(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }
}
