package cn.nukkit.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CallbackTask extends Task {

    protected Class owner;
    protected Method method;

    protected Object[] args = null;

    public CallbackTask(Class<?> owner, String functionName) {
        try {
            this.method = owner.getMethod(functionName);
            this.owner = owner;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public CallbackTask(Class<?> owner, String functionName, Object[] args) {
        try {
            Class[] argsClass = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argsClass[i] = args[i].getClass();
            }
            this.method = owner.getMethod(functionName, argsClass);
            this.owner = owner;
            this.args = args;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public CallbackTask(Object owner, String functionName) {
        this(owner.getClass(), functionName);
    }

    public CallbackTask(Object owner, String functionName, Object[] args) {
        this(owner.getClass(), functionName, args);
    }

    @Override
    public void onRun(long currentTick) {
        try {
            if (args == null) {
                this.method.invoke(owner);
            } else {
                this.method.invoke(owner, args);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Class getOwner() {
        return owner;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
