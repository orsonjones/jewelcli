/*
 * Copyright 2006 Tim Wood
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

package com.lexicalscope.jewel.cli.validation;

import static com.lexicalscope.fluent.FluentDollar.$;
import static com.lexicalscope.jewel.cli.specification.OptionSpecificationMatchers.mandatory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lexicalscope.fluent.map.FluentMap;
import com.lexicalscope.jewel.cli.ValidationErrorBuilder;
import com.lexicalscope.jewel.cli.specification.OptionSpecification;
import com.lexicalscope.jewel.cli.specification.OptionsSpecification;
import com.lexicalscope.jewel.cli.specification.ParsedOptionSpecification;

class ArgumentValidatorImpl<O> implements ArgumentValidator
{
    private final ValidationErrorBuilder validationErrorBuilder;

    private final Map<RawOption, List<String>> rawArguments;
    private final FluentMap<ParsedOptionSpecification, List<String>> arguments = $.<ParsedOptionSpecification, List<String>>map();
    private final FluentMap<ParsedOptionSpecification, List<String>> mandatoryArguments = $(arguments).$retainKeys(mandatory());

    private final List<String> validatedUnparsedArguments = new ArrayList<String>();

    private final OptionsSpecification<O> specification;

    public ArgumentValidatorImpl(
            final OptionsSpecification<O> specification,
            final ValidationErrorBuilder validationErrorBuilder)
    {
        this.specification = specification;
        this.validationErrorBuilder = validationErrorBuilder;

        rawArguments = new ValidationPipeline(specification, validationErrorBuilder).buildValidationPipeline(validatedUnparsedArguments).outputTo(arguments);
    }

    @Override public void processOption(final String optionName, final List<String> values) {
        rawArguments.put(new RawOption(optionName), values);
    }

    @Override public void processLastOption(final String optionName, final List<String> values) {
        rawArguments.put(new RawOption(optionName, true), values);
    }

    @Override public void processUnparsed(final List<String> values) {
        validatedUnparsedArguments.addAll(values);
    }

    @Override public OptionCollection finishedProcessing() {
        validateUnparsedOptions();
        validationErrorBuilder.validate();

        specification.
           getMandatoryOptions().
              _withoutKeys(mandatoryArguments).
              _forEach(ParsedOptionSpecification.class).
              reportMissingTo(validationErrorBuilder);

        validationErrorBuilder.validate();

        return new OptionCollectionImpl(specification, arguments, validatedUnparsedArguments);
    }

    private void validateUnparsedOptions()
    {
        if (specification.hasUnparsedSpecification())
        {
            final OptionSpecification unparsedSpecification = specification.getUnparsedSpecification();

            if (unparsedSpecification.isOptional() && validatedUnparsedArguments.isEmpty())
            {
                // OK
            }
            else if (!unparsedSpecification.allowedThisManyValues(validatedUnparsedArguments.size()))
            {
                validationErrorBuilder.wrongNumberOfValues(unparsedSpecification, validatedUnparsedArguments);
            }
        }
        else if (!validatedUnparsedArguments.isEmpty())
        {
            validationErrorBuilder.unexpectedTrailingValue(validatedUnparsedArguments);
        }
    }
}
