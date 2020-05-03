package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public final class ElementDropdown extends Element {

    private final List<String> options = new ArrayList<>();
    @JsonProperty("default")
    private int defaultOptionIndex = 0;

    public ElementDropdown(@Nonnull String elementText) {
        super(ElementType.DROPDOWN, elementText);
    }

    public ElementDropdown(@Nonnull String elementText, @Nonnull List<String> options) {
        super(ElementType.DROPDOWN, elementText);

        Preconditions.checkNotNull(options, "The provided dropdown options can not be null");
        this.options.addAll(options);
    }

    public ElementDropdown(@Nonnull String elementText, @Nonnull List<String> options, int defaultOptionIndex) {
        super(ElementType.DROPDOWN, elementText);

        Preconditions.checkNotNull(options, "The provided dropdown options can not be null");
        Preconditions.checkElementIndex(defaultOptionIndex, options.size(), "Default option index");

        this.options.addAll(options);
        this.defaultOptionIndex = defaultOptionIndex;
    }

    public String getDropdownOption(int index) {
        return this.options.get(index);
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    public static class Response {

        private final int index;
        private final String option;
    }
}