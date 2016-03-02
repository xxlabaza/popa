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

import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.AppProperties;
import ru.xxlabaza.popa.util.FileSystemUtils;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 27.02.2016
 */
@Slf4j
@Service
public class TemplaterFacade {

    private final String extensionsPattern;

    private final Map<String, Templater> templaters;

    private final Path templatesFolder;

    @Autowired
    public TemplaterFacade (AppProperties appProperties, List<Templater> templaters) {
        templatesFolder = appProperties.getFolder().getTemplates().toAbsolutePath();
        this.templaters = templaters.stream().collect(toMap(Templater::getSupportedFileExtension, Function.identity()));
        extensionsPattern = this.templaters.keySet().stream().collect(joining(",", ".{", "}"));
    }

    @SneakyThrows
    public String build (String templateName, Map<String, Object> bindings) {
        log.info("Template: '{}', bindings: {}", templateName, bindingsToString(bindings));
        val pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + templateName + extensionsPattern);
        val templatePath = Files.list(templatesFolder)
                .filter(it -> pathMatcher.matches(it.getFileName()))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException(String.format("Template '%s' not found", templateName)))
                .toAbsolutePath();
        val templater = templaters.get(FileSystemUtils.getExstension(templatePath));
        return templater.build(templatesFolder.relativize(templatePath), bindings);
    }

    public String process (Path path) {
        val fileDescriptor = FileDescriptor.load(path);
        return fileDescriptor.hasTemplate()
               ? build(fileDescriptor.getTemplate(), fileDescriptor.getBindings())
               : fileDescriptor.getContent();
    }

    private String bindingsToString (Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        return map.entrySet().stream()
                .map(entry -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(entry.getKey()).append('=');

                    Object value = entry.getValue();
                    if (value == null) {
                        sb.append("");
                    } else {
                        String valueString = value.toString().replaceAll("\\r\\n|\\r|\\n", " ");
                        if (sb.length() + valueString.length() > 80) {
                            sb.append(valueString.substring(0, 80 - (sb.length() + 5))).append("...");
                        } else {
                            sb.append(valueString);
                        }
                    }
                    return sb.toString();
                })
                .collect(Collectors.joining(",\n  ", "\n{\n  ", "\n}"));
    }
}
