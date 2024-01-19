package gov.nih.ncats.molwitch.renderer.utils;


import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilityTests {
    @Test
    public void testSafeAddEasy() {
        float number1 = 34;
        float number2 = 100;
        float expectedSum= 34+100;
        float actualSum = MathUtilities.safeFloatAdd(number1,number2);
        assertEquals(expectedSum, actualSum,0.001);
    }

    @Test
    public void testSafeAdd0() {
        float number1 = 34;
        float number2 = 0;
        float expectedSum = number1;
        float actualSum = MathUtilities.safeFloatAdd(number1,number2);
        assertEquals(expectedSum, actualSum, 0.001);
    }

    @Test
    public void testSafeMultiplyEasy() {
        float number1 = 34;
        float number2 = 100;
        float expectedProduct= 34*100;
        float actualSum = MathUtilities.safeFloatMultiply(number1,number2);
        assertEquals(expectedProduct, actualSum, 0.001);
    }

    @Test
    public void testSafeMultiplyEasyDouble() {
        double number1 = 34;
        double number2 = 100;
        float expectedProduct= 34*100;
        float actualSum = MathUtilities.safeFloatMultiply(number1,number2);
        assertEquals(expectedProduct, actualSum, 0.001);
    }
    @Test
    public void testSafeMultiplyByZero() {
        float number1 = 34;
        float number2 = 0;
        float expectedProduct= 0;
        float actualSum = MathUtilities.safeFloatMultiply(number1,number2);
        assertEquals(expectedProduct, actualSum, 0.001);
    }

    @Test
    public void testSafeAddTooBig() {
        float number1 = 34;
        float number2 = Float.MAX_VALUE;
        ArithmeticException mathException= null;
        try {
            float actualSum = MathUtilities.safeFloatAdd(number1,number2);
        } catch (ArithmeticException ex) {
            mathException = ex;
        }
        assertNotNull(mathException);
    }

    @Test
    public void testSafeAddTooSmall() {
        float number1 = -34;
        float number2 = Float.MIN_VALUE;
        ArithmeticException mathException= null;
        try {
            float actualSum = MathUtilities.safeFloatAdd(number1,number2);
        } catch (ArithmeticException ex) {
            mathException = ex;
        }
        assertNotNull(mathException);
    }

    @Test
    public void testSafeAddSmallButOK() {
        float number1 = 34;
        float number2 = Float.MIN_VALUE;
        float expected = number1 + number2;
        ArithmeticException mathException= null;
        try {
            float actualSum = MathUtilities.safeFloatAdd(number1,number2);
            assertEquals(expected, actualSum, 0.001);
        } catch (ArithmeticException ex) {
            mathException = ex;
        }
        assertNull(mathException);
    }

    @Test
    public void testSafeMultiplyTooBig() {
        float number1 = 34;
        float number2 = Float.MAX_VALUE;
        ArithmeticException mathException= null;
        try {
            float actualProduct = MathUtilities.safeFloatMultiply(number1, number2);
        } catch (ArithmeticException ex) {
            mathException = ex;
        }
        assertNotNull(mathException);
    }

    @Test
    public void testSafeScaleInt1() {
        int counter = 4;
        float scaleFactor = 0.5f;
        int scaled = MathUtilities.safeScaleInt(counter, scaleFactor);
        assertEquals(2, scaled);
    }

    @Test
    public void testSafeScaleInt2() {
        int counter = 5;
        float scaleFactor = 0.5f;
        int scaled = MathUtilities.safeScaleInt(counter, scaleFactor);
        assertEquals(2, scaled);
    }

    @Test
    public void testSafeScaleInt3() {
        int counter = -5;
        float scaleFactor = 0.5f;
        int scaled = MathUtilities.safeScaleInt(counter, scaleFactor);
        assertEquals(-2, scaled);
    }

    @Test
    public void testLegacyOperation() {
        int weight = 1;
        int scaled = weight *= 1.75;
        int expected = 1;
        assertEquals(expected, scaled);
    }

    @Test
    public void testLegacyOperation2() {
        int weight = 2;
        int scaled = weight *= 1.75;
        int expected = 3;
        assertEquals(expected, scaled);
    }

}
