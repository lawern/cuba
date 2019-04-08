/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.NumberConstraint;

import java.math.BigDecimal;

public class DecimalMinValidator<T> extends AbstractValidator<T> {

    protected BigDecimal min = new BigDecimal(Long.MIN_VALUE);
    protected boolean inclusive = true;

    public DecimalMinValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.decimalMin");
    }

    public DecimalMinValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DecimalMinValidator<T> withMin(String min) {
        this.min = new BigDecimal(min);
        return this;
    }

    public BigDecimal getMin() {
        return min;
    }

    public DecimalMinValidator<T> withMin(String min, boolean inclusive) {
        this.min = new BigDecimal(min);
        this.inclusive = inclusive;
        return this;
    }

    public DecimalMinValidator<T> withInclusive(boolean inclusive) {
        this.inclusive = inclusive;
        return this;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    @Override
    public void accept(T value) {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = null;

        if (value instanceof Number) {
            constraint = getNumberConstraint((Number) value);
        } else if (value instanceof String) {
            constraint = getNumberConstraint(((String) value).length());
        }

        if (constraint == null || value instanceof Double) {
            throw new IllegalArgumentException("DecimalMinValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDecimalMin(min, inclusive)) {
            throw new ValidationException(String.format(getErrorMessage(), value, min));
        }
    }
}
