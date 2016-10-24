package cn.nukkit.service;

public interface EconomyService_v1 extends EconomyService {
    @Override
    default public int getVersion() {
        return 1;
    }
}
