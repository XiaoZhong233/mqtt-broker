package cn.sino.broker.proto;

import java.util.concurrent.*;


public class TimerData {

    public static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

    public static ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>();

    public static String getKey(String contextId, String packageId){
        return contextId.concat("-").concat(packageId);
    }
}
