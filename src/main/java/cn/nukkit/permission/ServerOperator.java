package cn.nukkit.permission;

/**
 * Who can be an operator (OP).
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @see cn.nukkit.permission.Permissible
 */
public interface ServerOperator {

    /**
     * Returns if this object is an operator.
     *
     * @return if this object is an operator.
     */
    boolean isOp();

    /**
     * Sets this object to be an operator or not to be.
     *
     * @param value {@code true} for giving this operator or {@code false} for cancelling.
     */
    void setOp(boolean value);
}
