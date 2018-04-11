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

package cn.nukkit.server.entity;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import javax.annotation.Nullable;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.ASM5;

public class EntityClassVisitor extends ClassVisitor {
    private static final String ENTITIES_API_PACKAGE = "cn/nukkit/api/entity/";
    private String entityInterface;
    private boolean visible;

    public EntityClassVisitor() {
        super(ASM5);
    }

    @Override
    public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
        for (String s : var6) {
            if (s.startsWith(ENTITIES_API_PACKAGE)) {
                this.entityInterface = s.replaceAll("/", ".");
            }
        }
    }

    @Override
    @Nullable
    public AnnotationVisitor visitAnnotation(String name, boolean visible) {
        this.visible = visible;
        return null;
    }

    public Optional<String> getEntityClass() {
        return !this.visible ? Optional.empty() : Optional.ofNullable(this.entityInterface);
    }

}
