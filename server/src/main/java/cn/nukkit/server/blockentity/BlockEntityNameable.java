package cn.nukkit.server.blockentity;

/**
 * 表达一个能被命名的事物的接口。<br>
 * An interface describes an object that can be named.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface BlockEntityNameable {

    /**
     * 返回这个事物的名字。<br>
     * Gets the name of this object.
     *
     * @return 这个事物的名字。<br>The name of this object.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    /**
     * 设置或更改这个事物的名字。<br>
     * Changes the name of this object, or names it.
     *
     * @param name 这个事物的新名字。<br>The new name of this object.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setName(String name);

    /**
     * 返回这个事物是否有名字。<br>
     * Whether this object has a name.
     *
     * @return 如果有名字，返回 {@code true}。<br>{@code true} for this object has a name.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean hasName();
}
