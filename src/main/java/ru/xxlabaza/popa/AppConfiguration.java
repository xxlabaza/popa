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
package ru.xxlabaza.popa;

import static java.util.stream.Collectors.toMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.val;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import ru.xxlabaza.popa.command.Command;
import ru.xxlabaza.popa.command.CommandConfiguration;
import ru.xxlabaza.popa.pack.PackConfiguration;
import ru.xxlabaza.popa.template.TemplaterConfiguration;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
@Configuration
@Import({
    CommandConfiguration.class,
    PackConfiguration.class,
    TemplaterConfiguration.class
})
class AppConfiguration {

    @Bean
    public Map<Option, Command> commandsMap (List<Command> commands) {
        return commands.stream().collect(toMap(Command::getOption, Function.identity()));
    }

    @Bean
    public Options options (List<Command> commands) {
        val options = new Options();
        commands.stream().map(Command::getOption).forEach(options::addOption);
        return options;
    }

    @Bean
    @ConfigurationPropertiesBinding
    public StringToPathConverter stringToPathConverter () {
        return new StringToPathConverter();
    }

    @Bean
    AppProperties appProperties () {
        return new AppProperties();
    }

    static class StringToPathConverter implements Converter<String, Path> {

        @Override
        public Path convert (String source) {
            return Paths.get(source);
        }
    }
}
