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
package ru.xxlabaza.popa.pack.compress;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 09.03.2016
 */
public class CompressorJavaScriptTest {

    private final static Compressor COMPRESSOR;

    static {
        COMPRESSOR = new CompressorJavaScript();
    }

    @Test
    public void compressS () {
        String string = "var i = 0; var j = 1";
        Assert.assertEquals("var i=0;\nvar j=1;", compress(string));
    }

    private String compress (String string) {
        Reader reader = new StringReader(string);
        Writer writer = new StringWriter();
        COMPRESSOR.compress(reader, writer);
        return writer.toString();
    }
}
