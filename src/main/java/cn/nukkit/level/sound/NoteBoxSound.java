package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public class NoteBoxSound extends GenericSound {

    private final int instrument;
    private final int pitch;

    public static final int INSTRUMENT_PIANO = 0;
    public static final int INSTRUMENT_BASS_DRUM = 1;
    public static final int INSTRUMENT_CLICK = 2;
    public static final int INSTRUMENT_TABOUR = 3;
    public static final int INSTRUMENT_BASS = 4;

    public NoteBoxSound(Vector3 pos) {
        this(pos, 0);
    }

    public NoteBoxSound(Vector3 pos, int instrument) {
        this(pos, instrument, 0);
    }

    public NoteBoxSound(Vector3 pos, int instrument, int pitch) {
        super(pos, instrument, pitch);
        this.instrument = instrument;
        this.pitch = pitch;
    }

    @Override
    public DataPacket[] encode() {
        BlockEventPacket pk = new BlockEventPacket();
        pk.x = (int) this.x;
        pk.y = (int) this.y;
        pk.z = (int) this.z;
        pk.case1 = this.instrument;
        pk.case2 = this.pitch;

        return new DataPacket[]{pk};
    }
}
