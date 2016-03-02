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
package ru.xxlabaza.popa.command;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 25.02.2016
 */
@Service
public class CommandPrintHelp extends AbstractCommand {

    @Value("${spring.application.name}")
    private String appName;

    @Lazy
    @Autowired
    private Options options;

    @Override
    public void execute (String[] args) {
        new HelpFormatter().printHelp(appName, options, true);
    }

    @Override
    protected Option createOption () {
        return Option
                .builder("h")
                .longOpt("help")
                .desc("Print help usage")
                .build();
    }
}
