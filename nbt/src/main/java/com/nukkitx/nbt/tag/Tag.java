package com.nukkitx.nbt.tag;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor
public abstract class Tag<T> implements Comparable<Tag<T>> {
    private final String name;

    public abstract T getValue();

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@Nonnull Tag other) {
        if (equals(other)) {
            return 0;
        } else {
            if (other.getName().equals(getName())) {
                throw new IllegalStateException("Cannot compare two Tags with the same name but different values for sorting");
            } else {
                return getName().compareTo(other.getName());
            }
        }
    }

    @Override
    public String toString() {
        String append = ": ";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")" + append;
        }
        return append;
    }
}
