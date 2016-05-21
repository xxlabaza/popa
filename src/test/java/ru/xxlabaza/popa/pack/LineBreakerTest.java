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
package ru.xxlabaza.popa.pack;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.xxlabaza.popa.Main;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 09.03.2016
 */
@IntegrationTest("app.style.lineBreak=2")
@SpringApplicationConfiguration(classes = Main.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class LineBreakerTest {

    @Autowired
    private LineBreaker lineBreaker;

    @Test
    public void test1 () {
        String string = "var i = 0; var j = 1;";
        String result = lineBreaker.lineBreak(string);
        Assert.assertEquals("var i = 0;\nvar j = 1;\n", result);
    }

    @Test
    public void test2 () {
        String string = " ";
        String result = lineBreaker.lineBreak(string);
        Assert.assertEquals("\n", result);
    }

    @Test
    public void test3 () {
        String string = "var i = 0;                ";
        String result = lineBreaker.lineBreak(string);
        Assert.assertEquals("var i = 0;\n", result);
    }

    @Test
    public void test4 () {
        String string = "                    var i = 0;                ";
        String result = lineBreaker.lineBreak(string);
        Assert.assertEquals("var i = 0;\n", result);
    }

    @Test
    public void test5 () {
        String string = "var i = 0; var j = \"hello;world\"; var h = 'hello;world';";
        String result = lineBreaker.lineBreak(string);
        Assert.assertEquals("var i = 0;\n" +
                            "var j = \"hello;world\";\n" +
                            "var h = 'hello;world';\n", result);
    }

    @Test
    public void test6 () {
        String string = "var i = 0, j = 1";
        String result = lineBreaker.lineBreak(string);
        Assert.assertEquals("var i = 0,\nj = 1", result);
    }
}
