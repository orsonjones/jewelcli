package com.lexicalscope.jewel.cli;

import com.lexicalscope.fluentreflection.FluentClass;
import com.lexicalscope.fluentreflection.FluentMethod;
import com.lexicalscope.jewel.cli.specification.ParsedOptionSpecification;
import com.lexicalscope.jewel.cli.specification.UnparsedOptionSpecification;

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

class AbstractConvertMethodToOptionSpecification {
    protected final FluentClass<?> klass;

    public AbstractConvertMethodToOptionSpecification(final FluentClass<?> klass) {
        this.klass = klass;
    }

    protected UnparsedOptionSpecification createUnparsedOptionSpecificationFrom(
            final FluentMethod method) {

        return new UnparsedOptionSpecificationImpl(new UnparsedAnnotationAdapter(klass, method, method.annotation(Unparsed.class)));
    }

    protected ParsedOptionSpecification createParsedOptionSpecificationFrom(final FluentMethod method) {
        return new ParsedOptionSpecificationImpl(new OptionAnnotationAdapter(klass, method));
    }
}
