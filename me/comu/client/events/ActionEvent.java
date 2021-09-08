package me.comu.client.events;

import org.lwjgl.input.Keyboard;


public class ActionEvent extends Event {

    private int key;
    private final Type type;

    public ActionEvent(Type type) {
        key = Keyboard.getEventKey();
        this.type = type;
    }

    public final Type getType() {
        return type;
    }

    public int getKey() {
        return key;
    }

    public enum Type {
        KEY_PRESS, LEFT_CLICK, RIGHT_CLICK, MIDDLE_CLICK
    }

}