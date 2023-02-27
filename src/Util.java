import java.awt.*;

public class Util {
    public static Color lerpColor(Color a, Color b, float t) {
        try {
            float r = a.getRed() * (1 - t) + b.getRed() * t;
            float g = a.getGreen() * (1 - t) + b.getGreen() * t;
            float bl = a.getBlue() * (1 - t) + b.getBlue() * t;
            return new Color(r / 255f, g / 255f, bl / 255f);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static Color lerpColor(Color a, Color b, float t, boolean alpha) {
        try {
            Color c = lerpColor(a, b, t);
            return new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, alpha ? (float)(4 * (t - 0.5) * (t - 0.5)) : 1);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static float constraint(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float map(float value, float aMin, float aMax, float bMin, float bMax) {
        return bMin + (value - aMin) * (bMax - bMin) / (aMax - aMin);
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
