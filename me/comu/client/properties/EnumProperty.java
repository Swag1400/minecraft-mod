package me.comu.client.properties;

public class EnumProperty<T extends Enum> extends Property<T>
{
    public EnumProperty(T value, String... aliases)
    {
        super(value, aliases);
    }

    public String getFixedValue()
    {
        return Character.toString(this.value.name().charAt(0)) + this.value.name().toLowerCase().replaceFirst(Character.toString(this.value.name().charAt(0)).toLowerCase(), "");
    }

    public void setValue(String value)
    {
        Enum[] array;

        for (int length = (array = ((Enum) getValue()).getClass().getEnumConstants()).length, i = 0; i < length; i++)
        {
            if (array[i].name().equalsIgnoreCase(value))
            {
                this.value = (T) array[i];
            }
        }
    }

    public void increment()
    {
        Enum[] array;

        for (int length = (array = ((Enum) getValue()).getClass().getEnumConstants()).length, i = 0; i < length; i++)
        {
            if (array[i].name().equalsIgnoreCase(getFixedValue()))
            {
                i++;

                if (i > array.length - 1)
                {
                    i = 0;
                }

                setValue(array[i].toString());
            }
        }
    }

    public void decrement()
    {
        Enum[] array;

        for (int length = (array = ((Enum) getValue()).getClass().getEnumConstants()).length, i = 0; i < length; i++)
        {
            if (array[i].name().equalsIgnoreCase(getFixedValue()))
            {
                i--;

                if (i < 0)
                {
                    i =  array.length - 1;
                }

                setValue(array[i].toString());
            }
        }
    }
}