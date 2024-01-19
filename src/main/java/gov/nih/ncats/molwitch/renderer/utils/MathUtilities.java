package gov.nih.ncats.molwitch.renderer.utils;

import java.util.logging.Logger;

public class MathUtilities {

    private static final Logger logger =
            Logger.getLogger (MathUtilities.class.getName ());

    public static float safeFloatMultiply(float factor1, float factor2) {
        //special case: multiplying by zero yields zero
        if(factor1 == 0 || factor2 == 0) {
            return 0;
        }
        double product = factor1 * factor2;
        if( product > Float.MAX_VALUE || product < Float.MIN_VALUE) {
            String msg = "Error multiplying " + factor1 + " by " + factor2 + "(result is too large or too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }
        return (float)product;
    }

    public static float safeFloatMultiply(double factor1, double factor2) {
        //special case: multiplying by zero yields zero
        if(factor1 == 0 || factor2 == 0) {
            return 0;
        }
        double product = factor1 * factor2;
        if( product > Float.MAX_VALUE || product < Float.MIN_VALUE) {
            String msg = "Error multiplying " + factor1 + " by " + factor2 + "(result is too large or too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }
        return (float)product;
    }
    public static float safeFloatAdd(float addend1, float addend2) {
        if( (addend1 == Float.MAX_VALUE && addend2 > 0 )|| (addend2 == Float.MAX_VALUE && addend1 > 0)) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(one addend is too large)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        if( (addend1 == Float.MIN_VALUE && addend2 < 0 )|| (addend2 == Float.MIN_VALUE && addend1 < 0)) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(one addend is too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        double sum = addend1 + addend2;
        if( sum > Float.MAX_VALUE || sum < Float.MIN_VALUE) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(result is too large or too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }
        return (float)sum;
    }

    public static int safeScaleInt(int value, float scale) {
        //special case: multiplying by zero yields zero
        if(value == 0 || scale == 0.0) {
            return 0;
        }
        double initialResult = ((float) value) * scale;
        if( initialResult > Integer.MAX_VALUE || initialResult < Integer.MIN_VALUE) {
            String msg = "Error multiplying " + value + " by " + scale + "(result is too large or too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }
        return (int) Math.floor(initialResult);
    }
}
