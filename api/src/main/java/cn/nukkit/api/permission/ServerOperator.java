/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.permission;

/**
 * 能成为服务器管理员(OP)的对象。<br>
 * Who can be an operator(OP).
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.api.permission.Permissible
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface ServerOperator {

    /**
     * 返回这个对象是不是服务器管理员。<br>
     * Returns if this object is an operator.
     *
     * @return 这个对象是不是服务器管理员。<br>if this object is an operator.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isOp();

    /**
     * 把这个对象设置成服务器管理员。<br>
     * Sets this object to be an operator or not to be.
     *
     * @param value {@code true}为授予管理员，{@code false}为取消管理员。<br>
     *              {@code true} for giving this operator or {@code false} for cancelling.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setOp(boolean value);
}
