package com.nukkitx.server.entity;

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
