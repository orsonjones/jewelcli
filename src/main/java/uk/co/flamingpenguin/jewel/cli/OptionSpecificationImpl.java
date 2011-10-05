/*
 * Copyright 2009 Tim Wood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.flamingpenguin.jewel.cli;

import java.lang.reflect.Method;
import java.util.List;

import com.lexicalscope.fluentreflection.ReflectedMethod;

class OptionSpecificationImpl implements OptionSpecification {
    private final OptionName m_optionName;
    private final OptionType m_optionType;
    private final OptionContext m_optionContext;
    private final Method m_method;
    private final ReflectedMethod optionalityMethod;

    OptionSpecificationImpl(
            final OptionName optionName,
            final OptionType optionType,
            final OptionContext optionContext,
            final Method method,
            final ReflectedMethod optionalityMethod) {
        m_optionName = optionName;
        m_optionType = optionType;
        m_optionContext = optionContext;
        m_method = method;
        this.optionalityMethod = optionalityMethod;
    }

    public List<String> getDefaultValue() {
        return m_optionContext.getDefaultValue();
    }

    public String getDescription() {
        return m_optionName.getDescription();
    }

    public String getLongName() {
        return m_optionName.getLongName();
    }

    public List<String> getShortNames() {
        return m_optionName.getShortNames();
    }

    public boolean hasDefaultValue() {
        return !getDefaultValue().isEmpty();
    }

    public boolean hasShortName() {
        return !getShortNames().isEmpty();
    }

    public boolean isHelpOption() {
        return m_optionContext.isHelpRequest();
    }

    public String getPattern() {
        return m_optionType.getPattern();
    }

    public Class<?> getType() {
        return m_optionType.getType();
    }

    public boolean hasValue() {
        return !isBoolean();
    }

    public boolean isMultiValued() {
        return m_optionType.isMultiValued();
    }

    public boolean isOptional() {
        return optionalityMethod != null || isBoolean();
    }

    private final boolean isBoolean() {
        return getType().isAssignableFrom(Boolean.class) || getType().isAssignableFrom(boolean.class);
    }

    Method getMethod() {
        return m_method;
    }

    ReflectedMethod getOptionalityMethod() {
        return optionalityMethod;
    }

    @Override public String toString() {
        return new OptionSummary(this).toString();
    }
}