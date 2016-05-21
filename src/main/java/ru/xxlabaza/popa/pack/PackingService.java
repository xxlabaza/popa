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
package ru.xxlabaza.popa.pack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.AppProperties;
import ru.xxlabaza.popa.pack.comment.CommentRemoveService;
import ru.xxlabaza.popa.pack.compress.CompressService;
import ru.xxlabaza.popa.util.FileSystemUtils;

import static java.util.regex.Pattern.compile;
import static ru.xxlabaza.popa.pack.ContentType.CSS;
import static ru.xxlabaza.popa.pack.ContentType.HTML;
import static ru.xxlabaza.popa.pack.ContentType.JAVASCRIPT;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
@Slf4j
@Service
public class PackingService {

    private final static Pattern CSS_URL_PATTERN;

    static {
        CSS_URL_PATTERN = compile("(?<=url\\()['\"]?([^ \\f\\n\\r\\t\\v'\"\\)]+)['\"]?");
    }

    private final Path build;

    private final Path compressed;

    @Autowired
    private CommentRemoveService commentRemoveService;

    @Autowired
    private CompressService compressService;

    @Autowired
    private LineBreaker lineBreaker;

    private final OutputSettings outputSettings;

    @Autowired
    public PackingService (AppProperties appProperties) {
        build = appProperties.getFolder().getBuild();
        compressed = appProperties.getFolder().getCompressed();

        outputSettings = new OutputSettings();
        outputSettings.prettyPrint(false);
    }

    @SneakyThrows
    public String pack (Path path) {
        log.info("Packing '{}'", path);
        val document = Jsoup.parse(path.toFile(), "UTF-8").outputSettings(outputSettings);
        processCss(document);
        processJavaScript(document);
        return processHtml(document);
    }

    private Path createPath (String path) {
        return path.startsWith("/")
               ? Paths.get(path.substring(1))
               : Paths.get(path);
    }

    private void processCss (Document document) {
        document.select("link[rel=stylesheet]:not([href^=http])").forEach(link -> {
            Path path = build.resolve(createPath(link.attr("href")));
            log.info("Processing style '{}'", path);

            String content = commentRemoveService.removeComments(path);
            content = correctURLs(path, content);

            if (!path.getFileName().toString().endsWith(".min.css")) {
                content = compressService.compress(content, CSS);
            }

            Element style = document.createElement("style");
            style.html(content);

            link.after(style);
            link.remove();
        });
    }

    private String processHtml (Document document) {
        String content = commentRemoveService.removeComments(document.html(), HTML);
        return Parser.unescapeEntities(content, false);
    }

    private void processJavaScript (Document document) {
        document.select("script[src$=.js]:not([src^=http])").forEach(script -> {
            Path path = build.resolve(createPath(script.attr("src")));
            log.info("Processing script '{}'", path);

//            String content = commentRemoveService.removeComments(path);
            String content = FileSystemUtils.getContent(path);
            if (!path.getFileName().toString().endsWith(".min.js")) {
                content = compressService.compress(content, JAVASCRIPT);
            }

            script.removeAttr("src");
            script.html(content);
        });
    }

    private String correctURLs (Path path, String content) {
        Matcher matcher = CSS_URL_PATTERN.matcher(content);
        StringBuffer buffer = new StringBuffer(content.length());
        while (matcher.find()) {
            String url = matcher.group(1);

            String[] tokens = url.split("[\\?#]", 2);

            Path pathToUrlFile = path.getParent().resolve(tokens[0]);
            Path urlPath = build.relativize(pathToUrlFile);
            if (Files.exists(pathToUrlFile)) {
                FileSystemUtils.copy(pathToUrlFile, compressed.resolve(urlPath));
            }

            StringBuilder sb = new StringBuilder('/');
            sb.append(urlPath);
            if (tokens.length > 1) {
                sb.append(url.contains("?")
                          ? '?'
                          : '#'
                ).append(tokens[1]);
            }
            matcher.appendReplacement(buffer, sb.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
