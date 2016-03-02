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

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import ru.xxlabaza.popa.AppProperties;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2016
 */
abstract class AbstractTemplater implements Templater {

    @Autowired
    protected AppProperties appProperties;

    private final String extension;

    private boolean isInitialized;

    AbstractTemplater (String extension) {
        this.extension = extension;
    }

    @Override
    @SneakyThrows
    public String build (Path templatePath, Map<String, Object> mappings) {
        if (!isInitialized) {
            lazyInitialization();
            isInitialized = true;
        }
        Writer stringWriter = new StringWriter();
        write(templatePath, mappings, stringWriter);
        return stringWriter.toString();
    }

    @Override
    public String getSupportedFileExtension () {
        return extension;
    }

    protected abstract void lazyInitialization () throws Exception;

    protected abstract void write (Path templatePath, Map<String, Object> mappings, Writer writer) throws Exception;
}
