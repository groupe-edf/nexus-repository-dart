<img src="https://zupimages.net/up/21/25/3lfx.jpg" width="80"/>

# nexus-repository-dart

[![Build Status](https://travis-ci.com/groupe-edf/nexus-repository-dart.svg?branch=main)](https://travis-ci.com/groupe-edf/nexus-repository-dart)
[![Coverage Status](https://coveralls.io/repos/github/groupe-edf/nexus-repository-dart/badge.svg?branch=main)](https://coveralls.io/github/groupe-edf/nexus-repository-dart?branch=main)

Nexus plugin to configure Dart repositories

# Table Of Contents
* [Features](#features)
* [Requirements](#requirements)
* [Developing](#developing)
   * [Tools required ](#tools-required )
   * [Build the plugin](#build-the-plugin)
* [Installing the plugin](#installing-the-plugin)
   * [Pre-built bundle](#pre-built-bundle)
   * [Bundle built locally](#bundle-built-locally)
* [Configuration](#configuration)
* [Usage](#usage)
* [Team](#team)
* [Getting help](#getting-help)

## Features

- [x] Dart Proxy repository

## Requirements

- Nexus Repository Manager up to 3.31.0-01

This plugin has been tested on the version 3.31.0-01 and 3.31.1-01 but it may works with others versions.

## Compatibility Matrix

| Plugin version | Nexus repository version     |
|----------------|------------------------------|
| v1.0.0         | < 3.38                       |
| v1.0.1         | < 3.38                       |
| v1.1.0         | >= 3.38                      |

## Developing

There is goods informations about developing bundle for Nexus 3 at [Bundle Development](https://help.sonatype.com/display/NXRM3/Bundle+Development)

### Tools required 

- [Apache Maven 3](https://maven.apache.org/download.cgi)
- [OpenJDK 8](https://developers.redhat.com/products/openjdk/download)

### Build the plugin
- Clone the project with git clone ...
- Build the plugin with

```
cd nexus-repository-dart
mvn clean install -PbuildKar
```

## Installing the plugin
### Pre-built bundle
The pre-built bundle are directly available in the assets of [releases on Github](https://github.com/groupe-edf/nexus-repository-dart/releases) with name `nexus-repository-dart-<version>-bundle.kar`.

### Bundle built locally
The package built like described in [Developing](#developing) section should be available in `<cloned_repo>/target/` with name `nexus-repository-dart-<version>-bundle.kar`.

To install the nexus-repository-dart:
- Stop Nexus
- Copy the bundle (*.kar file) into the directory `<nexus_dir>/deploy`
- Start Nexus

## Configuration

Go to the configuration of Nexus (admin rights) -> Repositories

Click on "Create repository" and select "dart (proxy)"

Complete the informations like others Proxy repositories. By default, the remote storage for Dart is "https://pub.dev".

## Usage

To use Nexus Dart Proxy repository, it must be set as an environment variable.

- Windows command line :

```
set PUB_HOSTED_URL=http://[nexus_url]/repository/[repository_name]/
set FLUTTER_STORAGE_BASE_URL=http://[nexus_url]/repository/[repository_name]/
```

- Linux or MacOs :

```
export PUB_HOSTED_URL=http://[nexus_url]/repository/[repository_name]/
export FLUTTER_STORAGE_BASE_URL=http://[nexus_url]/repository/[repository_name]/
```

With Flutter it is possible to verify the configuration with the command `flutter doctor -v`

Example of result :

```
C:\WORKSPACES\dart_test\dart_test_project>flutter doctor -v
[✓] Flutter (Channel stable, 2.2.3, on Microsoft Windows [version 10.0.19042.630], locale fr-FR)
    • Flutter version 2.2.3 at C:\Softs\flutter
    • Framework revision f4abaa0735 (3 weeks ago), 2021-07-01 12:46:11 -0700
    • Engine revision 241c87ad80
    • Dart version 2.13.4
    • Pub download mirror http://localhost:8081/repository/dartlang/
    • Flutter download mirror http://localhost:8081/repository/dartlang/
    ...
```

Now, the downloads of Dart packages will works with the Nexus Dart Proxy repository.

## Troubleshooting
"flutter updtate" command is not working when nexus repository dart is setted. It can break your installation and need a complete reinstall of flutter on your computer.
To avoid this, environment variables `PUB_HOSTED_URL` and `FLUTTER_STORAGE_BASE_URL` must be unset before launching "flutter update".

## Team

Product Owner: [Cloudehard](https://github.com/Cloudehard)

Developer: [mat1e](https://github.com/mat1e)

## Getting help

Looking to contribute to our code but need some help? There's a few ways to get information:

- Check out the [Nexus3](http://stackoverflow.com/questions/tagged/nexus3) tag on Stack Overflow
- Check out the [Nexus Repository User List](https://groups.google.com/a/glists.sonatype.com/forum/?hl=en#!forum/nexus-users)
- [Open an issue on this repository](https://github.com/groupe-edf/nexus-repository-dart/issues)
