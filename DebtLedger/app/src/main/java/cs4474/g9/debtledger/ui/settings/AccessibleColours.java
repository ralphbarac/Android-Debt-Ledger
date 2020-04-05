package cs4474.g9.debtledger.ui.settings;

import android.graphics.Color;

public class AccessibleColours {

    private static final int positiveColour = Color.GREEN;
    private static final int negativeColor = Color.RED;
    private static final int positiveAccessibleColour = Color.parseColor("#4dac26");
    private static final int negativeAccessibleColour = Color.parseColor("#d01c8b");

    public static int getPositiveColour(boolean isInAccessibleMode) {
        return isInAccessibleMode ? positiveAccessibleColour : positiveColour;
    }

    public static int getNegativeColour(boolean isInAccessibleMode) {
        return isInAccessibleMode ? negativeAccessibleColour : negativeColor;
    }
}
