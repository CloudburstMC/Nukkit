package cn.nukkit.api.metadata.blockentity;

public interface SignBlockEntity extends BlockEntity {

    String getLine(int num);

    String setLine(int num, String line);

    String[] getLines();

    boolean isEditable();

    void setEditable(boolean editable);
}
