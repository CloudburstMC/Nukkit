package cn.nukkit.api.block.entity;

public interface RedstoneComparatorBlockEntity extends BlockEntity {

    int getOutputSignal();

    void setOutputSignal(int outputSignal);
}
