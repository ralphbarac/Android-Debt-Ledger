package cs4474.g9.debtledger.logic;

import android.graphics.Color;

public class ColourGenerator {

    public static int generateFromName(String firstName, String lastName) {
        // A special easter egg
        if (firstName.equals("Mary") && lastName.equals("Sirohey")) {
            return Color.rgb(200, 90, 200);
        }

        float[] hsv = new float[3];
        hsv[0] = Math.abs((firstName + lastName).hashCode()) % 360;
        // If hue is approximately yellow, adjust, since yellow does not look nice
        float yellowness = Math.abs(hsv[0] - 60);
        if (yellowness < 2.5) {
            hsv[0] = hsv[0] + 60; // Green-ish
        } else if (yellowness < 5) {
            hsv[0] = hsv[0] + 240; // Magenta-ish
        } else if (yellowness < 10) {
            hsv[0] = hsv[0] + 180; // Blue-ish
        }
        hsv[1] = 0.7f;
        hsv[2] = 1.0f;
        return Color.HSVToColor(hsv);
    }

    public static int generateFromGroupName(String groupName) {
        // A special easter egg
        if (groupName.toLowerCase().contains("purple")) {
            return Color.rgb(200, 90, 200);
        }

        float[] hsv = new float[3];
        hsv[0] = Math.abs(groupName.hashCode()) % 360;
        // If hue is approximately yellow, adjust, since yellow does not look nice
        float yellowness = Math.abs(hsv[0] - 60);
        if (yellowness < 2.5) {
            hsv[0] = hsv[0] + 60; // Green-ish
        } else if (yellowness < 5) {
            hsv[0] = hsv[0] + 240; // Magenta-ish
        } else if (yellowness < 10) {
            hsv[0] = hsv[0] + 180; // Blue-ish
        }
        hsv[1] = 0.7f;
        hsv[2] = 1.0f;
        return Color.HSVToColor(hsv);
    }
}
