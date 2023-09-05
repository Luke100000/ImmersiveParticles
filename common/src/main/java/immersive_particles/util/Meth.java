package immersive_particles.util;

/**
 * @author Guilherme Chaguri
 */
public class Meth {
    private static final float BF_SIN_TO_COS;
    private static final int BF_SIN_BITS, BF_SIN_MASK, BF_SIN_MASK2, BF_SIN_COUNT, BF_SIN_COUNT2;
    private static final float BF_RAD_FULL, BF_RAD_TO_INDEX;
    private static final float[] BF_SIN_HALF;

    static {
        BF_SIN_TO_COS = (float) (java.lang.Math.PI * 0.5f);

        BF_SIN_BITS = 12;
        BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
        BF_SIN_MASK2 = BF_SIN_MASK >> 1;
        BF_SIN_COUNT = BF_SIN_MASK + 1;
        BF_SIN_COUNT2 = BF_SIN_MASK2 + 1;

        BF_RAD_FULL = (float) (java.lang.Math.PI * 2.0);
        BF_RAD_TO_INDEX = BF_SIN_COUNT / BF_RAD_FULL;

        BF_SIN_HALF = new float[BF_SIN_COUNT2];
        for (int i = 0; i < BF_SIN_COUNT2; i++) {
            BF_SIN_HALF[i] = (float) java.lang.Math.sin((i + java.lang.Math.min(1, i % (BF_SIN_COUNT / 4)) * 0.5) / BF_SIN_COUNT * BF_RAD_FULL);
        }

        float[] hardcodedAngles = {
                90 * 0.017453292F, // getLook when looking up (sin) - Fixes Elytra
                90 * 0.017453292F + BF_SIN_TO_COS // getLook when looking up (cos) - Fixes Elytra
        };
        for (float angle : hardcodedAngles) {
            int index1 = (int) (angle * BF_RAD_TO_INDEX) & BF_SIN_MASK;
            int index2 = index1 & BF_SIN_MASK2;
            int mul = ((index1 == index2) ? +1 : -1);
            BF_SIN_HALF[index2] = (float) (java.lang.Math.sin(angle) / mul);
        }
    }

    public static float sin(float rad) {
        int index1 = (int) (rad * BF_RAD_TO_INDEX) & BF_SIN_MASK;
        int index2 = index1 & BF_SIN_MASK2;
        // int mul = (((index1 - index2) >>> 31) << 1) + 1;
        int mul = ((index1 == index2) ? +1 : -1);
        return BF_SIN_HALF[index2] * mul;
    }

    public static float cos(float rad) {
        return sin(rad + BF_SIN_TO_COS);
    }
}