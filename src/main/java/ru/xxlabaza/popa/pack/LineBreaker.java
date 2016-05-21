/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.xxlabaza.popa.pack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 09.03.2016
 */
@Service
class LineBreaker {

    @Value("${app.style.lineBreak}")
    private int lineBreak;

    public String lineBreak (String content) {
        StringBuilder sb = new StringBuilder(content.length() + 100);
        boolean insideString = false;
        char startStringChar = ' ';

        for (String row : content.split("\n")) {
            char[] chars = row.toCharArray();

            int from = getFirstNotSpaceCharacterIndex(chars, 0);
            if (from == -1) {
                sb.append('\n');
                continue;
            }

            int count = 0;

            for (int index = from; index < chars.length; index++) {
                char currentChar = chars[index];
                sb.append(currentChar);

                if ((index == 0 || chars[index - 1] != '\\') &&
                    (currentChar == '"' || currentChar == '\'')) {
                    if (startStringChar == currentChar) {
                        insideString = false;
                        startStringChar = ' ';
                    } else if (!insideString) {
                        insideString = true;
                        startStringChar = currentChar;
                    }
                } else if (!insideString && (currentChar == ';' || currentChar == ',') && count >= lineBreak) {
                    sb.append('\n');
                    index = getFirstNotSpaceCharacterIndex(chars, index + 1) - 1;
                    if (index < 0) {
                        break;
                    }
                    count = 0;
                } else {
                    count++;
                }
            }
        }
        return sb.toString();
    }

    private int getFirstNotSpaceCharacterIndex (char[] chars, int currentIndex) {
        for (int index = currentIndex; index < chars.length; index++) {
            if (!Character.isSpaceChar(chars[index])) {
                return index;
            }
        }
        return -1;
    }

    private boolean isLineBreaker (char currentChar, char[] chars, int currentIndex) {
        if (currentChar == ';' || currentChar == ',') {
            return true;
        }
//        String[] lineBreakerWords = {
//            "function",
//            "do",
//            "while",
//            "for",
//            "switch",
//            "/",
//            "|",
//            "*",
//            ":",
//            "+",
//            "-"
//        };

        return false;
    }
}
