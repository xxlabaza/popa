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

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SECURITY;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

import java.nio.file.Path;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.cli.Option;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xxlabaza.popa.AppProperties;
import ru.xxlabaza.popa.AppProperties.Server.Proxy;
import ru.xxlabaza.popa.server.TemplatedResource;
import ru.xxlabaza.popa.template.TemplaterFacade;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 25.02.2016
 */
@Slf4j
@Service
class CommandRunServer extends AbstractCommand {

    private final Path assetsPath;

    private final Path contentPath;

    private final int defaultPort;

    private final Proxy proxy;

    @Autowired
    private TemplaterFacade templaterFacade;

    @Autowired
    CommandRunServer (AppProperties appProperties) {
        defaultPort = appProperties.getServer().getPort();
        proxy = appProperties.getServer().getProxy();

        val folder = appProperties.getFolder();
        assetsPath = folder.getAssets();
        contentPath = folder.getContent();
    }

    @Override
    @SneakyThrows
    public void execute (String[] args) {
        int port = args != null && args.length == 1
                   ? Integer.parseInt(args[0])
                   : defaultPort;

        val server = new Server(port);
        server.setStopAtShutdown(true);

        val handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {
            createContentHandler(),
            createAssetsHandler(),
            createProxyServlet(port)
        });
        server.setHandler(handlers);

        log.info("Starting server at http://localhost:{}", port);
        server.start();
        server.join();
    }

    @Override
    protected Option createOption () {
        return Option
                .builder("s")
                .longOpt("server")
                .argName("port")
                .optionalArg(true)
                .numberOfArgs(1)
                .desc("Run server for dynamic generation content. Default port is " + defaultPort)
                .build();
    }

    private Handler createAssetsHandler () {
        val assetsHandler = new ResourceHandler();
        assetsHandler.setDirectoriesListed(true);
        assetsHandler.setBaseResource(new PathResource(assetsPath));
        return assetsHandler;
    }

    private Handler createContentHandler () {
        val contentHandler = new ResourceHandler();
        contentHandler.setDirectoriesListed(true);
        contentHandler.setBaseResource(new TemplatedResource(contentPath, templaterFacade));
        return contentHandler;
    }

    private Handler createProxyServlet (int port) {
        if (proxy == null || proxy.getTo() == null) {
            return new DefaultHandler();
        }

        val prefix = !proxy.getPrefix().startsWith("/")
                     ? "/" + proxy.getPrefix()
                     : proxy.getPrefix();

        log.info("Proxy settings were found");
        log.info("Redirects all requests from 'http://localhost:{}{}' to '{}'", port, prefix, proxy.getTo());

        val proxyServletHolder = new ServletHolder(ProxyServlet.Transparent.class);
        proxyServletHolder.setInitParameter("proxyTo", proxy.getTo().toString());
        proxyServletHolder.setInitParameter("prefix", prefix);

        val servletContextHandler = new ServletContextHandler(NO_SESSIONS | NO_SECURITY);
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(proxyServletHolder, "/*");

        return servletContextHandler;
    }
}
