package cn.nukkit.form.response;

import cn.nukkit.form.element.ElementButton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class SimpleFormResponse {

    private final int clickedId;
    private final ElementButton button;
}
