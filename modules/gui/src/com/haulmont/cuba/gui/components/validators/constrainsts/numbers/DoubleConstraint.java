/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts.numbers;

public class DoubleConstraint implements NumberConstraint {

    protected Double value;

    public DoubleConstraint(Double value) {
        this.value = value;
    }

    @Override
    public boolean isMax(long max) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMin(long min) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDigits(int integer, int fractional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDecimalMax(int max, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDecimalMin(int min, boolean inclusive) {
        throw new UnsupportedOperationException();
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
