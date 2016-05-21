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
package ru.xxlabaza.popa;

import java.nio.file.Path;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 25.02.2016
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Valid
    private Folder folder;

    private Server server;

    @Valid
    private Style style;

    @Data
    public static class Folder {

        @NotNull
        private Path assets;

        @NotNull
        private Path build;

        @NotNull
        private Path compressed;

        @NotNull
        private Path content;

        @NotNull
        private Path templates;
    }

    @Data
    public static class Server {

        private int port;

        private Proxy proxy;

        @Data
        public static class Proxy {

            private String prefix;

            private String to;

            public void setTo (String to) {
                this.to = to != null && !to.startsWith("http")
                          ? "http://" + to
                          : to;
            }
        }
    }

    @Data
    public static class Style {

        @NotNull
        private Integer lineBreak;
    }
}
