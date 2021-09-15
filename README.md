# Fetch saved queries from infoshare programmatically

This repository contains a small [sbt](https://www.scala-sbt.org/) project.  Once compiled, it provides a simple command-line utility, written in Scala, which uses [Selenium](https://www.seleniumhq.org/) to automate the download of files from the [Stats NZ Infoshare website](http://infoshare.stats.govt.nz/).  Chrome is used throughout.  You will need to download and install Chrome and [chromedriver](https://chromedriver.chromium.org/) yourself for this to work.

To create the program:

```bash
sbt assembly
```

This will create `./target/scala-2.13/infoshare.jar`, which can then be run as follows:

```bash
java -jar target/scala-2.13/infoshare.jar test.sch blah.csv
```

Here `test.sch` is a file containing a list of series identifiers, and a test file is included as a reference.  `blah.csv` is the output file.

To simplify the process of installing compatible vesions of Chrome and Chromedriver, a simple `Dockerfile` is provided.  To build the image (after running `sbt assembly`):

```bash
docker build -t infoshare .
```

One can run a container as a command-line program of sorts:

```bash
docker run -d --rm -v ${PWD}:/work infoshare /work/test.sch /work/blah.csv
```

In this case the contents of the present working directory are mounted inside the container at `/work`, and so the output will be visible in `${PWD}/blah.csv` after the container terminates.  The container is run as root, so `blah.csv` will be owned by root.  I'll revisit this at some point.