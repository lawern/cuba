/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.NumberConstraint;

public class MinValidator<T extends Number> extends AbstractValidator<T> {

    protected long min;

    public MinValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.min");
    }

    public MinValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MinValidator<T> withMin(long min) {
        this.min = min;
        return this;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null || value instanceof Double) {
            throw new IllegalArgumentException("MinValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isMin(min)) {
            throw new ValidationException(String.format(getErrorMessage(), value, min));
        }
    }
}
