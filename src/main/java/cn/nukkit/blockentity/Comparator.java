package cn.nukkit.blockentity;

public interface Comparator extends BlockEntity {

    int getOutputSignal();

    void setOutputSignal(int outputSignal);
}
