package com.ralf.tt.trial;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledJob {
    /**
     * Class for CronJob, to run DataParser every HH:15 after 60 minutes of delay
     * First method is by default
     * Second method for custom input of delay and time
     */

    public void ScheduledJob(){
        int frequencyInMinutes = 60; // default frequency is once every hour
        int delayInMinutes = 15; // default delay is 15 minutes after a full hour
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable job = () -> {
            Calendar now = Calendar.getInstance();
            int minute = now.get(Calendar.MINUTE);
            if (minute == delayInMinutes) new DataParser();
        };

        scheduler.scheduleAtFixedRate(job, 0, frequencyInMinutes, TimeUnit.MINUTES);
    }
    public void ScheduledJob(int frequencyInMinutes, int delayInMinutes ){
        // For custom set frequency and delay after full hour
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable job = () -> {
            Calendar now = Calendar.getInstance();
            int minute = now.get(Calendar.MINUTE);
            if (minute == delayInMinutes) {
                new DataParser();
            }
        };
        scheduler.scheduleAtFixedRate(job, 0, frequencyInMinutes, TimeUnit.MINUTES);
    }

}
