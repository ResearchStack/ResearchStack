package org.researchstack.backbone.utils;

import java.lang.Math;

public class MathUtils {

    /**
     * Method to calculate standard deviation from an array.
     **/
    public static double calculateSDFromArray(double [] numArray)
    {
        double sum = 0, standardDeviation = 0;
        int length = numArray.length;
        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation/length);
    }

    /**
     * Method to evaluate whether an integer is even.
     */
    public static boolean isEven(int n) {
        return n % 2 == 0;
    }

    /**
     * Method to evaluate whether an integer is odd.
     */
    public static boolean isOdd(int n) {
        return Math.abs(n % 2) == 1;
    }

    /**
     * Method to multiply two quaternions (non-commutative). For formula, see
     * http://mathworld.wolfram.com/Quaternion.html
     */
    public static float[] multiplyQuaternions(float[] q1, float[] q2) {
        float[] productQuaternion = new float[4];
        productQuaternion[0] = (q1[0] * q2[0]) - (q1[1] * q2[1]) - (q1[2] * q2[2]) - (q1[3] * q2[3]);
        productQuaternion[1] = (q1[0] * q2[1]) + (q1[1] * q2[0]) + (q1[2] * q2[3]) - (q1[3] * q2[2]);
        productQuaternion[2] = (q1[0] * q2[2]) - (q1[1] * q2[3]) + (q1[2] * q2[0]) + (q1[3] * q2[1]);
        productQuaternion[3] = (q1[0] * q2[3]) + (q1[1] * q2[2]) - (q1[2] * q2[1]) + (q1[3] * q2[0]);
        return productQuaternion;
    }

    /**
     * Method to calculate the inverse (complex conjugate) of a quaternion. For formula, see
     * http://mathworld.wolfram.com/Quaternion.html
     */
    public static float[] calculateInverseOfQuaternion(float[] originalQuaternion) {
        float[] inverseQuaternion = new float[4];
        inverseQuaternion[0] = originalQuaternion[0];
        inverseQuaternion[1] = (-1.0f * originalQuaternion[1]);
        inverseQuaternion[2] = (-1.0f * originalQuaternion[2]);
        inverseQuaternion[3] = (-1.0f * originalQuaternion[3]);
        return inverseQuaternion;
    }

    /**
     * Methods to calculate Tait-Bryan/Euler angles from device attitude quaternions
     */

    public static double allOrientationsForPitch(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * w + y * z), 1.0 - 2.0 * (x * x + z * z)));
        return angle_in_rads;
    }

    public static double allOrientationsForRoll(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * z - w * y), 1.0 - 2.0 * (x * x + y * y)));
        return angle_in_rads;
    }

    // TODO: testing roll alternatives

    // same as 1
    public static double allOrientationsForRoll11(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * z - w * y), 1.0 - 2.0 * (x * x + y * y)));
        return angle_in_rads;
    }

    // nearly correct (opposite sign for start and fin)
    public static double allOrientationsForRoll6(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * w - y * z), 1.0 - 2.0 * (w * w + z * z)));
        return angle_in_rads;
    }

    // nearly correct (opposite sign for start and fin)
    public static double allOrientationsForRoll7(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * z - w * y), 1.0 - 2.0 * (w * w + z * z)));
        return angle_in_rads;
    }

    public static double allOrientationsForRoll14(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * y - z * w), 1.0 - 2.0 * (x * x + y * y)));
        return angle_in_rads;
    }

    public static double allOrientationsForRoll15(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * y + z * w), 1.0 - 2.0 * (x * x + y * y)));
        return angle_in_rads;
    }

    public static double allOrientationsForRoll16(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * y - z * w), 1.0 - 2.0 * (y * y + z * z)));
        return angle_in_rads;
    }

    public static double allOrientationsForRoll17(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * y + z * w), 1.0 - 2.0 * (x * x + z * z)));
        return angle_in_rads;
    }


    public static double allOrientationsForAzimuth(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.asin(2.0 * (x * y - w * z)));
        return angle_in_rads;
    }
}
