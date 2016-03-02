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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.xxlabaza.popa.pack.ContentType;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
abstract class AbstractCommentRemover implements CommentRemover {

    private final Pattern pattern;

    private final ContentType type;

    AbstractCommentRemover (Pattern pattern, ContentType type) {
        this.pattern = pattern;
        this.type = type;
    }

    @Override
    public ContentType getType () {
        return type;
    }

    @Override
    public String removeComments (String content) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String token = matcher.group();
            if (token.startsWith("\"") || token.startsWith("'")) {
                continue;
            }

            content = content.replaceFirst(Pattern.quote(token), "");
        }
        return content;
    }
}
