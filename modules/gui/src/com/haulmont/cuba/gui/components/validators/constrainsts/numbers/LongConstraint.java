/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts.numbers;

import java.math.BigDecimal;

public class LongConstraint implements NumberConstraint {

    protected Long value;

    public LongConstraint(Long value) {
        this.value = value;
    }

    @Override
    public boolean isMax(long max) {
        return value <= max;
    }

    @Override
    public boolean isMin(long min) {
        return value >= min;
    }

    @Override
    public boolean isDigits(int integer, int fractional) {
        BigDecimal bigDecimal = new BigDecimal(value).stripTrailingZeros();

        int integerLength = bigDecimal.precision() - bigDecimal.scale();
        int fractionLength = bigDecimal.scale() < 0 ? 0 : bigDecimal.scale();

        return integer >= integerLength && fractional >= fractionLength;
    }

    @Override
    public boolean isDecimalMax(int max, boolean inclusive) {
        if (inclusive) {
            return value <= max;
        } else {
            return value < max;
        }
    }

    @Override
    public boolean isDecimalMin(int min, boolean inclusive) {
        if (inclusive) {
            return value >= min;
        } else {
            return value > min;
        }
    }

    @Override
    public boolean isNegativeOrZero() {
        return value <= 0;
    }

    @Override
    public boolean isNegative() {
        return value < 0;
    }

    @Override
    public boolean isPositiveOrZero() {
        return value >= 0;
    }

    @Override
    public boolean isPositive() {
        return value > 0;
    }
}
