package me.comu.api.interfaces;


public interface Toggleable
{
    boolean isRunning();

    void setRunning(boolean running);

    void toggle();
}
