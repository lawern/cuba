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
            Element notEmpty = constraints.element("notEmpty");
            if (notEmpty != null) {
                NotEmptyValidator notEmptyValidator = new NotEmptyValidator();
                String message = notEmpty.attributeValue("message");
                if (message != null) {
                    notEmptyValidator.setErrorMessage(loadResourceString(message));
                }

                component.addValidator(notEmptyValidator);
            }

            Element notBlank = constraints.element("notBlank");
            if (notBlank != null) {
                NotBlankValidator notBlankValidator = new NotBlankValidator<>();
                String message = notBlank.attributeValue("message");
                if (message != null) {
                    notBlankValidator.setErrorMessage(loadResourceString(message));
                }

                component.addValidator(notBlankValidator);
            }

            Element regexpElement = constraints.element("regexp");
            if (regexpElement != null) {
                String regexp = regexpElement.attributeValue("regexp");
                RegexpValidator regexpValidator = new RegexpValidator<>(regexp);

                String message = regexpElement.attributeValue("message");
                if (message != null) {
                    regexpValidator.setErrorMessage(loadResourceString(message));
                }

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

                String message = sizeElement.attributeValue("message");
                if (message != null) {
                    sizeValidator.setErrorMessage(loadResourceString(message));
                }

                component.addValidator(sizeValidator);
            }

            Element mustBeNullElement = constraints.element("mustBeNull");
            if (mustBeNullElement != null) {
                NullValidator nullValidator = new NullValidator<>();
                String message = mustBeNullElement.attributeValue("message");
                if (message != null) {
                    nullValidator.setErrorMessage(loadResourceString(message));
                }

                component.addValidator(nullValidator);
            }

            Element mustBeNotNullElement = constraints.element("mustBeNotNull");
            if (mustBeNotNullElement != null) {
                NotNullValidator notNullValidator = new NotNullValidator<>();
                String message = mustBeNotNullElement.attributeValue("message");
                if (message != null) {
                    notNullValidator.setErrorMessage(loadResourceString(message));
                }

                component.addValidator(notNullValidator);
            }

            Element negativeOrZeroElement = constraints.element("negativeOrZero");
            if (negativeOrZeroElement != null) {
                NegativeOrZeroValidator negativeOrZeroValidator = new NegativeOrZeroValidator<>();
                String message = negativeOrZeroElement.attributeValue("message");
                if (message != null) {
                    negativeOrZeroValidator.setErrorMessage(loadResourceString(message));
                }

                component.addValidator(negativeOrZeroValidator);
            }
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