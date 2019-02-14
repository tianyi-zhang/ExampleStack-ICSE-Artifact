/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora.spi.io;

/**
 * Based on the work of x4u (http://stackoverflow.com/users/237321/x4u)
 * http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java/6162687#6162687
 */
final class HalfPrecision {

    // ignores the higher 16 bits
    static float toFloat(int hbits) {
        // Thanks to Jakob project: https://github.com/jawi/jacob
        int exp = (hbits >> 10) & 0x1f;
        int mant = hbits & 0x3ff;

        double val;
        if (exp == 0) {
            val = mant * Math.pow(2, -24);
        } else if (exp != 31) {
            val = (mant + 1024) * Math.pow(2, exp - 25);
        } else if (mant != 0) {
            val = Double.NaN;
        } else {
            val = Double.POSITIVE_INFINITY;
        }

        return (float) (((hbits & 0x8000) == 0) ? val : -val);
    }

    // returns all higher 16 bits as 0 for all results
    static int fromFloat(float fval) {
        int fbits = Float.floatToIntBits(fval);
        int sign = (fbits >>> 16) & 0x8000;                 // sign only
        int val = (fbits & 0x7fffffff) + 0x1000;          // rounded value

        if (val >= 0x47800000) {                          // might be or become NaN/Inf
            if ((fbits & 0x7fffffff) >= 0x47800000) {     // is or must become NaN/Inf
                if (val < 0x7f800000) {                   // was value but too large
                    return sign | 0x7c00;                 // make it +/-Inf
                }
                return sign | 0x7c00 |                    // remains +/-Inf or NaN
                        (fbits & 0x007fffff) >>> 13;      // keep NaN (and Inf) bits
            }
            return sign | 0x7bff;                         // unrounded not quite Inf
        }
        if (val >= 0x38800000) {                          // remains normalized value
            return sign | val - 0x38000000 >>> 13;        // exp - 127 + 15
        }
        if (val < 0x33000000) {                           // too small for subnormal
            return sign;                                  // becomes +/-0
        }
        val = (fbits & 0x7fffffff) >>> 23;                // tmp exp for subnormal calc
        return sign | ((fbits & 0x7fffff | 0x800000)      // add subnormal bit
                + (0x800000 >>> val - 102)                // round depending on cut off
                >>> 126 - val);                           // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }

}
