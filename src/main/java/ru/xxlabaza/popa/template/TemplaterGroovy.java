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

import groovy.text.Template;
import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;
import java.io.File;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2016
 */
@Service
class TemplaterGroovy extends AbstractTemplater {

    private MarkupTemplateEngine engine;

    private Path templatesFolder;

    TemplaterGroovy () {
        super("tpl");
    }

    @Override
    protected void lazyInitialization () throws Exception {
        TemplateConfiguration configuration = new TemplateConfiguration();
        configuration.setUseDoubleQuotes(true);
        configuration.setAutoIndent(true);
        configuration.setAutoNewLine(true);
        configuration.setLocale(Locale.UK);
        engine = new MarkupTemplateEngine(configuration);

        templatesFolder = appProperties.getFolder().getTemplates().toAbsolutePath();
    }

    @Override
    protected void write (Path templatePath, Map<String, Object> mappings, Writer writer) throws Exception {
        File templateFile = templatesFolder.resolve(templatePath).toFile();
        Template template = engine.createTemplate(templateFile);
        template.make(mappings).writeTo(writer);
    }
}
