![nukkit](https://raw.githubusercontent.com/PowerNukkit/PowerNukkit/master/.github/images/banner.png)

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

Use the JAR that ends with `-shaded` to run your server.

Running
-------------
Simply run `java -jar powernukkit-<version>-shaded.jar` **in an empty folder**.

Plugin API
-------------
Information on Nukkit's API can be found at the [wiki](https://nukkitx.com/wiki/index/).

Docker
-------------
Running PowerNukkit in [Docker](https://www.docker.com/):

Run these commands in terminal or cmd: (copy & paste everything at once may work)
```sh
mkdir my-server
cd my-server
curl -sSL https://raw.githubusercontent.com/PowerNukkit/PowerNukkit/master/docker-compose.yml > docker-compose.yml
```

If you want to keep your server always updated when it restarts, run with:   
(edit the docker-compose.yml file to choose the base version you want)
```sh
docker-compose run --rm --name powernukkit server
```

But if you want to keep using the same version and update, use this command to create a fixed container

```sh
docker-compose run --name powernukkit server
```

<b>To return to the terminal and keep the server running:</b>  
Keep holding <kbd>CTRL</kbd>, press <kbd>P</kbd>, release <kbd>P</kbd>, press <kbd>Q</kbd>, release <kbd>Q</kbd>, and release <kbd>CTRL</kbd>


Managing your server after the docker installation:  
(these commands only works if you created a fixed container)
```sh
# Starts your server, use CTRL+P+Q to detach without stopping
docker start powernukkit -i
# Attach a detached server
docker attach powernukkit
# Stops your server with system signal
docker stop powernukkit
# Uninstall the container (keeps the data), useful to update your server
docker rm powernukkit
```

Check the [docker-compose.yml](docker-compose.yml) file for more details.

### Supported tags
* _bleeding_ (⚠️ **use with care, may contains unstable code!**)
* 1.3.1.1, 1.3.1, 1.3, 1, latest
* 1.3.0.1, 1.3.0
* 1.2.1.0, 1.2.1, 1.2
* 1.2.0.2, 1.2.0
* 1.1.1.1, 1.1.1, 1.1
* 1.1.1.0

Contributing
------------
Please read the [CONTRIBUTING](.github/CONTRIBUTING.md) guide before submitting any issue. Issues with insufficient information or in the wrong format will be closed and will not be reviewed.
