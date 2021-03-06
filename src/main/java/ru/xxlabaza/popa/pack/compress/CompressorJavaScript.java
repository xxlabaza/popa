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

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import java.io.Reader;
import java.io.Writer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.pack.ContentType;

import static ru.xxlabaza.popa.pack.ContentType.JAVASCRIPT;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 02.03.2016
 */
@Slf4j
@Service
class CompressorJavaScript implements Compressor {

    @Value("${app.style.lineBreak}")
    private int lineBreak;

    private final ErrorReporter errorReporter;

    CompressorJavaScript () {
        errorReporter = new JavaScriptErrorReporter();
    }

    @Override
    @SneakyThrows
    public void compress (Reader reader, Writer writer) {
        JavaScriptCompressor compressor = new JavaScriptCompressor(reader, errorReporter);
        compressor.compress(writer, lineBreak, false, false, true, true);
    }

    @Override
    public ContentType getSupportedType () {
        return JAVASCRIPT;
    }

    private class JavaScriptErrorReporter implements ErrorReporter {

        @Override
        public void error (String message, String fileName, int line, String source, int offset) {
            log.error("Error in {}", createMessage(message, fileName, line, source, offset));
        }

        @Override
        public EvaluatorException runtimeError (String message, String fileName, int line, String source, int offset) {
            error(message, fileName, line, source, offset);
            return new EvaluatorException(message);
        }

        @Override
        public void warning (String message, String fileName, int line, String source, int offset) {
            log.warn("Warning {}", createMessage(message, fileName, line, source, offset));
        }

        private String createMessage (String message, String fileName, int line, String source, int offset) {
            val messageBuilder = new StringBuilder();
            if (line >= 0) {
                messageBuilder.append("in ").append(line).append(':').append(offset).append(": ");
            }
            messageBuilder.append(message).append('\n').append(source);
            return messageBuilder.toString();
        }
    }
}
