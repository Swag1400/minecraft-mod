package me.comu.client.properties;


public class Property<T>
{
    private final String[] aliases;
    protected T value;

    public Property(T value, String... aliases)
    {
        this.value = value;
        this.aliases = aliases;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }
}
