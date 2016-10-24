package cn.nukkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServiceManager {
    private ConcurrentHashMap<Class<? extends Service>, ConcurrentLinkedQueue<Service>> services = new ConcurrentHashMap<>();

    public ServiceManager() {
    }

    public <T extends Service> void addService(Class<? extends Service> clazz, T service) {
        services.putIfAbsent(clazz, new ConcurrentLinkedQueue<>());
        ConcurrentLinkedQueue<Service> array = services.get(clazz);
        array.add(service);
    }

    public <T extends Service> Collection<T> getServices(Class<T> clazz) {
        ConcurrentLinkedQueue<Service> current = services.get(clazz);
        ArrayList<T> result = new ArrayList<>();
        if (current != null) {
            result.addAll((Collection<? extends T>) current);
        }
        return result;
    }

    public EconomyServiceSet getEconomyServices() {
        return new EconomyServiceSet(getServices(EconomyService.class));
    }

    public PermissionsServiceSet getPermissionServices() {
        return new PermissionsServiceSet(getServices(PermissionService.class));
    }

    public ProtectionServiceSet getProtectionServices() {
        return new ProtectionServiceSet(getServices(ProtectionService.class));
    }
}
