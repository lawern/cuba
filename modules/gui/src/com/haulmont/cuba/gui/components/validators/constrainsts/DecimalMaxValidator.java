/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.NumberConstraint;

import java.math.BigDecimal;

public class DecimalMaxValidator<T> extends AbstractValidator<T> {

    protected BigDecimal max = new BigDecimal(Long.MAX_VALUE);
    protected boolean inclusive = true;

    public DecimalMaxValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.decimalMax");
    }

    public DecimalMaxValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DecimalMaxValidator<T> withMax(String max) {
        this.max = new BigDecimal(max);
        return this;
    }

    public BigDecimal getMax() {
        return max;
    }

    public DecimalMaxValidator<T> withMax(String max, boolean inclusive) {
        this.max = new BigDecimal(max);
        this.inclusive = inclusive;
        return this;
    }

    public DecimalMaxValidator<T> withInclusive(boolean inclusive) {
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
            throw new IllegalArgumentException("DecimalMaxValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDecimalMax(max, inclusive)) {
            throw new ValidationException(String.format(getErrorMessage(), value, max));
        }
    }
}
