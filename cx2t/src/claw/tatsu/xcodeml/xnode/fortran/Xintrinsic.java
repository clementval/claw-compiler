/*
 * This file is released under terms of BSD license
 * See LICENSE file for more information
 */
package claw.tatsu.xcodeml.xnode.fortran;

import java.util.HashMap;
import java.util.Map;

/**
 * Intrinsic function name as enumeration.
 *
 * @author clementval
 */
public enum Xintrinsic {
    ABS, AIMAG, AINT, AMAX0, AMIN0, ANINT, CEILING, CMPLX, CONJG, DBLE, DCMPLX, DFLOAT, DIM, DNUM, DPROD, DREAL, FLOAT,
    FLOOR, IFIX, IMAG, INT, INUM, JNUM, KNUM, MAX, MAX1, MIN, MIN1, MOD, MODULO, NINT, QCMPLX, QEXT, QFLOAT, QNUM,
    QREAL, REAL, RNUM, SIGN, SNGL, ZEXT, PRESENT, RAN, RANF, EXPONENT, FRACTION, NEAREST, RRSPACING, SCALE,
    SET_EXPONENT, SPACING, HUGE, ILEN, MAXEXPONENT, MINEXPONENT, PRECISION, RADIX, RANGE, SIZEOF, TINY, DOT_PRODUCT,
    MATMUL, MCLOCK, SECNDS, SELECTED_CHAR_KIND, SELECTED_INT_KIND, SELECTED_REAL_KIND, ACOSD, ACOSH, ASIN, ASIND, ASINH,
    ATAN, ATAN2, ATAN2D, ATAND, ATANH, BESSEL_J0, BESSEL_J1, BESSEL_JN, BESSEL_Y0, BESSEL_Y1, BESSEL_YN, COS, COSD,
    COSH, COTAN, COTAND, EXP, EXP10, GAMMA, HYPOT, LOG, LOG10, LOG_GAMMA, SIN, SIND, SINH, SQRT, TAN, TAND, TANH,
    BIT_SIZE, STORAGE_SIZE, LEADZ, POPCNT, POPPAR, TRAILZ, LGE, LGT, LLE, LLT, ACHAR, CHAR, IACHAR, ICHAR, ADJUSTL,
    ADJUSTR, INDEX, LEN_TRIM, SCAN, VERIFY, REPEAT, TRIM, COMMAND_ARGUMENT_COUNT, IARG, IARGC, LEN, NARGS, NUMARG,
    MERGE, PACK, SPREAD, UNPACK, ALLOCATED, IS_CONTIGUOUS, LBOUND, RANK, SHAPE, SIZE, UBOUND, MAXLOC, MINLOC, FINDLOC,
    CSHIFT, EOSHIFT, RESHAPE, TRANSPOSE, ALL, ANY, COUNT, IALL, IANY, IPARITY, MAXVAL, MINVAL, NORM2, PARITY, PRODUCT,
    SUM, COSHAPE, IMAGE_INDEX, LCOBOUND, UCOBOUND, NUM_IMAGES, THIS_IMAGE, EXTENDS_TYPE_OF, SAME_TYPE_AS, ASSOCIATED,
    BADDRESS, IADDR, CACHESIZE, EOF, ERF, ERFC, ERFC_SCALED, FP_CLASS, INT_PTR_KIND, ISNAN, LOC, LOGICAL, MALLOC,
    NEW_LINE, NULL, TRANSFER, IS_IOSTAT_END, IS_IOSTAT_EOR;

    private static final Map<String, Xintrinsic> _stringToEnum = new HashMap<>();

    static
    {
        for (Xintrinsic value : values())
        {
            _stringToEnum.put(value.toString().toLowerCase(), value);
        }
    }

    public static Xintrinsic fromString(String value)
    {
        return (value == null || !_stringToEnum.containsKey(value.toLowerCase())) ? null
                : _stringToEnum.get(value.toLowerCase());
    }
}
