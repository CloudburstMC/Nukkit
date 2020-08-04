![nukkit](.github/images/banner.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![PowerNukkit v1.X](https://github.com/PowerNukkit/PowerNukkit/workflows/PowerNukkit%20v1.X/badge.svg?branch=master)](https://github.com/PowerNukkit/PowerNukkit/actions?query=branch%3Amaster)
[![Discord](https://img.shields.io/discord/728280425255927879)](https://powernukkit.org/discord)

Introduction
-------------

Nukkit is nuclear-powered server software for Minecraft: Pocket Edition.
It has a few key advantages over other server software:

* Written in Java, Nukkit is faster and more stable.
* Having a friendly structure, it's easy to contribute to Nukkit's development and rewrite plugins from other platforms into Nukkit plugins.

Nukkit is **under improvement** yet, we welcome contributions. 

**PowerNukkit** is a modified version of Nukkit which adds support to a huge amount of features like water-logging, all new blocks, more plugin events, offhand slot, bug fixes and many more.

Please note that PowerNukkit is not handled by NukkitX's staff and is provided with love but without warranty. If you find any issue while running PowerNukkit you should [create a new issue](https://github.com/PowerNukkit/PowerNukkit/issues) in this repository first.

It's also advisable to have a backup schedule set up. PowerNukkit is highly experimental and things may break, so make backups and stay safe.

Links
--------------------

* __[Download PowerNukkit](https://github.com/PowerNukkit/PowerNukkit/releases)__
* __[PowerNukkit Discord](https://powernukkit.org/discord)__
* __[Plugins](https://nukkitx.com/resources/categories/nukkit-plugins.1)__
* __[Wiki](https://nukkitx.com/wiki/nukkit)__
* __[NukkitX](https://github.com/NukkitX/Nukkit)__
* __[NukkitX News](https://nukkitx.com)__
* __[NukkitX Forums](https://nukkitx.com/forums)__
* __[NukkitX Discord](https://discord.gg/5PzMkyK)__

Message from the official NukkitX's staff:

> *Thank you for visiting our official sites. Our official websites are provided free of charge, and we do not like to place ads on the home page affecting your reading. If you like this project, please [donate to us](https://nukkitx.com/donate). All the donations will only be used for Nukkit websites and services.*

Build JAR file
-------------
- `git clone https://github.com/PowerNukkit/PowerNukkit`
- `cd PowerNukkit`
- `git submodule update --init`
- `./mvnw clean package`

The compiled JAR can be found in the `target/` directory.

Running
-------------
Simply run `java -jar powernukkit-<version>.jar` **in an empty folder**.

Plugin API
-------------
Information on Nukkit's API can be found at the [wiki](https://nukkitx.com/wiki/index/).

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
