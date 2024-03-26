package cn.nukkit.permission;

/**
 * 能成为服务器管理员(OP)的对象。<br>
 * Who can be an operator(OP).
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.permission.Permissible
 */
public interface ServerOperator {

    /**
     * 返回这个对象是不是服务器管理员。<br>
     * Returns if this object is an operator.
     *
     * @return 这个对象是不是服务器管理员。<br>if this object is an operator.
     */
    boolean isOp();

    /**
     * 把这个对象设置成服务器管理员。<br>
     * Sets this object to be an operator or not to be.
     *
     * @param value {@code true}为授予管理员，{@code false}为取消管理员。<br>
     *              {@code true} for giving this operator or {@code false} for cancelling.
     */
    void setOp(boolean value);
}
