package me.comu.client.properties;

import java.text.DecimalFormat;

public class NumberProperty<T extends Number> extends Property<T>
{
    private final T minimum, maximum, increment;
    private boolean clamp;

    public NumberProperty(T value, T minimum, T maximum, T increment, String... aliases)
    {
        super(value, aliases);
        clamp = true;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public NumberProperty(T value, String... aliases)
    {
        super(value, aliases);
        clamp = false;
        this.minimum = maximum = increment = null;
    }

    public T getMaximum()
    {
        return maximum;
    }

    public T getMinimum()
    {
        return minimum;
    }

    public T getIncrement() {
        return increment;
    }

    public double getDoubleValue()
    {
        if (value instanceof Integer)
        {
            return value.intValue() * 1.0D;
        }
        else if (value instanceof Float)
        {
            return value.floatValue() * 1.0D;
        }
        else if (value instanceof Double)
        {
            return value.doubleValue();
        }
        else if (value instanceof Long)
        {
           return (double) value.longValue();
        }
        else if (value instanceof Short)
        {
            return value.shortValue();
        }
        else if (value instanceof Byte)
        {
           return (double) value.byteValue();
        }
        return -1;
    }

    public double getDoubleMinimum()
    {
        if (maximum instanceof Integer)
        {
            return maximum.intValue() * 1.0D;
        }
        else if (maximum instanceof Float)
        {
            return maximum.floatValue() * 1.0D;
        }
        else if (maximum instanceof Double)
        {
            return maximum.doubleValue();
        }
        else if (maximum instanceof Long)
        {
            return (double) maximum.longValue();
        }
        else if (maximum instanceof Short)
        {
            return maximum.shortValue();
        }
        else if (maximum instanceof Byte)
        {
            return (double) maximum.byteValue();
        }
        return -1;
    }

    public double getDoubleMaximum()
    {
        if (minimum instanceof Integer)
        {
            return minimum.intValue() * 1.0D;
        }
        else if (minimum instanceof Float)
        {
            return minimum.floatValue() * 1.0D;
        }
        else if (minimum instanceof Double)
        {
            return minimum.doubleValue();
        }
        else if (minimum instanceof Long)
        {
            return (double) minimum.longValue();
        }
        else if (minimum instanceof Short)
        {
            return minimum.shortValue();
        }
        else if (minimum instanceof Byte)
        {
            return (double) minimum.byteValue();
        }
        return -1;
    }


    @Override
    public void setValue(T value)
    {
        if (clamp)
        {
            if (value instanceof Integer)
            {
                if (value.intValue() > maximum.intValue())
                {
                    value = maximum;
                }
                else if (value.intValue() < minimum.intValue())
                {
                    value = minimum;
                }
            }
            else if (value instanceof Float)
            {
                if (value.floatValue() > maximum.floatValue())
                {
                    value = maximum;
                }
                else if (value.floatValue() < minimum.floatValue())
                {
                    value = minimum;
                }
            }
            else if (value instanceof Double)
            {
                if (value.doubleValue() > maximum.doubleValue())
                {
                    value = maximum;
                }
                else if (value.doubleValue() < minimum.doubleValue())
                {
                    value = minimum;
                }
            }
            else if (value instanceof Long)
            {
                if (value.longValue() > maximum.longValue())
                {
                    value = maximum;
                }
                else if (value.longValue() < minimum.longValue())
                {
                    value = minimum;
                }
            }
            else if (value instanceof Short)
            {
                if (value.shortValue() > maximum.shortValue())
                {
                    value = maximum;
                }
                else if (value.shortValue() < minimum.shortValue())
                {
                    value = minimum;
                }
            }
            else if (value instanceof Byte)
            {
                if (value.byteValue() > maximum.byteValue())
                {
                    value = maximum;
                }
                else if (value.byteValue() < minimum.byteValue())
                {
                    value = minimum;
                }
            }
        }

        super.setValue(value);
    }

    public void increment()
    {
        if (clamp)
        {
            if (value instanceof Integer)
            {
                if (value.intValue() > maximum.intValue())
                {
                    setValue(maximum);
                }
                else if (value.intValue() < minimum.intValue())
                {
                    setValue(minimum);
                } else {
                    Object incrementedValue = value.intValue()+increment.intValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Float)
            {
                if (value.floatValue() > maximum.floatValue())
                {
                    setValue(maximum);
                }
                else if (value.floatValue() < minimum.floatValue())
                {
                    setValue(minimum);
                } else {
                    Object incrementedValue = value.floatValue()+increment.floatValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Double)
            {
                if (value.doubleValue() > maximum.doubleValue())
                {
                    setValue(maximum);
                }
                else if (value.doubleValue() < minimum.doubleValue())
                {
                    setValue(minimum);
                }else {
                    Object incrementedValue = value.doubleValue()+increment.doubleValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Long)
            {
                if (value.longValue() > maximum.longValue())
                {
                    setValue(maximum);
                }
                else if (value.longValue() < minimum.longValue())
                {
                    setValue(minimum);

                }else {
                    Object incrementedValue = value.longValue()+increment.longValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Short)
            {
                if (value.shortValue() > maximum.shortValue())
                {
                    setValue(maximum);
                }
                else if (value.shortValue() < minimum.shortValue())
                {
                    setValue(minimum);

                }else {
                    Object incrementedValue = value.shortValue()+increment.shortValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Byte)
            {
                if (value.byteValue() > maximum.byteValue())
                {
                    setValue(maximum);
                }
                else if (value.byteValue() < minimum.byteValue())
                {
                    setValue(minimum);
                }else {
                    Object incrementedValue = value.byteValue()+increment.byteValue();
                    setValue((T) incrementedValue);
                }
            }
        }
    }

    public void decrement()
    {
        {
            if (value instanceof Integer)
            {
                if (value.intValue() > maximum.intValue())
                {
                    setValue(maximum);
                }
                else if (value.intValue() < minimum.intValue())
                {
                    setValue(minimum);
                } else {
                    Object incrementedValue = value.intValue()-increment.intValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Float)
            {
                if (value.floatValue() > maximum.floatValue())
                {
                    setValue(maximum);
                }
                else if (value.floatValue() < minimum.floatValue())
                {
                    setValue(minimum);
                } else {
                    Object incrementedValue = value.floatValue()-increment.floatValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Double)
            {
                if (value.doubleValue() > maximum.doubleValue())
                {
                    setValue(maximum);
                }
                else if (value.doubleValue() < minimum.doubleValue())
                {
                    setValue(minimum);
                }else {
                    Object incrementedValue = value.doubleValue()-increment.doubleValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Long)
            {
                if (value.longValue() > maximum.longValue())
                {
                    setValue(maximum);
                }
                else if (value.longValue() < minimum.longValue())
                {
                    setValue(minimum);

                }else {
                    Object incrementedValue = value.longValue()-increment.longValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Short)
            {
                if (value.shortValue() > maximum.shortValue())
                {
                    setValue(maximum);
                }
                else if (value.shortValue() < minimum.shortValue())
                {
                    setValue(minimum);

                }else {
                    Object incrementedValue = value.shortValue()-increment.shortValue();
                    setValue((T) incrementedValue);
                }
            }
            else if (value instanceof Byte)
            {
                if (value.byteValue() > maximum.byteValue())
                {
                    setValue(maximum);
                }
                else if (value.byteValue() < minimum.byteValue())
                {
                    setValue(minimum);
                }else {
                    Object incrementedValue = value.byteValue()-increment.byteValue();
                    setValue((T) incrementedValue);
                }
            }
        }
    }
}
