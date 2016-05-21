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
package ru.xxlabaza.popa.pack.compress;

import com.yahoo.platform.yui.compressor.CssCompressor;
import java.io.Reader;
import java.io.Writer;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.pack.ContentType;

import static ru.xxlabaza.popa.pack.ContentType.CSS;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 02.03.2016
 */
@Service
class CompressorCss implements Compressor {

    @Value("${app.style.lineBreak}")
    private int lineBreak;

    @Override
    @SneakyThrows
    public void compress (Reader reader, Writer writer) {
        val compressor = new CssCompressor(reader);
        compressor.compress(writer, lineBreak);
    }

    @Override
    public ContentType getSupportedType () {
        return CSS;
    }
}
