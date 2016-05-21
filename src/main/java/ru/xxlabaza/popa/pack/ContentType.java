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
package ru.xxlabaza.popa.pack;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.val;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
public enum ContentType {

    HTML("html", "htm"),
    CSS("css"),
    JAVASCRIPT("js"),
    UNDEFINED;

    @Getter
    private final String[] extensions;

    private ContentType (String... extensions) {
        this.extensions = extensions;
    }

    public static ContentType parse (String extension) {
        if (extension == null) {
            return UNDEFINED;
        }
        val preparedExtension = extension.trim().toLowerCase();
        if (preparedExtension.isEmpty()) {
            return UNDEFINED;
        }

        for (ContentType type : values()) {
            if (Stream.of(type.getExtensions()).anyMatch(preparedExtension::equals)) {
                return type;
            }
        }
        return UNDEFINED;
    }
}
