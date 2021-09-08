package me.comu.api.stopwatch;


public class Stopwatch
{
    private long previousMS;

    public Stopwatch()
    {
        reset();
    }

    public boolean hasCompleted(long milliseconds)
    {
        return getCurrentMS() - previousMS >= milliseconds;
    }

    public void reset()
    {
        previousMS = getCurrentMS();
    }

    public long getPreviousMS()
    {
        return previousMS;
    }

    public long getCurrentMS()
    {
        return System.nanoTime() / 1000000;
    }
}
