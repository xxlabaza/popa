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

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.stereotype.Service;

import static freemarker.template.Configuration.VERSION_2_3_23;
import static freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 26.02.2016
 */
@Service
class TemplaterFreemarker extends AbstractTemplater {

    private Configuration freemarkerConfiguration;

    TemplaterFreemarker () {
        super("ftl");
    }

    @Override
    protected void lazyInitialization () throws Exception {
        freemarkerConfiguration = new Configuration(VERSION_2_3_23);
        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        freemarkerConfiguration.setTemplateExceptionHandler(RETHROW_HANDLER);
        freemarkerConfiguration.setDirectoryForTemplateLoading(appProperties.getFolder().getTemplates().toFile());
    }

    @Override
    protected void write (Path templatePath, Map<String, Object> mappings, Writer writer) throws Exception {
        Template template = freemarkerConfiguration.getTemplate(templatePath.toString());
        template.process(mappings, writer);
    }
}
