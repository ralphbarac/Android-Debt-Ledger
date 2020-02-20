package cs4474.g9.debtledger.logic;

import android.graphics.Color;

public class ColourGenerator {

    public static int generateFromName(String firstName, String lastName) {
        // TODO: Improve and limit colour generation
        float[] hsv = new float[3];
        hsv[0] = (firstName.hashCode() + lastName.hashCode()) % 360;
        hsv[1] = 30;
        hsv[2] = 30;
        return Color.HSVToColor(hsv);
    }
}
