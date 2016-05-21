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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 05.03.2016
 */
public class CommentRemoverHtmlTest {

    private final static CommentRemover COMMENT_REMOVER;

    static {
        COMMENT_REMOVER = new CommentRemoverHtml();
    }

    @Test
    public void removeCommentsStartOfLine () {
        String text = "Hello world!";
        String string = "<!-- This is a comment -->" + text;
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(text, result);
    }

    @Test
    public void removeCommentsEndOfLine () {
        String text = "Hello world!";
        String string = text + "<!-- This is a comment -->";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(text, result);
    }

    @Test
    public void removeCommentsMultiline () {
        String string = "<!--My license-->\n" +
                        "<!-- Few words \n" +
                        "     about author \n" +
                        "     -->\n" +
                        "<!----> \n" +
                        "Hello world!<!-- This is a realy important information! -->";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals("\n\n \nHello world!", result);
    }

    @Test
    public void dontRemoveCommentsFromInvalidString () {
        String string = "Hello world!\n" +
                        "<!-- My comment...or not?";
        Assert.assertEquals(string, string);
    }

    @Test
    public void dontRemoveConditionalComments () {
        String string = "Hello world<!--[if IE 8]>Hello IE 8!<![endif]-->";
        String result = COMMENT_REMOVER.removeComments(string);
        Assert.assertEquals(string, result);
    }
}
