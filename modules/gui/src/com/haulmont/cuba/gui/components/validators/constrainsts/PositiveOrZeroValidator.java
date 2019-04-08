/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.NumberConstraint;

public class PositiveOrZeroValidator<T extends Number> extends AbstractValidator<T> {

    public PositiveOrZeroValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.positiveOrZero");
    }

    public PositiveOrZeroValidator(String messageError) {
        this.errorMessage = messageError;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null) {
            throw new IllegalArgumentException("PositiveOrZeroValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isPositiveOrZero()) {
            throw new ValidationException(String.format(getErrorMessage(), value));
        }
    }
}
