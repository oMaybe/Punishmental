package net.ruxor.punishments.profiler;

import net.ruxor.punishments.util.TimeUtils;

import java.util.HashMap;

public class NewProfiler {

    public HashMap<String, HashMap<Long, Long>> timings = new HashMap<>();

    public NewProfiler(){

    }

    public void start(){
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        start(stack.getMethodName());

    }

    public void start(String name){
        timings.put(name, new HashMap<>());
        timings.get(name).put(System.currentTimeMillis(), -1L);
    }

    public void stop(){
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        stop(stack.getMethodName());
    }

    public void stop(String name){
        timings.get(name).put(timings.get(name).keySet().stream().findFirst().get(), System.currentTimeMillis());
    }

    public long difference(String name){
        return timings.get(name).values().stream().findFirst().get() - timings.get(name).keySet().stream().findFirst().get();
    }

    public String differenceTime(String name){
        return TimeUtils.formatTimeMillis(difference(name));
    }

    public void reset(){
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        reset(stack.getMethodName());
    }

    public void reset(String name){
        start(name);
    }

}
