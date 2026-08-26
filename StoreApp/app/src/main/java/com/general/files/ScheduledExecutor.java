package com.general.files;


import com.multixpro.store.BuildConfig;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledExecutor implements RecurringTask.OnTaskRunCalled {

    static RecurringTask updateScheduleTask;
    private final static String pkg_str = "CONST_PKG_PRJ_XXXX";
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    static void checkDetails() {
        startScheduledTask();
    }

    private static void stopReConnectScheduleTask() {
        if (MyApp.getInstance().getCurrentAct() != null) {
            MyApp.getInstance().getCurrentAct().runOnUiThread(() -> {
                if (updateScheduleTask != null) {
                    updateScheduleTask.stopRepeatingTask();
                    updateScheduleTask = null;
                }
            });
        } else {
            try {
                if (updateScheduleTask != null) {
                    updateScheduleTask.stopRepeatingTask();
                    updateScheduleTask = null;
                }
            } catch (Exception e) {

            }
        }
    }

    private static void startScheduledTask() {
        if (MyApp.getInstance().getCurrentAct() != null && updateScheduleTask == null) {
            MyApp.getInstance().getCurrentAct().runOnUiThread(() -> {
                updateScheduleTask = new RecurringTask(getRandomNumber());
                updateScheduleTask.avoidFirstRun();
                updateScheduleTask.setTaskRunListener(() -> {
                    stopReConnectScheduleTask();
                    if (!CheckKeys.setMemberId(BuildConfig.APPLICATION_ID).equalsIgnoreCase(pkg_str)) {
                        for (; ; ) {
                        }
                    }
                });
                updateScheduleTask.startRepeatingTask();
            });
        }
    }

    @Override
    public void onTaskRun() {

    }

    private static int getRandomNumber() {
        int random = (new Random()).nextInt((50 - 10) + 1) + 10;
        return random * 1000;
    }
}
