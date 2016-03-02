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
import ru.xxlabaza.popa.pack.PackingService;
import ru.xxlabaza.popa.util.FileSystemUtils;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 29.02.2016
 */
@Slf4j
@Service
class CommandCompress extends AbstractCommand {

    private final static PathMatcher HTML_PATH_MATCHER;

    static {
        HTML_PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:*.{html,htm}");
    }

    @Autowired
    private CommandBuildContent commandBuildContent;

    @Autowired
    private PackingService compressService;

    private final Folder folder;

    @Autowired
    CommandCompress (AppProperties appProperties) {
        folder = appProperties.getFolder();
    }

    @Override
    @SneakyThrows
    public void execute (String[] arguments) {
        val build = folder.getBuild();
        if (!Files.exists(build)) {
            log.info("Content was not built. Starting appropriate command...");
            commandBuildContent.execute(null);
        }

        val compressed = folder.getCompressed();
        FileSystemUtils.createNewDirectory(compressed);

        Files.walk(build)
                .filter(it -> HTML_PATH_MATCHER.matches(it.getFileName()))
                .forEach(it -> {
                    log.info("Compressing '{}'", it);
                    String content = compressService.pack(it);
                    Path outputFile = compressed.resolve(build.relativize(it));
                    log.info("Writing result to '{}'", outputFile);
                    FileSystemUtils.write(outputFile, content);
                });
    }

    @Override
    protected Option createOption () {
        return Option
                .builder("c")
                .longOpt("compress")
                .desc("Compressing build files into uber HTML files with inline css/js contents")
                .build();
    }
}
