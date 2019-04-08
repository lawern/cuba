/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts.numbers;

import java.math.BigDecimal;

public interface NumberConstraint {
    boolean isMax(long max);

    boolean isMin(long min);

    boolean isDigits(int integer, int fraction);

    boolean isDecimalMax(BigDecimal max, boolean inclusive);

    boolean isDecimalMin(BigDecimal min, boolean inclusive);

    boolean isNegativeOrZero();

    boolean isNegative();

    boolean isPositiveOrZero();

    boolean isPositive();
}
