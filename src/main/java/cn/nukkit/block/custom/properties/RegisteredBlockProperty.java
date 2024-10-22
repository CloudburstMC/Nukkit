package cn.nukkit.block.custom.properties;

import lombok.Value;

import java.math.BigInteger;

@Value
public class RegisteredBlockProperty {
    BlockProperty<?> property;
    int offset;

    public void validateMeta(int meta) {
        this.property.validateMeta(meta, this.offset);
    }

    public void validateMeta(long meta) {
        this.property.validateMeta(meta, this.offset);
    }

    public void validateMeta(BigInteger meta) {
        this.property.validateMeta(meta, this.offset);
    }

    @Override
    public String toString() {
        return this.offset + "-" + (this.offset + this.property.getBitSize()) + ":" + this.property.getName();
    }
}
