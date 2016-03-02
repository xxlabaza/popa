/*
 * Copyright 2016 xxlabaza.
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
package ru.xxlabaza.popa.template;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.xxlabaza.popa.Main;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class TemplaterGroovyTest {

    @Autowired
    private TemplaterGroovy templaterGroovy;

    @Test
    public void build () {
//        Path templatePath = Paths.get("/Users/xxlabaza/tt/templates/page3.tpl");
//        Map<String, Object> bindings = new HashMap<>(2);
//        bindings.put("title", "Title");
//        bindings.put("content", "Content");
//        templaterGroovy.build(templatePath, bindings);
    }
}
