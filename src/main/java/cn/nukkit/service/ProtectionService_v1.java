package cn.nukkit.service;

public interface ProtectionService_v1 extends ProtectionService {
    @Override
    default public int getVersion() {
        return 1;
    }
}
