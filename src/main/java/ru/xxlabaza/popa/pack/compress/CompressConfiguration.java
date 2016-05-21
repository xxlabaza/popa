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

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 21.05.2016
 */
@Configuration
public class CompressConfiguration {

    @Bean
    CompressService compressService (List<Compressor> compressors) {
        return new CompressService(compressors);
    }

    @Bean
    CompressorJavaScript compressorJavaScript () {
        return new CompressorJavaScript();
    }

    @Bean
    CompressorCss compressorCss () {
        return new CompressorCss();
    }
}
