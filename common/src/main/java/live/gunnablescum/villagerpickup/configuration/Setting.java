package live.gunnablescum.villagerpickup.configuration;

public class Setting {

    private boolean value;

    public Setting(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public void toggle() {
        value = !value;
    }
}
