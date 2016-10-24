package cn.nukkit.service;

public interface PermissionService_v1 extends PermissionService {
    @Override
    default public int getVersion() {
        return 1;
    }
}
