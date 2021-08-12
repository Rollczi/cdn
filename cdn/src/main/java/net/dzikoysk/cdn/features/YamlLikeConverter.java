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

package net.dzikoysk.cdn.features;

import net.dzikoysk.cdn.CdnUtils;
import panda.utilities.StringUtils;

import static net.dzikoysk.cdn.CdnConstants.ARRAY;
import static net.dzikoysk.cdn.CdnConstants.LINE_SEPARATOR;
import static net.dzikoysk.cdn.CdnConstants.OBJECT_SEPARATOR;
import static net.dzikoysk.cdn.CdnConstants.OPERATOR;

final class YamlLikeConverter {

    private static final String OBJECT_IN_LIST = "- :";

    private final String[] lines;
    private final StringBuilder converted = new StringBuilder();
    private int previousIndentation = 0;

    YamlLikeConverter(String source) {
        this.lines = StringUtils.split(source.replace(System.lineSeparator(), LINE_SEPARATOR), LINE_SEPARATOR);
    }

    String convert() {
        for (String line : lines) {
            String indentation = StringUtils.extractParagraph(line);
            close(indentation.length());

            line = line.trim();
            boolean isArray = line.startsWith(ARRAY);

            if (isArray && line.endsWith(OBJECT_SEPARATOR[0])) {
                line = line.substring(1);
                line = line.startsWith(" ") ? line.substring(1) : line;
                line = "- " + CdnUtils.stringify(line);
            }

            if (line.endsWith(OPERATOR) && (!isArray || line.equals(OBJECT_IN_LIST))) {
                converted.append(indentation)
                        .append(line, 0, line.length() - 1)
                        .append(" {")
                        .append(LINE_SEPARATOR);
            }
            else {
                converted.append(indentation)
                        .append(line)
                        .append(LINE_SEPARATOR);
            }

            previousIndentation = indentation.length();
        }

        close(0);
        return converted.toString();
    }

    private void close(int toIndentation) {
        while (previousIndentation > toIndentation) {
            previousIndentation = previousIndentation - 2;

            converted.append(StringUtils.buildSpace(previousIndentation))
                    .append("}")
                    .append(LINE_SEPARATOR);
        }
    }

}
