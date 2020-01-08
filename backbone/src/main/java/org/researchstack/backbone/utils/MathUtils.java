package org.researchstack.backbone.utils;

import java.lang.Math;

public class MathUtils {

     /**
     * Method to multiply two quaternions (non-commutative).
     * For original formula, see http://mathworld.wolfram.com/Quaternion.html
     **/
    public static float[] multiplyQuaternions(float[] q1, float[] q2) {
        float[] productQuaternion = new float[4];
        productQuaternion[0] = (q1[0] * q2[0]) - (q1[1] * q2[1]) - (q1[2] * q2[2]) - (q1[3] * q2[3]);
        productQuaternion[1] = (q1[0] * q2[1]) + (q1[1] * q2[0]) + (q1[2] * q2[3]) - (q1[3] * q2[2]);
        productQuaternion[2] = (q1[0] * q2[2]) - (q1[1] * q2[3]) + (q1[2] * q2[0]) + (q1[3] * q2[1]);
        productQuaternion[3] = (q1[0] * q2[3]) + (q1[1] * q2[2]) - (q1[2] * q2[1]) + (q1[3] * q2[0]);
        return productQuaternion;
    }


    /**
     * Method to calculate the inverse (complex conjugate) of a quaternion.
     * For original formula, see http://mathworld.wolfram.com/Quaternion.html
     **/
    public static float[] calculateInverseOfQuaternion(float[] originalQuaternion) {
        float[] inverseQuaternion = new float[4];
        inverseQuaternion[0] = originalQuaternion[0];
        inverseQuaternion[1] = (-1 * originalQuaternion[1]);
        inverseQuaternion[2] = (-1 * originalQuaternion[2]);
        inverseQuaternion[3] = (-1 * originalQuaternion[3]);
        return inverseQuaternion;
    }


    /**
     * Methods to calculate Tait-Bryan/Euler angles from device attitude quaternions
     **/
    public static double allOrientationsForPitch(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (x * w + y * z), 1.0 - 2.0 * (x * x + z * z)));
        return angle_in_rads;
    }

    public static double allOrientationsForRoll(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.atan2(2.0 * (y * w - x * z), 1.0 - 2.0 * (y * y + z * z)));
        return angle_in_rads;
    }

    public static double allOrientationsForYaw(double w, double x, double y, double z) {
        double angle_in_rads;
        angle_in_rads = (Math.asin(2.0 * (x * y - w * z)));
        return angle_in_rads;
    }
}
