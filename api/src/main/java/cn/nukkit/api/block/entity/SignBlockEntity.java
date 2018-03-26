package cn.nukkit.api.block.entity;

public interface SignBlockEntity extends BlockEntity {

    String getLine(int num);

    String setLine(int num, String line);

    String[] getLines();
}
