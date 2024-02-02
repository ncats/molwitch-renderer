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
        return checkDoubleResult(product);
    }

    public static float safeFloatMultiply(double factor1, double factor2) {
        //special case: multiplying by zero yields zero
        if(factor1 == 0 || factor2 == 0) {
            return 0;
        }
        double product = factor1 * factor2;
        return checkDoubleResult(product);
    }

    public static float safeFloatMultiply(float factor1, double factor2, float factor3) {
        //special case: multiplying by zero yields zero
        if(factor1 == 0 || factor2 == 0 || factor3 == 0) {
            return 0;
        }
        double product = factor1 * factor2;
        if( product > Float.MAX_VALUE || product < (-1*Float.MIN_VALUE)) {
            String msg = "Error multiplying " + factor1 + " by " + factor2 + "(result is too large or too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        double product2 = product * factor3;
        return checkDoubleResult(product2);
    }
    public static float safeFloatAdd(float addend1, float addend2) {
        if( (addend1 == Float.MAX_VALUE && addend2 > 0 )|| (addend2 == Float.MAX_VALUE && addend1 > 0)) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(one addend is too large)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        if( (addend1 == (-1 *Float.MAX_VALUE) && addend2 < 0 )|| (addend2 == (-1 * Float.MAX_VALUE) && addend1 < 0)) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(one addend is too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        double sum = addend1 + addend2;
        return checkDoubleResult(sum);
    }

    public static float safeFloatAdd(float addend1, double addend2) {
        if( (addend1 == Float.MAX_VALUE && addend2 > 0 )|| (addend2 == Float.MAX_VALUE && addend1 > 0)) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(one addend is too large)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        if( (addend1 ==(-1 * Float.MAX_VALUE) && addend2 < 0 )|| (addend2 == (-1 * Float.MAX_VALUE) && addend1 < 0)) {
            String msg = "Error adding " + addend1 + " to " + addend2 + "(one addend is too small)";
            logger.severe( msg);
            throw new ArithmeticException(msg);
        }

        double sum = addend1 + addend2;
        return checkDoubleResult(sum);
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
        if( value > 0) {
            return (int) Math.floor(initialResult);
        }
        return (int) Math.round(initialResult);
    }

    private static float checkDoubleResult(double result) {
        if( result > Float.MAX_VALUE ) {
            String msg = "Error performing operation (result [" + result + "] is too large)";
            logger.severe(msg);
            throw new ArithmeticException(msg);
        } else if( result < (-1 *Float.MAX_VALUE)) {
            String msg = "Error performing operation (result [" + result + "] is too small)";;
            logger.severe(msg);
            throw new ArithmeticException(msg);
        }
        return (float)result;
    }

}
