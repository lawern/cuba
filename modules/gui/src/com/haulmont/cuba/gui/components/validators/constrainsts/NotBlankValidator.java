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

public class NotBlankValidator<T extends String> extends AbstractValidator<T> {

    public NotBlankValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.notBlank");
    }

    public NotBlankValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        if (value == null || value.trim().length() == 0) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
