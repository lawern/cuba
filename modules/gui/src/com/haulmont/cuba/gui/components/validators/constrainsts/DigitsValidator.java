/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.NumberConstraint;

import java.math.BigDecimal;

public class DigitsValidator<T> extends AbstractValidator<T> {

    protected int integer;
    protected int fraction;

    public DigitsValidator(int integer, int fraction) {
        this.errorMessage = messages.getMainMessage("validation.constraints.digits");
        this.integer = integer;
        this.fraction = fraction;
    }

    public DigitsValidator(int integer, int fraction, String errorMessage) {
        this.errorMessage = errorMessage;
        this.integer = integer;
        this.fraction = fraction;
    }

    public DigitsValidator<T> withIntger(int integer) {
        this.integer = integer;
        return this;
    }

    public DigitsValidator<T> withFraction(int fraction) {
        this.fraction = fraction;
        return this;
    }

    public int getIntger() {
        return integer;
    }

    public int getFraction() {
        return fraction;
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
            try {
                constraint = getNumberConstraint(new BigDecimal((String) value));
            } catch (NumberFormatException e) {
                throw new ValidationException(messages.formatMainMessage("validation.constraints.digits.wrongString", value));
            }
        }

        if (constraint == null || value instanceof Double) {
            throw new IllegalArgumentException("DigitsValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDigits(integer, fraction)) {
            throw new ValidationException(String.format(getErrorMessage(), value, integer, fraction));
        }
    }
}
