package official.com.windowdetailssharingapp.core.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:51 AM
 */
public class LocalStorage {

    private final static String SAVE_PATH = Environment.getExternalStorageDirectory()
            + "/Android/data/official.com.windowdetailssharingapp/Projects/";

    private final static String EXPORT_PATH = Environment.getExternalStorageDirectory()
            + "/Android/data/official.com.windowdetailssharingapp/Exports/";

    private final static File SAVE_DIR;
    private final static File EXPORT_DIR;

    static {
        SAVE_DIR = new File(SAVE_PATH);
        if (!SAVE_DIR.exists()) {
            SAVE_DIR.mkdirs();
        }

        EXPORT_DIR = new File(EXPORT_PATH);
        if (!EXPORT_DIR.exists()) {
            EXPORT_DIR.mkdirs();
        }
    }

    public static File getSaveDir() {
        return SAVE_DIR;
    }

    public static File getExportDir() {
        return EXPORT_DIR;
    }

    @Nullable
    public static Bitmap getBitmap(int property, String itemId, String projectName) {
        File projectDir = new File(SAVE_DIR, projectName);
        File propertyBitmapFile = new File(projectDir, itemId + "_" + String.valueOf(property) + ".jpg");
        if (!propertyBitmapFile.exists()) {
            return null;
        }

        String filePath = propertyBitmapFile.getAbsolutePath();
        return BitmapFactory.decodeFile(filePath);
    }

    public static boolean saveBitmap(Bitmap bitmap, int property, String itemId, String projectName) {
        File projectDir = new File(SAVE_DIR, projectName);
        File propertyBitmapFile = new File(projectDir, itemId + "_" + String.valueOf(property) + ".jpg");
        if (propertyBitmapFile.exists()) {
            propertyBitmapFile.delete();
        }

        try {
            propertyBitmapFile.createNewFile();
            FileOutputStream out = new FileOutputStream(propertyBitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public static class ItemTypes {

        // Window Types
        public static String[] WINDOW_TYPES = {
                "Louvre Windows", // 01
                "Hinged Door Units", // 02
                "Casement/Awning",// 03
                "Aneeta Windows", // 04
                "Bi-Fold Doors/Windows", // 05
                "Double Hung Windows", // 06
                "Face Sliders", // 07
                "Fixed Glass Windows", // 08
                "Pivot Doors", // 09
                "Standard Slider Suites" // 10
        };


        // Aneeta Windows
        public static String[] ANEETA_WINDOWS = {
                "Aneeta insert type",
                "Window configuration",
                "Timber type (Jamb)",
                "Sill timber type",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Lock Side",
                "Aluminium Finish / Colour",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Insert Positioning",
                "Flyscreen requirements",
                "Flyscreen mesh type",
                "Special requirements (BAL etc)",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Bi-Fold D/W Units
        public static String[] BI_FOLD_UNITS = {
                "Door/Window Configuration",
                "Timber type (Jamb)",
                "Sill type (Sub-sill, channel, hardwood etc)",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Door/ Sash detail (profile, section sizes)",
                "Doors fold in or out",
                "Active door on double doors",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Hardware finish/ style/ type",
                "Drop-bolt lengths/quantity/finish",
                "Entrance door hardware",
                "Screen kits (S1E, ES2, etc)",
                "Screen kit finish/colour",
                "Flyscreen mesh type",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Casement and Awning Windows
        public static String[] CASEMENT_AWNING_WINDOWS = {
                "Window type (Casement/awning)",
                "Window configuration (Single, double, multi etc)",
                "Timber type (Jamb)",
                "Sill timber type",
                "Height of W (O/A + other)",
                "Width of W (O/A + other)",
                "Frame Depth",
                "Sill Depth",
                "Sash detail (profile, section sizes)",
                "Sashes swing in or out",
                "Active sash on double sets",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Hardware system (Truth, hinged, standard friction etc)",
                "Motorised winder system",
                "Hardware finish/ colour",
                "Locking requirements (drop-bolts for double casement etc)",
                "Flyscreen requirements/mesh type",
                "Flyscreen pelmet detail",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Double Hung Windows
        public static String[] DOUBLE_HUNG_WINDOWS = {
                "Door/Window Configuration",
                "Timber type (Jamb)",
                "Timber type (Sill)",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Door/ Sash Profile",
                "Top-rail & Stile Section size",
                "Bottom-rail Section size",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Balance type",
                "Track Finish (if CW)",
                "Locking Hardware & Finish",
                "Flyscreen",
                "Flyscreen mesh type",
                "Sash horns",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Face Sliders
        public static String[] FACE_SLIDERS = {
                "Door/Window Configuration",
                "Timber type (Jamb)",
                "Timber type (Sill)",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Internal Frame Depth",
                "External Frame Depth",
                "Internal Sill Depth",
                "External Sill Depth",
                "Door/ Sash Profile",
                "Top-rail & Stile Section size",
                "Bottom-rail Section size",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Sliding Hardware System",
                "Track Finish",
                "Locking Hardware & Finish",
                "Sliding Flyscreens/flydoors",
                "Flyscreen mesh type",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Fixed Glass Windows
        public static String[] FIXED_GLASS_WINDOWS = {
                "Window configuration/bays",
                "Timber type (Jamb)",
                "Sill timber type",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Glazing rebate/plants",
                "Rebate/plant size",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Glass/ sash position within frame",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Louvre Windows
        public static String[] LOUVRE_WINDOWS = {
                "Number of louvre bays",
                "Timber type (Jamb)",
                "Sill timber type",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Breezway System & Clip size",
                "Gallery location/set-out",
                "Gallery colour/finish",
                "Clip colour finish",
                "Lock requirements/side",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Timber blades",
                "Flyscreen requirements",
                "Flyscreen mesh type",
                "Map rod requirements",
                "Hardware extras (powerlouvres etc)",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Pivot Doors
        public static String[] PIVOT_DOORS = {
                "Timber type (Jamb)",
                "Timber type (Sill)",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Pivot Side (Outside view)",
                "Door Make-up (e.g: full-light, T&G, veneer etc)",
                "Effective board widths",
                "Door thickness",
                "Door swings in or out",
                "Side-light",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Glass position within frame",
                "Hardware finish/ style/ type",
                "Locking hardware",
                "Pivot type",
                "Handle requirements",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Hinged Door Units
        public static String[] HINGED_DOOR_UNITS = {
                "Door type (Single/Double)",
                "Timber type (Jamb)",
                "Sill timber type",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Door detail (profile, section sizes)",
                "Hinge Side (Outside view)",
                "Doors swing in or out",
                "Active door (double doors)",
                "Head clearances",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Hardware (Drop-bolts)",
                "Hardware (Entrance kit)",
                "Hardware (Handles)",
                "Hardware finish/colour",
                "Flyscreen Requirements",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };


        // Standard Slider Suites
        public static String[] STANDARD_SLIDER_SUITES = {
                "Door/Window Configuration",
                "Timber type (Jamb)",
                "Timber type (Sill)",
                "Height (O/A + Other)",
                "Width (O/A + Other)",
                "Frame Depth",
                "Sill Depth",
                "Door/ Sash Profile",
                "Top-rail & Stile Section size",
                "Bottom-rail Section size",
                "Glass Requirements (type, make-up, thickness, etc.)",
                "Sliding Hardware System",
                "Track Finish",
                "Locking Hardware & Finish",
                "Sliding Flyscreens/flydoors",
                "Flyscreen mesh type",
                "Site Accessibility",
                "Site Assembling / Glazing Required?"
        };

    }

}