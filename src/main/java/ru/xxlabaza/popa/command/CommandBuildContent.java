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

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.cli.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.AppProperties;
import ru.xxlabaza.popa.AppProperties.Folder;
import ru.xxlabaza.popa.template.TemplaterFacade;
import ru.xxlabaza.popa.util.FileSystemUtils;

import static ru.xxlabaza.popa.util.FileSystemUtils.copy;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 25.02.2016
 */
@Slf4j
@Service
class CommandBuildContent extends AbstractCommand {

    private final static PathMatcher CONTENT_FILE_PATH_MATCHER;

    static {
        CONTENT_FILE_PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:*.{html,htm}");
    }

    private final Folder folder;

    @Autowired
    private TemplaterFacade templaterFacade;

    @Autowired
    CommandBuildContent (AppProperties appProperties) {
        folder = appProperties.getFolder();
    }

    @Override
    @SneakyThrows
    public void execute (String[] args) {
        val content = folder.getContent();
        val assets = folder.getAssets();

        val build = folder.getBuild();
        if (!Files.exists(build)) {
            log.info("Creating folder '{}'", build);
            Files.createDirectories(build);
        }

        Files.walk(content)
                .filter(it -> CONTENT_FILE_PATH_MATCHER.matches(it.getFileName()))
                .forEach(it -> {
                    Path outputPath = build.resolve(content.relativize(it));
                    log.info("Processing '{}'", it);
                    String result = templaterFacade.process(it);
                    log.info("Writing result to '{}'", outputPath);
                    FileSystemUtils.write(outputPath, result);
                });

        log.info("Copying '{}' content to '{}' folder", assets, build);
        Files.list(assets).forEach(it -> {
            copy(it, build.resolve(it.getFileName()));
        });
    }

    @Override
    protected Option createOption () {
        return Option
                .builder("b")
                .longOpt("build")
                .desc("Build content files and copy them and assets into build folder")
                .build();
    }
}
