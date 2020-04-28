package cn.nukkit.form;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.SimpleFormResponse;
import cn.nukkit.form.util.FormType;
import cn.nukkit.form.util.ImageType;
import cn.nukkit.player.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@ToString
public class SimpleForm extends Form<SimpleFormResponse> {

    private final String content;
    private final List<ElementButton> buttons;

    @JsonIgnore
    private final Int2ObjectMap<Consumer<Player>> buttonListeners;

    public SimpleForm(
            String title,
            String content,
            List<ElementButton> buttons,
            Int2ObjectMap<Consumer<Player>> buttonListeners,
            List<BiConsumer<Player, SimpleFormResponse>> listeners,
            List<Consumer<Player>> closeListeners,
            List<Consumer<Player>> errorListeners
    ) {
        super(FormType.SIMPLE, title, listeners, closeListeners, errorListeners);
        this.content = content;
        this.buttons = buttons;
        this.buttonListeners = buttonListeners;
    }

    public ElementButton getButton(int index) {
        return buttons.size() > index ? buttons.get(index) : null;
    }

    @Override
    public void handleResponse(Player p, JsonNode node) {
        if (!node.isInt()) {
            error(p);
            log.warn("Received invalid response for SimpleForm {}", node);
            return;
        }

        int clicked = node.intValue();

        Consumer<Player> buttonListener = buttonListeners.get(clicked);
        if (buttonListener != null) {
            buttonListener.accept(p);
        }

        submit(p, new SimpleFormResponse(clicked, getButton(clicked)));
    }

    public static class SimpleFormBuilder extends FormBuilder<SimpleForm, SimpleFormBuilder, SimpleFormResponse> {

        private String content = "";
        private final List<ElementButton> buttons = new ArrayList<>();
        private final Int2ObjectMap<Consumer<Player>> buttonListeners = new Int2ObjectOpenHashMap<>();

        /**
         * Set the form text content
         *
         * @param content form text content
         * @return self builder instance
         */
        public SimpleFormBuilder content(@Nonnull String content) {
            Preconditions.checkNotNull(content, "content must not be null");
            this.content = content;
            return this;
        }

        /**
         * Add a button
         *
         * @param text button title
         * @return self builder instance
         */
        public SimpleFormBuilder button(@Nonnull String text) {
            this.buttons.add(new ElementButton(text));
            return this;
        }

        /**
         * Add a button with image
         *
         * @param text      button title
         * @param imageType button image type
         * @param imageData button image data
         * @return self builder instance
         */
        public SimpleFormBuilder button(@Nonnull String text, @Nonnull ImageType imageType, @Nonnull String imageData) {
            this.buttons.add(new ElementButton(text, imageType, imageData));
            return this;
        }

        /**
         * Adda button with on click callback
         *
         * @param text   button title
         * @param action callback called when the button is clicked
         * @return self builder instance
         */
        public SimpleFormBuilder button(@Nonnull String text, @Nonnull Consumer<Player> action) {
            this.buttons.add(new ElementButton(text));
            this.buttonListeners.put(buttons.size() - 1, action);
            return this;
        }

        /**
         * Add a button with image and on click callback
         *
         * @param text      button title
         * @param imageType button image type
         * @param imageData button image data
         * @param action    callback called when the button is clicked
         * @return self builder instance
         */
        public SimpleFormBuilder button(@Nonnull String text, @Nonnull ImageType imageType, @Nonnull String imageData, @Nonnull Consumer<Player> action) {
            this.buttons.add(new ElementButton(text, imageType, imageData));
            this.buttonListeners.put(buttons.size() - 1, action);
            return this;
        }

        /**
         * Add one or more buttons
         *
         * @param button  button element
         * @param buttons list of button elements
         * @return self builder instance
         */
        public SimpleFormBuilder buttons(@Nonnull ElementButton button, @Nonnull ElementButton... buttons) {
            this.buttons.add(button);
            this.buttons.addAll(Arrays.asList(buttons));
            return this;
        }

        /**
         * Add list of buttons
         *
         * @param buttons list of button elements
         * @return self builder instance
         */
        public SimpleFormBuilder buttons(@Nonnull Collection<ElementButton> buttons) {
            this.buttons.addAll(buttons);
            return this;
        }

        /**
         * Builds a new SimpleForm instance using builder values
         *
         * @return SimpleForm instance
         */
        @Override
        public SimpleForm build() {
            return new SimpleForm(title, content, buttons, buttonListeners, listeners, closeListeners, errorListeners);
        }

        @Override
        protected SimpleFormBuilder self() {
            return this;
        }
    }
}
