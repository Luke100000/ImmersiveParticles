package immersive_particles.util;

public class Utils {
    public static double squaredDistance(double x1, double x2, double y1, double y2, double z1, double z2) {
        return Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2) + Math.pow((z1 - z2), 2);
    }

    public static double cosNoise(double time) {
        return cosNoise(time, 5);
    }

    public static double cosNoise(double time, int layers) {
        double value = 0.0f;
        for (int i = 0; i < layers; i++) {
            value += Math.cos(time);
            time *= 1.7;
        }
        return value;
    }

    public static String lastSplit(String string, String sep) {
        return string.substring(string.lastIndexOf(sep) + 1);
    }

    public static String firstSplit(String string, String sep) {
        int i = string.lastIndexOf(sep);
        if (i < 0) {
            return string;
        } else {
            return string.substring(0, i);
        }
    }
}
