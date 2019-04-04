/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;

public class SizeValidator<T extends String> extends AbstractValidator<T> {

    protected int min;
    protected int max = Integer.MAX_VALUE;

    public SizeValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.sizeRange");
    }

    public SizeValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public SizeValidator<T> withMin(int min) {
        this.min = min;
        return this;
    }

    public int getMin() {
        return min;
    }

    public SizeValidator<T> withMax(int max) {
        this.max = max;
        return this;
    }

    public int getMax() {
        return max;
    }

    public SizeValidator<T> withSize(int min, int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider that null value is in range
        if (value != null) {
            if (min > value.length() || value.length() > max) {
                throw new ValidationException(String.format(getErrorMessage(), value, min, max));
            }
        }
    }
}
