/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.NumberConstraint;

public class MaxValidator<T extends Number> extends AbstractValidator<T> {

    protected long max = Long.MAX_VALUE;

    public MaxValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.max");
    }

    public MaxValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MaxValidator<T> withMax(long max) {
        checkPositiveValue(max, "Max value cannot be less then 0");

        this.max = max;
        return this;
    }

    public Long getMax() {
        return max;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null || value instanceof Double) {
            throw new IllegalArgumentException("MaxValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isMax(max)) {
            throw new ValidationException(String.format(getErrorMessage(), value, max));
        }
    }
}
