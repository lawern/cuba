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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.haulmont.cuba.gui.components.ValidationException;

import java.util.regex.Pattern;

public class RegexpValidator<T extends String> extends AbstractValidator<T> {

    protected Pattern pattern;

    public RegexpValidator(String regexp) {
        Preconditions.checkNotNull(regexp);

        this.errorMessage = messages.getMainMessage("validation.constraints.regexp");
        this.pattern = Pattern.compile(regexp);
    }

    public RegexpValidator(String regexp, String errorMessage) {
        Preconditions.checkNotNull(regexp);

        this.errorMessage = errorMessage;
        this.pattern = Pattern.compile(regexp);
    }

    @Override
    public void accept(T value) throws ValidationException {
        if (value == null || !pattern.matcher((value)).matches()) {
            throw new ValidationException(String.format(getErrorMessage(), Strings.nullToEmpty(value)));
        }
    }
}
