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
public final class ElementDropdown extends Element {

    @JsonProperty("options")
    private final List<String> dropdownOptions = new ArrayList<>();
    private int defaultOptionIndex = 0;

    public ElementDropdown(@Nonnull String elementText) {
        super(ElementType.DROPDOWN, elementText);
    }

    public ElementDropdown(@Nonnull String elementText, @Nonnull List<String> dropdownOptions) {
        super(ElementType.DROPDOWN, elementText);

        Preconditions.checkNotNull(dropdownOptions, "The provided dropdown options can not be null");
        this.dropdownOptions.addAll(dropdownOptions);
    }

    public ElementDropdown(@Nonnull String elementText, @Nonnull List<String> dropdownOptions, int defaultOptionIndex) {
        super(ElementType.DROPDOWN, elementText);

        Preconditions.checkNotNull(dropdownOptions, "The provided dropdown options can not be null");
        Preconditions.checkElementIndex(defaultOptionIndex, dropdownOptions.size(), "Default option index");

        this.dropdownOptions.addAll(dropdownOptions);
        this.defaultOptionIndex = defaultOptionIndex;
    }

    @Nonnull
    public List<String> getDropdownOptions() {
        return this.dropdownOptions;
    }

    public String getDropdownOption(int index) {
        return this.dropdownOptions.get(index);
    }

    @Nonnull
    public ElementDropdown addOptions(@Nonnull List<String> dropdownOptions) {
        Preconditions.checkNotNull(dropdownOptions, "The provided dropdown options can not be null");

        this.dropdownOptions.addAll(dropdownOptions);
        return this;
    }

    @Nonnull
    public ElementDropdown addOption(@Nonnull String dropdownOption) {
        Preconditions.checkNotNull(dropdownOption, "The provided dropdown option can not be null");

        this.dropdownOptions.add(dropdownOption);
        return this;
    }

    public int getDefaultOptionIndex() {
        return this.defaultOptionIndex;
    }

    @Nonnull
    public ElementDropdown defaultIndex(int defaultOptionIndex) {
        Preconditions.checkElementIndex(defaultOptionIndex, dropdownOptions.size(), "Default option index");

        this.defaultOptionIndex = defaultOptionIndex;
        return this;
    }

    @Nonnull
    public ElementDropdown defaultIndex(@Nonnull String dropdownOption) {
        Preconditions.checkNotNull(dropdownOption, "The provided dropdown option can not be null");

        if (this.dropdownOptions.contains(dropdownOption)) {
            this.defaultOptionIndex = this.dropdownOptions.indexOf(dropdownOption);
        } else {
            this.dropdownOptions.add(dropdownOption);
            this.defaultOptionIndex = this.dropdownOptions.size() - 1;
        }
        return this;
    }

    @RequiredArgsConstructor
    @Getter
    public static class Response {

        private final int index;
        private final String option;
    }
}