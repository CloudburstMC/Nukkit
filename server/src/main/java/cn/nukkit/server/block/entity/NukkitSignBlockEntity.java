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

package cn.nukkit.server.block.entity;

import cn.nukkit.api.block.entity.SignBlockEntity;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class NukkitSignBlockEntity implements SignBlockEntity {
    private final String[] lines;

    public NukkitSignBlockEntity(String[] lines) {
        this.lines = lines;
    }

    @Override
    public String getLine(int num) {
        checkLineNumber(num);
        return lines[num];
    }

    @Override
    public String setLine(int num, String line) {
        checkLineNumber(num);
        return lines[num] = line;
    }

    @Override
    public String[] getLines() {
        return Arrays.copyOf(lines, 4);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {

    }

    public static void checkLineNumber(int num) {
        Preconditions.checkArgument(num >= 0 && num < 4, "Line number must be between 0 and 3");
    }
}
