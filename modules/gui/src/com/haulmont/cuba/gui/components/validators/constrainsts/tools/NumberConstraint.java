/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts.tools;

public interface NumberConstraint {
    boolean isMax(long max);

    boolean isMin(long min);

    boolean isDigits(int integer, int fractional);

    boolean isDecimalMax(int max, boolean inclusive);

    boolean isDecimalMin(int min, boolean inclusive);

    boolean isNegativeOrZero();

    boolean isNegative();

    boolean isPositiveOrZero();

    boolean isPositive();
}
