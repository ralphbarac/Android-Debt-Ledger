package cs4474.g9.debtledger.ui.settings;

import android.graphics.Color;

public class AccessibleColours {

    private static final int positiveColour = Color.GREEN;
    private static final int negativeColor = Color.RED;
    private static final int positiveAccessibleColour = Color.parseColor("#018571");
    private static final int negativeAccessibleColour = Color.parseColor("#A6611A");

    public static int getPositiveColour(boolean isInAccessibleMode) {
        return isInAccessibleMode ? positiveAccessibleColour : positiveColour;
    }

    public static int getNegativeColour(boolean isInAccessibleMode) {
        return isInAccessibleMode ? negativeAccessibleColour : negativeColor;
    }
}
