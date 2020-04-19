package cn.nukkit.form.response;

import cn.nukkit.form.element.ElementButton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SimpleFormResponse {

    private final int clickedId;
    private final ElementButton button;
}
