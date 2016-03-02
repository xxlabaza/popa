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
package ru.xxlabaza.popa.template;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2016
 */
@Getter
@AllArgsConstructor
public class FileDescriptor {

    private final static Pattern CONTENT_FILE_META_DELIMITER;

    private final static String CONTENT_KEY_NAME;

    private final static String TEMPLATE_KEY_NAME;

    static {
        CONTENT_FILE_META_DELIMITER = Pattern.compile("^~+$");
        TEMPLATE_KEY_NAME = "template";
        CONTENT_KEY_NAME = "content";
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static FileDescriptor load (Path path) {
        Map<String, Object> bindings = new HashMap<>();
        StringBuilder contentBuffer = new StringBuilder();

        Files.lines(path).forEachOrdered(line -> {
            if (!CONTENT_FILE_META_DELIMITER.matcher(line).matches()) {
                contentBuffer.append(line).append('\n');
            } else {
                bindings.putAll((Map<String, Object>) new Yaml().load(contentBuffer.toString()));
                contentBuffer.setLength(0); // .clear()
            }
        });

        bindings.put(CONTENT_KEY_NAME, contentBuffer.toString().trim());
        return new FileDescriptor(bindings, path.getFileName().toString());
    }

    private final Map<String, Object> bindings;

    private final String name;

    String getContent () {
        return bindings.get(CONTENT_KEY_NAME).toString();
    }

    String getTemplate () {
        return bindings.get(TEMPLATE_KEY_NAME).toString();
    }

    boolean hasTemplate () {
        return bindings.containsKey(TEMPLATE_KEY_NAME);
    }
}
