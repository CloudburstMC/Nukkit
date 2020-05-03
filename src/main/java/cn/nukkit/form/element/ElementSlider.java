package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
@Getter
public final class ElementSlider extends Element {

    private final float min;
    private final float max;
    private final int step;
    @JsonProperty("default")
    private final float defaultValue;

    public ElementSlider(@Nonnull String elementText) {
        super(ElementType.SLIDER, elementText);
        this.min = 0f;
        this.max = 100f;
        this.step = 1;
        this.defaultValue = 0f;
    }

    public ElementSlider(@Nonnull String elementText, float min, float max) {
        super(ElementType.SLIDER, elementText);
        if (min >= max) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.min = min;
        this.max = max;
        this.step = 1;
        this.defaultValue = min;
    }

    public ElementSlider(@Nonnull String elementText, float min, float max, int step) {
        super(ElementType.SLIDER, elementText);
        if (min >= max) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = min;
    }

    public ElementSlider(@Nonnull String elementText, float min, float max, int step, float defaultValue) {
        super(ElementType.SLIDER, elementText);
        if (min >= max) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = defaultValue;
    }
}
