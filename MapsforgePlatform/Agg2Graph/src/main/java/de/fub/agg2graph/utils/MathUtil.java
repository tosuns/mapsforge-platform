package de.fub.agg2graph.utils;

/*
 * @(#)MathUtil.java  1.0 Apr 26, 2008
 *
 *  The MIT License
 *
 *  Copyright (c) 2008 Malachi de AElfweald <malachid@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class MathUtil {

    protected static BigInteger BigIntegerZERO = BigInteger.ZERO;
    protected static BigInteger BigIntegerONE = BigInteger.ONE;
    protected static BigInteger BigIntegerTWO = BigInteger.valueOf(2);
    protected static BigInteger BigIntegerTHREE = BigInteger.valueOf(3);
    protected static BigInteger FactorialBreakpoint = BigInteger.valueOf(96);
    protected static BigDecimal BigDecimalZERO = BigDecimal.ZERO;
    protected static BigDecimal BigDecimalONE = BigDecimal.ONE;
    protected static BigDecimal BigDecimalTWO = new BigDecimal(2);
    protected static BigDecimal BigDecimalFOUR = new BigDecimal(4);

    public static BigInteger sqrt(BigInteger number) {
        return sqrt(number, BigIntegerONE);
    }

    public static BigDecimal sqrt(BigDecimal number, RoundingMode rounding) {
        return sqrt(number, BigDecimalONE, rounding);
    }

    protected static BigInteger sqrt(BigInteger number, BigInteger guess) {
        // ((n/g) + g)/2: until same result twice in a row
//    BigInteger result = number.divide(guess).add(guess).divide(BigIntegerTWO);
//    if(result.compareTo(guess) == 0)
//      return result;
//
//    return sqrt(number, result);

        // redoing this to avoid StackOverFlow
        BigInteger result = BigIntegerZERO;
        BigInteger flipA = result;
        BigInteger flipB = result;
        boolean first = true;
        while (result.compareTo(guess) != 0) {
            if (!first) {
                guess = result;
            } else {
                first = false;
            }

            result = number.divide(guess).add(guess).divide(BigIntegerTWO);
            // handle flip flops
            if (result.equals(flipB)) {
                return flipA;
            }

            flipB = flipA;
            flipA = result;
        }
        return result;

    }

    public static BigDecimal sqrt(BigDecimal number, BigDecimal guess, RoundingMode rounding) {
        BigDecimal result = BigDecimalZERO;
        BigDecimal flipA = result;
        BigDecimal flipB = result;
        boolean first = true;
        while (result.compareTo(guess) != 0) {
            if (!first) {
                guess = result;
            } else {
                first = false;
            }

            result = number.divide(guess, rounding).add(guess).divide(BigDecimalTWO, rounding);
            // handle flip flops
            if (result.equals(flipB)) {
                return flipA;
            }

            flipB = flipA;
            flipA = result;
        }
        return result;
    }
}
