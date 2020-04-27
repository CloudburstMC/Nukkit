package cn.nukkit.form;

import cn.nukkit.form.element.*;
import cn.nukkit.form.response.CustomFormResponse;
import cn.nukkit.form.util.FormType;
import cn.nukkit.form.util.ImageData;
import cn.nukkit.player.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.*;

@Getter
public class CustomForm extends Form<CustomFormResponse> {

    @JsonProperty("content")
    private final List<Element> elements;

    private final ImageData imageData;

    public CustomForm(String title, ImageData imageData, List<Element> elements) {
        super(FormType.CUSTOM, title);
        this.imageData = imageData;
        this.elements = elements;
    }

    public Element getElement(int index) {
        return elements.get(index);
    }

    @Override
    public void handleResponse(Player p, JsonNode node) {
        if (!node.isArray()) {
            error(p);
            log.warn("Received invalid response for CustomForm {}", node);
            return;
        }

        submit(p, new CustomFormResponse(this, node));
    }

    public static class CustomFormBuilder extends FormBuilder<CustomForm, CustomFormBuilder, CustomFormResponse> {

        private final List<Element> elements = new ArrayList<>();

        private ImageData imageData = null;

        public CustomFormBuilder dropdown(@Nonnull String text, @Nonnull List<String> options) {
            return element(new ElementDropdown(text, options));
        }

        public CustomFormBuilder dropdown(@Nonnull String text, @Nonnull List<String> options, int defaultOption) {
            Preconditions.checkPositionIndex(defaultOption, options.size(), "Default option index out of bounds");
            return element(new ElementDropdown(text, options, defaultOption));
        }

        public CustomFormBuilder input(@Nonnull String text) {
            return element(new ElementInput(text));
        }

        public CustomFormBuilder input(@Nonnull String text, @Nonnull String placeholder) {
            return element(new ElementInput(text, placeholder));
        }

        public CustomFormBuilder input(@Nonnull String text, @Nonnull String placeholder, @Nonnull String defaultText) {
            return element(new ElementInput(text, placeholder, defaultText));
        }

        public CustomFormBuilder label(@Nonnull String text) {
            return element(new ElementLabel(text));
        }

        public CustomFormBuilder slider(@Nonnull String elementText) {
            return element(new ElementSlider(elementText));
        }

        public CustomFormBuilder slider(@Nonnull String elementText, float minimum, float maximum) {
            return element(new ElementSlider(elementText, minimum, maximum));
        }

        public CustomFormBuilder slider(@Nonnull String elementText, float minimum, float maximum, int stepCount) {
            return element(new ElementSlider(elementText, minimum, maximum, stepCount));
        }

        public CustomFormBuilder slider(@Nonnull String elementText, float minimum, float maximum, int stepCount, float defaultValue) {
            return element(new ElementSlider(elementText, minimum, maximum, stepCount, defaultValue));
        }

        public CustomFormBuilder stepSlider(@Nonnull String elementText) {
            return element(new ElementStepSlider(elementText));
        }

        public CustomFormBuilder stepSlider(@Nonnull String elementText, @Nonnull List<String> stepOptions) {
            return element(new ElementStepSlider(elementText, stepOptions));
        }

        public CustomFormBuilder stepSlider(@Nonnull String elementText, @Nonnull List<String> stepOptions, int defaultStepIndex) {
            return element(new ElementStepSlider(elementText, stepOptions, defaultStepIndex));
        }

        public CustomFormBuilder toggle(@Nonnull String elementText) {
            return element(new ElementToggle(elementText));
        }

        public CustomFormBuilder toggle(@Nonnull String elementText, boolean defaultValue) {
            return element(new ElementToggle(elementText, defaultValue));
        }

        public CustomFormBuilder element(@Nonnull Element element) {
            elements.add(element);
            return this;
        }

        public CustomFormBuilder elements(@Nonnull Element element, @Nonnull Element... elements) {
            this.elements.add(element);
            this.elements.addAll(Arrays.asList(elements));
            return this;
        }

        public CustomFormBuilder elements(@Nonnull Collection<Element> elements) {
            this.elements.addAll(elements);
            return this;
        }

        public CustomFormBuilder image(@Nonnull ImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        @Override
        public CustomForm build() {
            return new CustomForm(title, imageData, Collections.unmodifiableList(elements));
        }

        @Override
        protected CustomFormBuilder self() {
            return this;
        }
    }
}
