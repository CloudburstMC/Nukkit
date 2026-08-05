package cn.nukkit.utils;

import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Persona skin piece tint
 */
@Data
public class PersonaPieceTint {

    PersonaPieceType pieceType;
    /**
     * @deprecated since v2168, use colorsNew
     */
    List<String> colors;
    /**
     * @since v2168
     */
    List<Color> colorsNew;

    public PersonaPieceTint(String type, List<String> colors) {
        this.pieceType = PersonaPieceType.fromName(type);
        this.colors = colors;
    }

    public PersonaPieceTint(PersonaPieceType type, List<Color> colorsNew) {
        this.pieceType = type;
        this.colorsNew = colorsNew;
    }

    public List<String> getColors() {
        if ((colors == null || colors.isEmpty()) && colorsNew != null && !colorsNew.isEmpty()) {
            colors = new ArrayList<>(colorsNew.size());
            for (Color c : colorsNew) {
                if (c.getAlpha() == 0) {
                    colors.add("#0");
                } else {
                    colors.add(String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
                }
            }
        }
        return colors;
    }

    public List<Color> getColorsNew() {
        if ((colorsNew == null || colorsNew.isEmpty()) && colors != null && !colors.isEmpty()) {
            colorsNew = new ArrayList<>(colors.size());
            for (String s : colors) {
                if (s.equals("#0")) {
                    colorsNew.add(new Color(0, true));
                } else {
                    colorsNew.add(new Color((int) Long.parseLong(s.startsWith("#") ? s.substring(1) : s, 16), true));
                }
            }
        }
        return colorsNew;
    }
}
