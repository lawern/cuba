/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */
package com.haulmont.cuba.gui.xml.layout.loaders;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.Buffered;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.HasDatatype;
import com.haulmont.cuba.gui.components.validators.constrainsts.*;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;

public abstract class AbstractFieldLoader<T extends Field> extends AbstractDatasourceComponentLoader<T> {

    @Override
    public void loadComponent() {
        assignFrame(resultComponent);
        assignXmlDescriptor(resultComponent, element);

        loadContainer(resultComponent, element);
        if (resultComponent.getValueSource() == null) {
            loadDatasource(resultComponent, element);
        }

        loadVisible(resultComponent, element);
        loadEditable(resultComponent, element);
        loadEnable(resultComponent, element);

        loadStyleName(resultComponent, element);

        loadCaption(resultComponent, element);
        loadIcon(resultComponent, element);
        loadDescription(resultComponent, element);
        loadContextHelp(resultComponent, element);

        loadValidators(resultComponent, element);
        loadConstraints(resultComponent, element);

        loadRequired(resultComponent, element);

        loadHeight(resultComponent, element);
        loadWidth(resultComponent, element);
        loadAlign(resultComponent, element);
        loadResponsive(resultComponent, element);
        loadCss(resultComponent, element);
    }

    protected void loadRequired(Field component, Element element) {
        String required = element.attributeValue("required");
        if (StringUtils.isNotEmpty(required)) {
            component.setRequired(Boolean.parseBoolean(required));
        }

        String requiredMessage = element.attributeValue("requiredMessage");
        if (requiredMessage != null) {
            component.setRequiredMessage(loadResourceString(requiredMessage));
        }
    }

    protected void loadValidators(Field component, Element element) {
        List<Element> validatorElements = element.elements("validator");

        if (!validatorElements.isEmpty()) {
            for (Element validatorElement : validatorElements) {
                Field.Validator validator = loadValidator(validatorElement);
                if (validator != null) {
                    component.addValidator(validator);
                }
            }

        } else if (component.getDatasource() != null) {
            MetaProperty property = component.getMetaProperty();
            Field.Validator validator = getDefaultValidator(property);
            if (validator != null) {
                component.addValidator(validator);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void loadConstraints(Field component, Element element) {
        Element constraints = element.element("constraints");
        if (constraints != null) {
            Element notEmptyElement = constraints.element("notEmpty");
            if (notEmptyElement != null) {
                NotEmptyValidator notEmptyValidator = new NotEmptyValidator();
                loadValidatorMessage(notEmptyValidator, notEmptyElement);
                component.addValidator(notEmptyValidator);
            }

            Element notBlankElement = constraints.element("notBlank");
            if (notBlankElement != null) {
                NotBlankValidator notBlankValidator = new NotBlankValidator<>();
                loadValidatorMessage(notBlankValidator, notBlankElement);
                component.addValidator(notBlankValidator);
            }

            Element regexpElement = constraints.element("regexp");
            if (regexpElement != null) {
                String regexp = regexpElement.attributeValue("regexp");
                RegexpValidator regexpValidator = new RegexpValidator<>(regexp);
                loadValidatorMessage(regexpValidator, regexpElement);
                component.addValidator(regexpValidator);
            }

            Element sizeElement = constraints.element("size");
            if (sizeElement != null) {
                SizeValidator sizeValidator = new SizeValidator();

                String min = sizeElement.attributeValue("min");
                if (min != null) {
                    int minValue = Integer.parseInt(min);
                    if (minValue < 0) {
                        throw new GuiDevelopmentException("Min value must be greater or equal to 0",
                                context.getFullFrameId());
                    }
                    sizeValidator.withMin(minValue);
                }

                String max = sizeElement.attributeValue("max");
                if (max != null) {
                    int maxValue = Integer.parseInt(max);
                    if (maxValue < 0) {
                        throw new GuiDevelopmentException("Max value must be greater or equal to 0",
                                context.getFullFrameId());
                    }
                    sizeValidator.withMax(maxValue);
                }

                loadValidatorMessage(sizeValidator, sizeElement);
                component.addValidator(sizeValidator);
            }

            Element mustBeNullElement = constraints.element("mustBeNull");
            if (mustBeNullElement != null) {
                NullValidator nullValidator = new NullValidator<>();
                loadValidatorMessage(nullValidator, mustBeNullElement);
                component.addValidator(nullValidator);
            }

            Element mustBeNotNullElement = constraints.element("mustBeNotNull");
            if (mustBeNotNullElement != null) {
                NotNullValidator notNullValidator = new NotNullValidator<>();
                loadValidatorMessage(notNullValidator, mustBeNotNullElement);
                component.addValidator(notNullValidator);
            }

            Element negativeOrZeroElement = constraints.element("negativeOrZero");
            if (negativeOrZeroElement != null) {
                NegativeOrZeroValidator negativeOrZeroValidator = new NegativeOrZeroValidator<>();
                loadValidatorMessage(negativeOrZeroValidator, negativeOrZeroElement);
                component.addValidator(negativeOrZeroValidator);
            }

            Element negativeElement = constraints.element("negative");
            if (negativeElement != null) {
                NegativeValidator negativeValidator = new NegativeValidator<>();
                loadValidatorMessage(negativeValidator, negativeElement);
                component.addValidator(negativeValidator);
            }

            Element positiveOrZeroElement = constraints.element("positiveOrZero");
            if (positiveOrZeroElement != null) {
                PositiveOrZeroValidator positiveOrZeroValidator = new PositiveOrZeroValidator<>();
                loadValidatorMessage(positiveOrZeroValidator, positiveOrZeroElement);
                component.addValidator(positiveOrZeroValidator);
            }

            Element positiveElement = constraints.element("positive");
            if (positiveElement != null) {
                PositiveValidator positiveValidator = new PositiveValidator<>();
                loadValidatorMessage(positiveValidator, positiveElement);
                component.addValidator(positiveValidator);
            }

            Element maxElement = constraints.element("max");
            if (maxElement != null) {
                MaxValidator maxValidator = new MaxValidator<>();
                loadValidatorMessage(maxValidator, maxElement);

                String max = maxElement.attributeValue("value");
                if (max != null) {
                    int maxValue = Integer.parseInt(max);
                    maxValidator.withMax(maxValue);
                }
                component.addValidator(maxValidator);
            }

            Element minElement = constraints.element("min");
            if (minElement != null) {
                MinValidator minValidator = new MinValidator<>();
                loadValidatorMessage(minValidator, minElement);

                String min = minElement.attributeValue("value");
                if (min != null) {
                    int minValue = Integer.parseInt(min);
                    minValidator.withMin(minValue);
                }
                component.addValidator(minValidator);
            }

            Element decimalMinElement = constraints.element("decimalMin");
            if (decimalMinElement != null) {
                DecimalMinValidator decimalMinValidator = new DecimalMinValidator<>();
                loadValidatorMessage(decimalMinValidator, decimalMinElement);

                String decimalMin = decimalMinElement.attributeValue("value");
                if (StringUtils.isNotBlank(decimalMin)) {
                    decimalMinValidator.withMin(decimalMin);
                }

                String inclusive = decimalMinElement.attributeValue("inclusive");
                if (StringUtils.isNotBlank(inclusive)) {
                    decimalMinValidator.withInclusive(Boolean.parseBoolean(inclusive));
                }
                component.addValidator(decimalMinValidator);
            }

            Element decimalMaxElement = constraints.element("decimalMax");
            if (decimalMaxElement != null) {
                DecimalMaxValidator decimalMaxValidator = new DecimalMaxValidator<>();
                loadValidatorMessage(decimalMaxValidator, decimalMaxElement);

                String decimalMax = decimalMaxElement.attributeValue("value");
                if (StringUtils.isNotBlank(decimalMax)) {
                    decimalMaxValidator.withMax(decimalMax);
                }

                String inclusive = decimalMaxElement.attributeValue("inclusive");
                if (StringUtils.isNotBlank(inclusive)) {
                    decimalMaxValidator.withInclusive(Boolean.parseBoolean(inclusive));
                }
                component.addValidator(decimalMaxValidator);
            }

            Element digitsElement = constraints.element("digits");
            if (digitsElement != null) {
                DigitsValidator digitsValidator;

                String integer = digitsElement.attributeValue("integer");
                String fraction = digitsElement.attributeValue("fraction");
                if (StringUtils.isNotBlank(integer) || StringUtils.isNotBlank(fraction)) {
                    digitsValidator = new DigitsValidator<>(Integer.parseInt(integer), Integer.parseInt(fraction));
                } else {
                    throw new GuiDevelopmentException("'integer' and 'fraction' properties are required", context.getFullFrameId());
                }

                loadValidatorMessage(digitsValidator, digitsElement);
                component.addValidator(digitsValidator);
            }
        }
    }

    protected void loadValidatorMessage(AbstractValidator validator, Element element) {
        String message = element.attributeValue("message");
        if (message != null) {
            validator.setErrorMessage(loadResourceString(message));
        }
    }

    protected void loadBuffered(Buffered component, Element element) {
        String buffered = element.attributeValue("buffered");
        if (StringUtils.isNotEmpty(buffered)) {
            component.setBuffered(Boolean.parseBoolean(buffered));
        }
    }

    protected void loadDatatype(HasDatatype component, Element element) {
        String datatypeAttribute = element.attributeValue("datatype");
        if (StringUtils.isNotEmpty(datatypeAttribute)) {
            //noinspection unchecked
            component.setDatatype(Datatypes.get(datatypeAttribute));
        }
    }
}