package cn.nukkit.utils;

import com.google.common.collect.ImmutableList;
import lombok.ToString;

import java.util.List;

@ToString
public class PersonaPieceTint {
    public final String pieceType;
    public final ImmutableList<String> colors;

    public PersonaPieceTint(String pieceType, List<String> colors) {
        this.pieceType = pieceType;
        this.colors = ImmutableList.copyOf(colors);
    }
}
