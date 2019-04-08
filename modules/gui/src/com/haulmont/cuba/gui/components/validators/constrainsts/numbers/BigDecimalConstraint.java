/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts.numbers;

import java.math.BigDecimal;

public class BigDecimalConstraint implements NumberConstraint {

    protected BigDecimal value;

    public BigDecimalConstraint(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean isMax(long max) {
        return compareValueWith(max) <= 0;
    }

    @Override
    public boolean isMin(long min) {
        return compareValueWith(min) >= 0;
    }

    @Override
    public boolean isDigits(int integer, int fractional) {
        BigDecimal bigDecimal = value.stripTrailingZeros();

        int integerLength = bigDecimal.precision() - bigDecimal.scale();
        int fractionLength = bigDecimal.scale() < 0 ? 0 : bigDecimal.scale();

        return integer >= integerLength && fractional >= fractionLength;
    }

    @Override
    public boolean isDecimalMax(int max, boolean inclusive) {
        if (inclusive) {
            return compareValueWith(max) <= 0;
        } else {
            return compareValueWith(max) < 0;
        }
    }

    @Override
    public boolean isDecimalMin(int min, boolean inclusive) {
        return false;
    }

    @Override
    public boolean isNegativeOrZero() {
        return value.signum() <= 0;
    }

    @Override
    public boolean isNegative() {
        return value.signum() < 0;
    }

    @Override
    public boolean isPositiveOrZero() {
        return value.signum() >= 0;
    }

    @Override
    public boolean isPositive() {
        return value.signum() > 0;
    }

    protected int compareValueWith(long val) {
        return this.value.compareTo(BigDecimal.valueOf(val));
    }
}
