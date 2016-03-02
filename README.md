# Popa makes writing front-end easier

Popa is a java cli static site generating tool for developers and designers. Popa's first goal is reducing the complexity of creating front-end by providing easy in use mechanisms of templating, minifying and HTTP server.

### Why Popa and not X?

You can use tools like JBake or something similar to write templated front-end. You can write your own code on top of template engines like Freemarker or Groovy. Popa aims to make your life easier. Via embedded HTTP server, which could proxy request to your back-end, templates and minification, you should be able to write clean and easy-to-support front-end.

### How does Popa work?

Popa works by processing content files, which contain meta-information about template and other. Just before building it, meta information is applied to template and in a straightforward fashion. Moreover, Popa is able to minify built files to self contained HTML pages with compressed and inlined styles and scripts.

### Usage

#### Initialize project structure

```bash
$> java -jar popa.jar --init
2016-03-02 18:53:16.886  INFO : Folder 'templates' was created
2016-03-02 18:53:16.891  INFO : Folder 'content' was created
2016-03-02 18:53:16.892  INFO : Folder 'assets' was created
$> tree
.
├── assets
├── content
└── templates

3 directories, 0 files
```

#### Build content files

Let's imagine that we have a structure of the project like this one:

```bash
$> tree
.
├── assets
│   ├── css
│   │   └── my.css
│   └── js
│       └── my.js
├── content
│   └── index.html
└── templates
    └── page.ftl

5 directories, 4 files
```

**templates/page.ftl**

```html
<html>
<head>
  <title>${title}</title>

<#list styles as style>
  <link rel="stylesheet" type="text/css" href="/css/${style}">
</#list>
</head>

<body>
${content}

<#list scripts as script>
  <script src="/js/${script}"></script>
</#list>
</body>
</html>
```

**content/index.html**

```html
title: My Page
template: page
styles:
  - my.css
scripts:
  - my.js
~~

  <h1>Hello world!</h1>
  <p>
    How are you?
  </p>
```

As you can see, content file **index.html** is just an HTML fragment with YAML meta information in the header with "**~**" delimeter. Number of signs "**~**" doesn't matter, it can be one "**~**" or even 255, bu it **must** takes whole line, without indentations and trailing spaces.

This meta data is optional, if your content file doesn't have it, the file is copied as is. There is no any required fields, except **template**, it tells which one template to use for building the content file.

Let's build project:

```bash
$> java -jar popa.jar --build
2016-03-02 22:06:10.100  INFO : Creating folder 'build'
2016-03-02 22:06:10.120  INFO : Processing 'content/index.html'
2016-03-02 22:06:10.138  INFO : Template: 'page', bindings:
{
  template=page,
  styles=[my.css],
  title=My Page,
  scripts=[my.js],
  content=<h1>Hello world!</h1>   <p>     How are you?   </p>
}
2016-03-02 22:06:10.425  INFO : Writing result to 'build/index.html'
2016-03-02 22:06:10.428  INFO : Copying 'assets' content to 'build' folder
2016-03-02 22:06:10.430  INFO : Directory 'build/css' was created
2016-03-02 22:06:10.431  INFO : File 'assets/css/my.css' was coppied
2016-03-02 22:06:10.434  INFO : Directory 'build/js' was created
2016-03-02 22:06:10.435  INFO : File 'assets/js/my.js' was coppied
$> tree
.
├── assets
│   ├── css
│   │   └── my.css
│   └── js
│       └── my.js
├── build
│   ├── css
│   │   └── my.css
│   ├── index.html
│   └── js
│       └── my.js
├── content
│   └── index.html
└── templates
    └── page.ftl

8 directories, 7 files
```

Now the **build** directory contains all assets files and built content file **index.html**:

```html
<html>
<head>
  <title>My Page</title>

  <link rel="stylesheet" type="text/css" href="/css/my.css">
</head>

<body>
<h1>Hello world!</h1>
  <p>
    How are you?
  </p>

  <script src="/js/my.js"></script>
</body>
</html>
```

> **IMPORTANT:** Popa supports [Freemarker](http://freemarker.org) and [Groovy Markup](http://docs.groovy-lang.org/latest/html/documentation/template-engines.html#_the_markuptemplateengine) template engines.

#### Compress the project

After we built the project from the previous example, we want to compress it in self contained HTML file with resource minification (JavaScript and CSS files).

Our project, right now, has the structure from the previous example:

```bash
$> tree
.
├── assets
│   ├── css
│   │   └── my.css
│   └── js
│       └── my.js
├── build
│   ├── css
│   │   └── my.css
│   ├── index.html
│   └── js
│       └── my.js
├── content
│   └── index.html
└── templates
    └── page.ftl

8 directories, 7 files
```

**my.css** file content

```css
/* H1 TAG RULE */
h1 {
    color: red; /* TODO: add yellow backgroud color */
}

/* P TAG RULE */
p {
    color: blue; /* hmmm, maybe it is not the bes color for text */
}
```

```javascript
console.log('Hello world!');
// bla-bla-bla
console.log('Are you still here?'); /* 42 */ var v = 10;
```

As you can see, those files have a lot of useless spaces, indentions and comments, which are good for a programmer, but not for a browser.

Let's inline those resources in our **index.html** file and minify them:

```bash
$> java -jar popa.jar --compress
2016-03-02 22:29:15.051  INFO : Creating folder 'compressed'
2016-03-02 22:29:15.053  INFO : Compressing 'build/index.html'
2016-03-02 22:29:15.358  INFO : Writing result to 'compressed/index.html'
$> tree
.
├── assets
│   ├── css
│   │   └── my.css
│   └── js
│       └── my.js
├── build
│   ├── css
│   │   └── my.css
│   ├── index.html
│   └── js
│       └── my.js
├── compressed
│   └── index.html
├── content
│   └── index.html
└── templates
    └── page.ftl

9 directories, 8 files
```

Now, in directory **compressed**, we have our uber HTML content file:

```html
<html><head>
  <title>My Page</title>

  <style>h1{color:red}p{color:blue}</style>
</head>

<body>
<h1>Hello world!</h1>
  <p>
    How are you?
  </p>

  <script>console.log("Hello world!");console.log("Are you still here?");var v=10;</script>

</body></html>
```

#### Run server

It is very tediously to write, build and check the result in a terminal, more awesome approach is to watch the result of your work right in a browser. For that purpose, you can run Popa as a server, which will build your content files dynamicaly, on the fly.

```bash
$> rm -rf build/ compressed/
$> java -jar popa.jar --server
2016-03-02 22:38:24.400  INFO : Starting server at http://localhost:9090
```

Now, you can open [http://localhost:9090/index.html](http://localhost:9090/index.html) and see our **index.html** file. You also can change content file or template, refresh your browser and see the changes.

Also, you are able to run server on a specific port:

```bash
$> java -jar popa.jar --server 8989
2016-03-02 22:39:11.157  INFO : Starting server at http://localhost:8989
```

Moreover, we can redirect all requests, which are not match any asset or content file, to a back-end, simply add **application.yml** file to a project's root:

```yml
app:
  server:
    proxy:
      to:     localhost:8080    # back-end address
      prefix: /some_prefix      # prefix, from which request redirects to back-end
```

```bash
$> java -jar popa.jar --server
2016-03-02 23:25:42.491  INFO : Proxy settings were found
2016-03-02 23:25:42.513  INFO : Redirects all requests from 'http://localhost:9090/some_prefix' to 'http://localhost:8080'
2016-03-02 23:25:42.560  INFO : Starting server at http://localhost:9090
```

> **IMPORTANT:** If you leave **app.server.proxy.prefix** an empty, it is set to default value - '/', which means what all requests, which don't match any asset or content file, will be redirected to your back-end.

#### Combine Popa options

For more comfortable use, you are able to combine different command options, for example, if you want to initialize project stracture and run server, just type:

```bash
$> java -jar popa.jar --init --server
```

or a short form:

```bash
$> java -jar popa.jar -is
```

#### Rewrite default properties

```bash
$> cat application.yml
app:
  server:
    port: 8181
    proxy:
      to:     localhost:8080
      prefix: /popa
  folder:
    templates:  temp
    content:    html
    build:      target
    assets:     static
    compressed: minified
$> java -jar popa.jar --init
2016-03-02 19:36:20.656  INFO : Folder 'temp' was created
2016-03-02 19:36:20.663  INFO : Folder 'html' was created
2016-03-02 19:36:20.663  INFO : Folder 'static' was created
$> tree
.
├── application.yml
├── html
├── static
└── temp

3 directories, 1 file
$> java -jar popa.jar --server
2016-03-02 19:40:18.834  INFO : Proxy settings were found
2016-03-02 19:40:18.840  INFO : Redirects all requests from 'http://localhost:9090/popa' to 'http://localhost:8080'
2016-03-02 19:40:18.887  INFO : Starting server at http://localhost:8181
...
```