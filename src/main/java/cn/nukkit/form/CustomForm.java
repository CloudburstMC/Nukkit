package cn.nukkit.form;

import cn.nukkit.form.element.*;
import cn.nukkit.form.response.CustomFormResponse;
import cn.nukkit.form.util.FormType;
import cn.nukkit.form.util.ImageData;
import cn.nukkit.form.util.ImageType;
import cn.nukkit.player.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@ToString
public class CustomForm extends Form<CustomFormResponse> {

    @JsonProperty("content")
    private final List<Element> elements;

    private final ImageData icon;

    public CustomForm(String title,
                      ImageData icon,
                      List<Element> elements,
                      List<BiConsumer<Player, CustomFormResponse>> listeners,
                      List<Consumer<Player>> closeListeners,
                      List<Consumer<Player>> errorListeners
    ) {
        super(FormType.CUSTOM, title, listeners, closeListeners, errorListeners);
        this.icon = icon;
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

        private ImageData icon = null;

        public CustomFormBuilder dropdown(@Nonnull String text, @Nonnull String... options) {
            return dropdown(text, Arrays.asList(options));
        }

        public CustomFormBuilder dropdown(@Nonnull String text, @Nonnull List<String> options) {
            return element(new ElementDropdown(text, options));
        }

        public CustomFormBuilder dropdown(@Nonnull String text, int defaultOption, @Nonnull String... options) {
            return dropdown(text, defaultOption, Arrays.asList(options));
        }

        public CustomFormBuilder dropdown(@Nonnull String text, int defaultOption, @Nonnull List<String> options) {
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

        public CustomFormBuilder stepSlider(@Nonnull String elementText, @Nonnull String... stepOptions) {
            return stepSlider(elementText, Arrays.asList(stepOptions));
        }

        public CustomFormBuilder stepSlider(@Nonnull String elementText, @Nonnull List<String> stepOptions) {
            return element(new ElementStepSlider(elementText, stepOptions));
        }

        public CustomFormBuilder stepSlider(@Nonnull String elementText, int defaultStepIndex, @Nonnull String... stepOptions) {
            return stepSlider(elementText, defaultStepIndex, Arrays.asList(stepOptions));
        }

        public CustomFormBuilder stepSlider(@Nonnull String elementText, int defaultStepIndex, @Nonnull List<String> stepOptions) {
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

        public CustomFormBuilder icon(@Nonnull ImageType imageType, @Nonnull String imageData) {
            this.icon = new ImageData(imageType, imageData);
            return this;
        }

        @Override
        public CustomForm build() {
            return new CustomForm(title, icon, Collections.unmodifiableList(elements), listeners, closeListeners, errorListeners);
        }

        @Override
        protected CustomFormBuilder self() {
            return this;
        }
    }
}
