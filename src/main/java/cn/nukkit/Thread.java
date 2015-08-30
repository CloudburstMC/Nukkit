package cn.nukkit;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class Thread extends java.lang.Thread {
    @Override
    public synchronized void start() {
        super.start();
        ThreadManager.getInstance().add(this);
    }

    public synchronized void quit() {
        if (this.isAlive()) {
            this.interrupt();
        }
        ThreadManager.getInstance().remove(this);
    }

}
