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
package ru.xxlabaza.popa.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 27.02.2016
 */
@Slf4j
public final class FileSystemUtils {

    @SneakyThrows
    public static void copy (Path from, Path to) {
        if (!Files.isDirectory(from)) {
            if (Files.exists(to)) {
                val fromFileTime = Files.getLastModifiedTime(from);
                val toFileTime = Files.getLastModifiedTime(to);
                if (fromFileTime.compareTo(toFileTime) < 0) {
                    log.info("File '{}' wasn't coppied. It has actual state", from.toString());
                    return;
                }
            }
            log.info("File '{}' was coppied", from.toString());
            Files.copy(from, to, REPLACE_EXISTING);
            return;
        }

        if (!Files.exists(to)) {
            log.info("Directory '{}' was created", to.toString());
            Files.createDirectories(to);
        }
        Files.list(from).forEach(it -> copy(it, to.resolve(it.getFileName())));
    }

    @SneakyThrows
    public static void createNewDirectory (Path path) {
        if (Files.exists(path)) {
            log.info("Removing existing folder '{}'", path);
            delete(path);
        }
        log.info("Creating folder '{}'", path);
        Files.createDirectories(path);
    }

    @SneakyThrows
    public static void delete (Path path) {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(FileSystemUtils::delete);
        }
        Files.deleteIfExists(path);
    }

    @SneakyThrows
    public static String getContent (Path file) {
        return new String(Files.readAllBytes(file));
    }

    public static String getExstension (Path file) {
        val fileName = file.getFileName().toString();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static Optional<Path> getResource (Path folder, String path) {
        val file = folder.resolve(path);
        return Files.exists(file)
               ? Optional.of(file)
               : Optional.empty();
    }

    @SneakyThrows
    public static void write (Path path, String content) {
        Files.write(path, content.getBytes(), CREATE);
    }

    private FileSystemUtils () {
    }
}
