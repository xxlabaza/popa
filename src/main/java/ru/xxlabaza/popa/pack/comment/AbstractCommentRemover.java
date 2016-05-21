/*
 * Copyright 2016 Artem Labazin <xxlabaza@gmail.com>.
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
package ru.xxlabaza.popa.pack.comment;

import java.util.stream.Stream;
import ru.xxlabaza.popa.pack.ContentType;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
abstract class AbstractCommentRemover implements CommentRemover {

    private final ContentType type;

    private final String[] startOfComment;

    AbstractCommentRemover (ContentType type, String... startOfComment) {
        this.type = type;
        this.startOfComment = startOfComment;
    }

    protected abstract int checkAndShift (char currentChar, char[] chars, int currentIndex);

    @Override
    public String removeComments (String content) {
        StringBuilder result = new StringBuilder(content.length());

        char[] chars = content.toCharArray();
        char currentChar;
        boolean insideString = false;
        char startStringChar = ' ';
        boolean insideRegex = false;

        for (int index = 0; index < chars.length; index++) {
            currentChar = chars[index];

            if (!insideRegex && isStartOrEndOfString(currentChar, chars, index)) {
                if (startStringChar == currentChar) {
                    insideString = false;
                    startStringChar = ' ';
                } else if (!insideString) {
                    insideString = true;
                    startStringChar = currentChar;
                }
            } else if (!insideString && !insideRegex && isStartOfComment(currentChar, chars, index)) {
                int newIndex = checkAndShift(currentChar, chars, index);
                if (newIndex != index) {
                    index = newIndex;
                    continue;
                }
            }

            result.append(currentChar);
        }

        return result.toString();
    }

    protected boolean isStartOrEndOfString (char currentChar, char[] chars, int currentIndex) {
        return (currentIndex == 0 || chars[currentIndex - 1] != '\\' || so(chars, currentIndex)) &&
               (currentChar == '"' || currentChar == '\'');
    }

    protected boolean isStartOfComment (char currentChar, char[] chars, int currentIndex) {
        if (startOfComment == null || (currentIndex != 0 && chars[currentIndex - 1] == '\\')) {
            return false;
        }

        return Stream.of(startOfComment).anyMatch(it -> {
            if (chars.length - currentIndex < it.length()) {
                return false;
            }

            for (int index = 0; index < it.length(); index++) {
                if (it.charAt(index) != chars[currentIndex + index]) {
                    return false;
                }
            }
            return true;
        });
    }

    @Override
    public ContentType getType () {
        return type;
    }

    private boolean so (char[] chars, int currentIndex) {
        for (int count = 1; count <= currentIndex; count++) {
            if (chars[currentIndex - count] != '\\') {
                return (count - 1) % 2 == 0;
            }
        }
        return false;
    }
}
