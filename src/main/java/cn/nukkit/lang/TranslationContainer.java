package cn.nukkit.lang;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TranslationContainer extends TextContainer implements Cloneable {

    protected String[] params;

    public TranslationContainer(String text) {
        this(text, new String[]{});
    }

    public TranslationContainer(String text, String params) {
        super(text);
        this.setParameters(new String[]{params});
    }

    public TranslationContainer(String text, String... params) {
        super(text);
        this.setParameters(params);
    }

    public String[] getParameters() {
        return params;
    }

    public void setParameters(String[] params) {
        this.params = params;
    }

    public String getParameter(int i) {
        return (i >= 0 && i < this.params.length) ? this.params[i] : null;
    }

    public void setParameter(int i, String str) {
        if (i >= 0 && i < this.params.length) {
            this.params[i] = str;
        }
    }

    @Override
    public TranslationContainer clone() {
        return new TranslationContainer(this.text, this.params.clone());
    }
}
