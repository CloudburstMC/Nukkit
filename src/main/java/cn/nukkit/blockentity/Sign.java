package cn.nukkit.blockentity;

public interface Sign extends BlockEntity {

    String getCreator();

    void setCreator(String creator);

    String[] getText();

    void setText(String... lines);
}
