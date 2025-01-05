package isaacwallace123.ecoverse.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradientUtils {

    /**
     * Applies a gradient to a given text from startColor to endColor.
     *
     * @param text       The text to apply the gradient to.
     * @param startColor The starting hex color (e.g., "#FFFFFF").
     * @param endColor   The ending hex color (e.g., "#FFFF00").
     * @return The gradient-formatted text for Minecraft.
     */
    public static String applyGradient(String text, String startColor, String endColor) {
        if (text == null || text.isEmpty()) return "";

        if (!startColor.startsWith("#") || !endColor.startsWith("#")) {
            throw new IllegalArgumentException("Colors must be in hex format (e.g., #RRGGBB).");
        }

        int length = text.length();

        int[] startRGB = hexToRgb(startColor);
        int[] endRGB = hexToRgb(endColor);

        StringBuilder gradientText = new StringBuilder();

        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);

            int r = (int) (startRGB[0] + (endRGB[0] - startRGB[0]) * ratio);
            int g = (int) (startRGB[1] + (endRGB[1] - startRGB[1]) * ratio);
            int b = (int) (startRGB[2] + (endRGB[2] - startRGB[2]) * ratio);

            String hexColor = rgbToHex(r, g, b);
            gradientText.append(convertHexToMinecraftColor(hexColor)).append(text.charAt(i));
        }

        return gradientText.toString();
    }

    public static boolean isGradientString(String text) {
        return text.matches(".*<#[a-fA-F0-9]{6}>.*</#[a-fA-F0-9]{6}>.*");
    }


    public static String applyGradientFromConfig(String text) {
        Pattern pattern = Pattern.compile("<#([a-fA-F0-9]{6})>(.*?)</#([a-fA-F0-9]{6})>");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String startColor = matcher.group(1);
            String gradientText = matcher.group(2);
            String endColor = matcher.group(3);

            return applyGradient(gradientText, "#" + startColor, "#" + endColor);
        }

        return text;
    }

    // Convert hex color to RGB array
    private static int[] hexToRgb(String hex) {
        return new int[]{
                Integer.parseInt(hex.substring(1, 3), 16),
                Integer.parseInt(hex.substring(3, 5), 16),
                Integer.parseInt(hex.substring(5, 7), 16)
        };
    }

    // Convert RGB values to a hex color
    private static String rgbToHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }

    // Convert hex to Minecraft color format
    private static String convertHexToMinecraftColor(String hexColor) {
        StringBuilder minecraftColor = new StringBuilder("§x");
        for (int i = 1; i < hexColor.length(); i++) {
            minecraftColor.append("§").append(hexColor.charAt(i));
        }
        return minecraftColor.toString();
    }
}