package net.ruxor.punishments.profiler.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Timing {
    public final String name;
    public int calls;
    public long call, total, lastCall;
    public double stdDev;
    public SimpleAverage average = new SimpleAverage(300, 0);
}
