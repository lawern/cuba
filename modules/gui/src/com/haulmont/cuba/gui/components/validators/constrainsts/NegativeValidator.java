/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.tools.NumberConstraint;

public class NegativeValidator<T extends Number> extends AbstractValidator<T> {

    public NegativeValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.negative");
    }

    public NegativeValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null) {
            throw new IllegalArgumentException("NegativeValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isNegative()) {
            throw new ValidationException(String.format(getErrorMessage(), value));
        }
    }
}
