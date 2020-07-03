![nukkit](.github/images/banner.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Build Status](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/badge/icon)](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/)
![Tests](https://img.shields.io/jenkins/t/https/ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master.svg)
[![Discord](https://img.shields.io/discord/393465748535640064.svg)](https://discord.gg/5PzMkyK)

Introduction
-------------

Cloudburst is nuclear-powered server software for Minecraft: Bedrock Edition.
It has a few key advantages over other server software:

* Written in Java, Cloudburst is faster and more stable.
* Having a friendly structure, it's easy to contribute to Cloudburst's development and rewrite plugins from other platforms into Cloudburst plugins.

Cloudburst is **under improvement** yet, we welcome contributions. 

Links
--------------------

* __[News](https://cloudburstmc.org)__
* __[Forums](https://cloudburstmc.org/forums)__
* __[Discord](https://discord.gg/5PzMkyK)__
* __[Download](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master)__
* __[Plugins](https://cloudburstmc.org/resources/categories/nukkit-plugins.1)__
* __[Wiki](https://cloudburstmc.org/wiki/nukkit)__

Build JAR file
-------------
- `git clone https://github.com/Cloudburst/Server`
- `cd Server`
- `git submodule update --init`
- `./mvnw clean package`

The compiled JAR can be found in the `target/` directory.

Running
-------------
Simply run `java -jar nukkit-1.0-SNAPSHOT.jar`.

Plugin API
-------------
Information on Cloudburst's API can be found at the [wiki](https://cloudburstmc.org/wiki/nukkit/).

Docker
-------------

Running Nukkit in [Docker](https://www.docker.com/) (17.05+ or higher).

Build image from source,

```
docker build -t nukkit .
```

Run once to generate the `/data` volume, default settings, and choose language,

```
docker run -it --rm -p 19132:19132 nukkit
```

Use [docker-compose](https://docs.docker.com/compose/overview/) to start server on port `19132` and with `./data` volume,

```
docker-compose up -d
```

Contributing
------------
Please read the [CONTRIBUTING](.github/CONTRIBUTING.md) guide before submitting any issue. Issues with insufficient information or in the wrong format will be closed and will not be reviewed.
