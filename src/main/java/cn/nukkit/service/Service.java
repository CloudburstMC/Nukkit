package cn.nukkit.service;

import cn.nukkit.plugin.Plugin;

public interface Service {
    public Plugin getPlugin();

    public boolean isEnabled();

    public String getName();

    /**
     * When adding a new method
     *  - All old interfaces will be kept
     *  - A default stub method is added to the v1 service
     *  - The stub will return the a not implemented value (false, UnsupportedOperationException etc.)
     *  - A new interface is created for the next version extending the previous
     *  - The new interface will declare the new method but not implement it
     *  - Update the ServiceSet
     */
    public int getVersion();

    default public void register(ServiceManager manager) {
        Class<? extends Service> clazz = getClass();
        while (true) {
            Class parent = clazz.getSuperclass();
            if (parent == Service.class) {
                manager.addService(clazz, this);
                return;
            }
            clazz = parent;
        }
    }
}
