/*
 * Copyright (c) 2021 dzikoysk
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

package net.dzikoysk.cdn.module.json;

import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.module.standard.StandardModule;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.module.shared.ArrayValueVisitor;

/**
 * Implementation of JSON file format based on default implementation of CDN format.
 */
public final class JsonLikeModule extends StandardModule {

    private static final ArrayValueVisitor ARRAY_VALUE_VISITOR = new ArrayValueVisitor("", "");

    @Override
    public String convertToCdn(String source) {
        String standardized = source.replace("\r\n", StandardOperators.LINE_SEPARATOR);
        return new JsonToCdnConverter().enforceNewlines(standardized);
    }

    @Override
    public void visitDescription(StringBuilder output, String indentation, String description) {
        // drop comments
    }

    @Override
    public Element<?> visitArrayValue(Element<?> element) {
        return ARRAY_VALUE_VISITOR.visit(element);
    }

}
