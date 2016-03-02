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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.pack.ContentType;
import ru.xxlabaza.popa.util.FileSystemUtils;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
@Service
public class CommentRemoveService {

    private final Map<ContentType, CommentRemover> commentRemovers;

    @Autowired
    public CommentRemoveService (List<CommentRemover> commentRemovers) {
        this.commentRemovers = commentRemovers.stream()
                .collect(Collectors.toMap(CommentRemover::getType, Function.identity()));
    }

    public String removeComments (Path path) {
        val content = FileSystemUtils.getContent(path);
        val fileExtension = FileSystemUtils.getExstension(path);
        val type = ContentType.parse(fileExtension);
        return removeComments(content, type);
    }

    public String removeComments (String content, ContentType type) {
        return commentRemovers.get(type).removeComments(content);
    }
}
