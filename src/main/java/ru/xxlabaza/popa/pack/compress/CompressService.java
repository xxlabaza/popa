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

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.pack.ContentType;

import static java.util.stream.Collectors.toMap;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 02.03.2016
 */
@Service
public class CompressService {

    private final Map<ContentType, Compressor> compressors;

    @Autowired
    CompressService (List<Compressor> compressors) {
        this.compressors = compressors.stream().collect(toMap(Compressor::getSupportedType, Function.identity()));
    }

    public String compress (String content, ContentType type) {
        if (!compressors.containsKey(type)) {
            throw new UnsupportedOperationException("Unsupported content type " + type.name());
        }

        Reader reader = new StringReader(content);
        Writer writer = new StringWriter();
        compressors.get(type).compress(reader, writer);

        return writer.toString();
    }
}
