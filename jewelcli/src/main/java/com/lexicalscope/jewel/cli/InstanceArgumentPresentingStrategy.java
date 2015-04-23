package com.lexicalscope.jewel.cli;

import static com.lexicalscope.fluentreflection.ReflectionMatchers.annotatedWith;

import java.util.Map;

import com.lexicalscope.fluentreflection.FluentClass;
import com.lexicalscope.fluentreflection.FluentMethod;
import com.lexicalscope.fluentreflection.InvocationTargetRuntimeException;
import com.lexicalscope.jewel.cli.specification.OptionsSpecification;
import com.lexicalscope.jewel.cli.specification.ParsedOptionSpecification;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2011 Tim Wood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class InstanceArgumentPresentingStrategy<O> implements ArgumentPresentingStrategy<O> {
    private final O options;
    private final FluentClass<O> klass;
    private final OptionsSpecification<O> specification;

    public InstanceArgumentPresentingStrategy(
            final OptionsSpecification<O> specification,
            final FluentClass<O> klass,
            final O options) {
        this.specification = specification;
        this.klass = klass;
        this.options = options;
    }

    @Override public O presentArguments(final Map<String, Object> argumentMap) {
        List<ValidationFailure> errors = new ArrayList<ValidationFailure>();
        for (final FluentMethod reflectedMethod : klass.methods(annotatedWith(Option.class))) {
            final boolean isBoolean = specification.getSpecification(reflectedMethod).isBoolean();
            setValueOnOptions(argumentMap, reflectedMethod, isBoolean, errors);
        }
        for (final FluentMethod reflectedMethod : klass.methods(annotatedWith(Unparsed.class))) {
            setValueOnOptions(argumentMap, reflectedMethod, false, errors);
        }
        if (!errors.isEmpty())
            throw new ArgumentValidationException(errors);
        return options;
    }

    private void setValueOnOptions(
            final Map<String, Object> argumentMap,
            final FluentMethod reflectedMethod,
            final boolean isBoolean,
            final List<ValidationFailure> errors) {
        try {
            if (argumentMap.containsKey(reflectedMethod.property()))
            {
                if (isBoolean)
                {
                    reflectedMethod.call(options, argumentMap.containsKey(reflectedMethod.property()));
                }
                else if (argumentMap.get(reflectedMethod.property()) != null)
                {
                    reflectedMethod.call(options, argumentMap.get(reflectedMethod.property()));
                }
            }
        } catch (InvocationTargetRuntimeException ex) {
            String message = ex.getExceptionThrownByInvocationTarget().getMessage();
            ParsedOptionSpecification optionSpecification = specification.getSpecification(reflectedMethod);
            ValidationFailure validationFailure = new ValidationFailureUnableToConstructType(optionSpecification, message);
            errors.add(validationFailure);
        }
    }
}
