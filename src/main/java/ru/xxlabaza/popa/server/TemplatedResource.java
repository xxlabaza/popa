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
package ru.xxlabaza.popa.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import ru.xxlabaza.popa.template.TemplaterFacade;
import ru.xxlabaza.popa.util.FileSystemUtils;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2016
 */
@Slf4j
public class TemplatedResource extends PathResource {

    private String content;

    private final TemplaterFacade templaterFacade;

    public TemplatedResource (Path path, TemplaterFacade templaterFacade) {
        super(path);
        this.templaterFacade = templaterFacade;
    }

    @Override
    public Resource addPath (String path) throws IOException, MalformedURLException {
        val rootPath = super.getFile().toPath();
        val validPath = path.startsWith("/")
                        ? path.substring(1)
                        : path;
        return new TemplatedResource(rootPath.resolve(validPath), templaterFacade);
    }

    @Override
    public File getFile () throws IOException {
        val content = getContent();
        if (content.isEmpty()) {
            return null;
        }
        val tmpFile = File.createTempFile("templated_resource_", ".tmp");
        FileSystemUtils.write(tmpFile.toPath(), content);
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    @Override
    public InputStream getInputStream () throws IOException {
        val content = getContent();
        return !content.isEmpty()
               ? new ByteArrayInputStream(content.getBytes())
               : null;
    }

    @Override
    public ReadableByteChannel getReadableByteChannel () throws IOException {
        val inputStream = getInputStream();
        return inputStream != null
               ? Channels.newChannel(inputStream)
               : null;
    }

    @Override
    @SneakyThrows
    public long length () {
        return getContent().getBytes().length;
    }

    private String getContent () throws IOException {
        if (content == null) {
            val file = super.getFile();
            log.info("Building '{}'", file);
            content = file != null
                      ? templaterFacade.process(file.toPath())
                      : "";
        }
        return content;
    }
}
