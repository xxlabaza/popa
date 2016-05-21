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

import org.springframework.stereotype.Service;

import static ru.xxlabaza.popa.pack.ContentType.HTML;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
@Service
class CommentRemoverHtml extends AbstractCommentRemover {

    CommentRemoverHtml () {
        super(HTML, "<!--");
    }

    @Override
    protected int checkAndShift (char currentChar, char[] chars, int currentIndex) {
        for (int subIndex = currentIndex + 4; subIndex < chars.length; subIndex++) {
            if (chars[subIndex] == '-' && chars[subIndex + 1] == '-' && chars[subIndex + 2] == '>') {
                return subIndex + 2;
            }
        }
        return currentIndex;
    }

    @Override
    protected boolean isStartOfComment (char currentChar, char[] chars, int currentIndex) {
        return super.isStartOfComment(currentChar, chars, currentIndex) && chars[currentIndex + 4] != '[';
    }

    @Override
    protected boolean isStartOrEndOfString (char currentChar, char[] chars, int currentIndex) {
        return false;
    }
}
