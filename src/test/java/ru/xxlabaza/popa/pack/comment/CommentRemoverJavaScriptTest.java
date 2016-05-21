/*
 * Copyright 2016 Pivotal Software, Inc..
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
package ru.xxlabaza.popa.pack.comment;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 04.03.2016
 */
public class CommentRemoverJavaScriptTest {

    private final static CommentRemover COMMENT_REMOVER;

    static {
        COMMENT_REMOVER = new CommentRemoverJavaScript();
    }

    @Test
    public void removeCommentsMultilineStartOfLine () {
        String text = "Hello world!";
        String string = "/* This is a comment */" + text;
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(text, result);
    }

    @Test
    public void removeCommentsMultilineEndOfLine () {
        String text = "Hello world!";
        String string = text + "/* This is a comment */";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(text, result);
    }

    @Test
    public void removeCommentsMultiline () {
        String string = "/*My license*/\n" +
                        "/* Few words \n" +
                        " * about author \n" +
                        " */\n" +
                        "/**/\n" +
                        "Hello world!/* This is a realy important information! */";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals("\n\n\nHello world!", result);
    }

    @Test
    public void removeCommentsOneLineEndOfLine () {
        String text = "Hello world!";
        String string = text + "// This is a comment";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(text, result);
    }

    @Test
    public void removeCommentsOneLineEndOfLine2 () {
        String string = "Hello // This is a comment\n" +
                        "world!";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals("Hello \nworld!", result);
    }

    @Test
    public void removeCommentsMixed () {
        String string = "/* Few words \n" +
                        " * about author \n" +
                        " */\n" +
                        "// Prepare for the text!\n" +
                        "Hello/* This is a realy important information! */ world!// the end\n" +
                        "/*or not?*/\n" +
                        "// I don't know";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals("\n\nHello world!\n\n", result);
    }

    @Test
    public void dontRemoveCommentsFromInvalidString () {
        String string = "Hello world!\n" +
                        "/* My comment...or not?";
        Assert.assertEquals(string, string);
    }

    @Test
    public void handleURL () {
        String string = "$(\"Please refer to https://github.com/moment/issues/1407 for more info.\",function(a){ })";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(string, result);
    }

    @Test
    public void loadFiles () {
        Stream.of("moment.min.js", "datatables.min.js").forEach(it -> {
            String string = getContent(it);
            String result = COMMENT_REMOVER.removeComments(string);
            Assert.assertEquals(string, result);
        });
    }

    @Test
    public void slashHandling () {
        String string = "\"\\\\\" \"http://some.com";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(string, result);
    }

    @Test
    public void regexHandling () {
        Map<String, String> examples = new HashMap<String, String>();
        examples.put("/* comment *//[&<>\"'`]/g// comment", "/[&<>\"'`]/g");
        examples.put("/* comment *//['\\n\\r\\u2028\\u2029\\\\]/g// comment", "/['\\n\\r\\u2028\\u2029\\\\]/g");
        examples.put("/* comment */var d = 4 / 2// comment", "var d = 4 / 2");

        examples.entrySet().stream().forEach(it -> {
            String result = COMMENT_REMOVER.removeComments(it.getKey());
            Assert.assertEquals(it.getValue(), result);
        });
    }

    @SneakyThrows
    private String getContent (String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);
        Path path = Paths.get(url.toURI());
        return new String(Files.readAllBytes(path), "UTF-8");
    }
}
