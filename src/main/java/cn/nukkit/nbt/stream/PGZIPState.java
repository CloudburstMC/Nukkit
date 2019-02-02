package cn.nukkit.nbt.stream;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PGZIPState {
    protected final DeflaterOutputStream str;
    protected final ByteArrayOutputStream buf;
    protected final Deflater def;

    public PGZIPState(PGZIPOutputStream parent) {
        this.def = parent.newDeflater();
        this.buf = new ByteArrayOutputStream(PGZIPBlock.SIZE);
        this.str = PGZIPOutputStream.newDeflaterOutputStream(buf, def);
    }


}
