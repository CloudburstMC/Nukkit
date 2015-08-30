package cn.nukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ThreadManager {

    private List<Thread> list;

    private static ThreadManager instance = null;

    public ThreadManager() {
        this.list = Collections.synchronizedList(new ArrayList<>());
    }

    public static void init() {
        instance = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return instance;
    }

    public void add(Thread thread) {
        this.list.add(thread);
    }

    public void remove(Thread thread) {
        this.list.remove(thread);
    }

    public List<Thread> getAll() {
        List<Thread> list = new ArrayList<>();
        for (Thread thread : this.list) {
            list.add(thread);
        }
        return list;
    }
}
