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

import java.nio.file.Path;
import lombok.SneakyThrows;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.AppProperties;
import ru.xxlabaza.popa.pack.comment.CommentRemoveService;
import ru.xxlabaza.popa.pack.compress.CompressService;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;
import static ru.xxlabaza.popa.pack.ContentType.CSS;
import static ru.xxlabaza.popa.pack.ContentType.HTML;
import static ru.xxlabaza.popa.pack.ContentType.JAVASCRIPT;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2016
 */
@Service
public class PackingService {

    private final Path build;

    @Autowired
    private CommentRemoveService commentRemoveService;

    @Autowired
    private CompressService compressService;

    private final OutputSettings outputSettings;

    @Autowired
    public PackingService (AppProperties appProperties) {
        build = appProperties.getFolder().getBuild();

        outputSettings = new OutputSettings();
        outputSettings.indentAmount(0);
        outputSettings.outline(false);
        outputSettings.prettyPrint(false);
        outputSettings.syntax(html);
    }

    @SneakyThrows
    public String pack (Path path) {
        val document = Jsoup.parse(path.toFile(), "UTF-8").outputSettings(outputSettings);
        processCss(document);
        processJavaScript(document);
        return processHtml(document.toString());
    }

    private void processCss (Document document) {
        document.select("link[rel=stylesheet]:not([href^=http]):not([id]):not([class])").forEach(link -> {
            String href = link.attr("href");
            String content = commentRemoveService.removeComments(build.resolve(href));
            content = compressService.compress(content, CSS);

            Element style = document.createElement("style");
            style.html(content);

            link.after(style);
            link.remove();
        });
    }

    private String processHtml (String content) {
        return commentRemoveService.removeComments(content, HTML);
    }

    private void processJavaScript (Document document) {
        document.select("script[src$=.js]:not([src^=http]):not([id]):not([class])").forEach(script -> {
            String src = script.attr("src");
            String content = commentRemoveService.removeComments(build.resolve(src));
            content = compressService.compress(content, JAVASCRIPT);

            script.removeAttr("src");
            script.html(content);
        });
    }
}
