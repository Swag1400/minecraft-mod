package me.comu.client.events;

import org.lwjgl.input.Keyboard;

import me.comu.api.event.Event;

public class InputEvent extends Event
{
    private final Type type;
    private int key;

    public InputEvent(Type type)
    {
        this.type = type;
        key = Keyboard.getEventKey();
    }

    public Type getType()
    {
        return type;
    }

    public int getKey()
    {
        return key;
    }

    public enum Type
    {
        KEYBOARD_KEY_PRESS, MOUSE_LEFT_CLICK, MOUSE_MIDDLE_CLICK, MOUSE_RIGHT_CLICK
    }
}
