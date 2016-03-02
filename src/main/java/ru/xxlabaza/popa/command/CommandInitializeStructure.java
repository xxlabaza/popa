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
package ru.xxlabaza.popa.command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.AppProperties;
import ru.xxlabaza.popa.AppProperties.Folder;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 25.02.2016
 */
@Slf4j
@Service
class CommandInitializeStructure extends AbstractCommand {

    @Autowired
    private AppProperties appProperties;

    @Override
    public void execute (String[] args) {
        Folder folder = appProperties.getFolder();
        Stream.of(folder.getTemplates(),
                  folder.getContent(),
                  folder.getAssets()
        ).forEach(this::createFolder);
    }

    @Override
    protected Option createOption () {
        return Option
                .builder("i")
                .longOpt("init")
                .desc("Create basic folder structure")
                .build();
    }

    @SneakyThrows
    private void createFolder (Path path) {
        if (Files.exists(path)) {
            log.warn("Folder \"{}\" already exists", path.getFileName());
            return;
        }
        Files.createDirectories(path);
        log.info("Folder \"{}\" was created", path.getFileName());
    }
}
