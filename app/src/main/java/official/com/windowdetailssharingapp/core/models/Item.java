package official.com.windowdetailssharingapp.core.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.ANEETA_WINDOWS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.BI_FOLD_UNITS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.CASEMENT_AWNING_WINDOWS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.DOUBLE_HUNG_WINDOWS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.FACE_SLIDERS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.FIXED_GLASS_WINDOWS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.HINGED_DOOR_UNITS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.LOUVRE_WINDOWS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.PIVOT_DOORS;
import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.STANDARD_SLIDER_SUITES;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:00 AM
 */
public class Item implements Serializable {

    private String id;
    private String type;
    private String project;
    private List<Property> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<Property> getProperties() {
        return properties;
    }

    private void setProperties(String[] properties) {
        this.properties = new ArrayList<>();
        for (String propertyName : properties) {
            Property property = new Property(propertyName, "");
            this.properties.add(property);
        }
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void createPropertiesFromType(String type) {
        switch (type) {
            case "Louvre Windows":
                setProperties(LOUVRE_WINDOWS);
                break;
            case "Hinged Door Units":
                setProperties(HINGED_DOOR_UNITS);
                break;
            case "Casement/Awning":
                setProperties(CASEMENT_AWNING_WINDOWS);
                break;
            case "Aneeta Windows":
                setProperties(ANEETA_WINDOWS);
                break;
            case "Bi-Fold Doors/Windows":
                setProperties(BI_FOLD_UNITS);
                break;
            case "Double Hung Windows":
                setProperties(DOUBLE_HUNG_WINDOWS);
                break;
            case "Face Sliders":
                setProperties(FACE_SLIDERS);
                break;
            case "Fixed Glass Windows":
                setProperties(FIXED_GLASS_WINDOWS);
                break;
            case "Pivot Doors":
                setProperties(PIVOT_DOORS);
                break;
            case "Standard Slider Suites":
                setProperties(STANDARD_SLIDER_SUITES);
                break;
        }
    }

}