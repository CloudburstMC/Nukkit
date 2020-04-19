package cn.nukkit.form.element;

import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
public final class ElementSlider extends Element {

    private float minimum;
    private float maximum;
    private int stepCount;
    private float defaultValue;

    public ElementSlider(@Nonnull String elementId, @Nonnull String elementText) {
        super(ElementType.SLIDER, elementId, elementText);
        this.minimum = 0f;
        this.maximum = 100f;
        this.stepCount = 1;
        this.defaultValue = 0f;
    }

    public ElementSlider(@Nonnull String elementId, @Nonnull String elementText, float minimum, float maximum) {
        super(ElementType.SLIDER, elementId, elementText);
        if (minimum >= maximum) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepCount = 1;
        this.defaultValue = minimum;
    }

    public ElementSlider(@Nonnull String elementId, @Nonnull String elementText, float minimum, float maximum, int stepCount) {
        super(ElementType.SLIDER, elementId, elementText);
        if (minimum >= maximum) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepCount = stepCount;
        this.defaultValue = minimum;
    }

    public ElementSlider(@Nonnull String elementId, @Nonnull String elementText, float minimum, float maximum, int stepCount, float defaultValue) {
        super(ElementType.SLIDER, elementId, elementText);
        if (minimum >= maximum) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepCount = stepCount;
        this.defaultValue = defaultValue;
    }

    public float getMinimum() {
        return this.minimum;
    }

    @Nonnull
    public ElementSlider minimum(float minimum) {
        this.minimum = minimum;
        return this;
    }

    public float getMaximum() {
        return this.maximum;
    }

    @Nonnull
    public ElementSlider maximum(float maximum) {
        this.maximum = maximum;
        return this;
    }

    public int getStepCount() {
        return this.stepCount;
    }

    @Nonnull
    public ElementSlider stepCount(int stepCount) {
        this.stepCount = stepCount;
        return this;
    }

    public float getDefaultValue() {
        return this.defaultValue;
    }

    @Nonnull
    public ElementSlider defaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
