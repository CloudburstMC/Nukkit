package cn.nukkit.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CallbackTask extends Task {

    protected Object owner;
    protected Method method;

    protected Object[] args = null;

    public CallbackTask(Object owner, String functionName) {
        try {
            this.method = owner.getClass().getMethod(functionName);
            this.owner = owner;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public CallbackTask(Object owner, String functionName, Object[] args) {
        try {
            Class[] argsClass = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argsClass[i] = args[i].getClass();
            }
            this.method = owner.getClass().getMethod(functionName, argsClass);
            this.owner = owner;
            this.args = args;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRun(int currentTick) {
        try {
            if (args == null) {
                this.method.invoke(owner);
            } else {
                this.method.invoke(owner, args);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getOwner() {
        return owner;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
