package official.com.windowdetailssharingapp.core.models;

import java.io.Serializable;

public class Property implements Serializable {

    private final String name;
    private String value;

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}