package com.nukkitx.server.scheduler;

import com.nukkitx.server.NukkitServer;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ForkJoinTask;

@Log4j2
public abstract class AsyncTask<V> extends ForkJoinTask<V> {

    private V result;
    private int taskId;
    private boolean finished = false;

    @Override
    public boolean exec() {
        this.result = null;
        this.run();
        this.finished = true;
        return true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public V getResult() {
        return this.result;
    }

    public void setResult(V result) {
        this.result = result;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public abstract void run();

    public void onCompletion(NukkitServer server) {

    }

    public void cleanObject() {
        this.result = null;
        this.taskId = 0;
        this.finished = false;
    }
}
