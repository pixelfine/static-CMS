# Static Site Generator

[![pipeline status](https://gitlab.com/lpetic/ssg/badges/main/pipeline.svg)](https://gitlab.com/lpetic/ssg/-/commits/main)
[![coverage report](https://gitlab.com/lpetic/ssg/badges/main/coverage.svg)](https://gitlab.com/lpetic/ssg/-/commits/main)

## Download Project

Download the project source code which includes binary and jar distribution. [Download here.](https://gitlab.com/lpetic/ssg/-/jobs/artifacts/main/download?job=build)

If you want to download a precise release, go to the release section to download the specific artifacts.

## Install and Launch

I would like to point out that there are **two cases**:
- **outside** the Docker container
- **inside** the Docker container

To install the project:
```shell script
$ make install         # run docker-compose
```

To launch the project:
```shell script
$ make inside          # get inside the container
$ make init            # gradle build
$ make run ARGS="arg"  # gradle run
```

The .jar file can be found under:
```
build/libs/ssg-1.0-SNAPSHOT.jar
```


Get the binary version:
```shell script
$ make inside          # from Docker Container 
$ make init
$ cd build/distributions/ssg-1.0-SNAPSHOT
$ unzip ssg-1.0-SNAPSHOT.zip
$ cd ssg-1.0-SNAPSHOT/bin
$ ./ssg help
```

You should use during:
- development : ```make run ARGS="command --option"```
- production : ```ssg command --option```.

Command line interface:
```shell script
$ ssg help
$ ssg build file1.html file2.html
$ ssg build --input-dir ./test/
$ ssg build --input-dir ./test/ --jobs 4
$ ssg build --input-dir ./test/ --output-dir ./folder_output/
$ ssg build --input-dir ./test/ --output-dir ./folder/ --rebuild-all
$ ssg serve
```

## Tests

To launch tests:

```shell script
$ make unit
```

Open unit test reports from the current folder:
```shell script
$ firefox /ssg/build/reports/tests/test/index.html # Unit Tests
$ firefox ssg/build/reports/jacoco/test/html/index.html # Jacoco
```
