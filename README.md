![nukkit](.github/images/banner.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Build Status](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/badge/icon)](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/)
![Tests](https://img.shields.io/jenkins/t/https/ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master.svg)
[![Discord](https://img.shields.io/discord/393465748535640064.svg)](https://discord.gg/5PzMkyK)

Introduction
-------------

Nukkit is nuclear-powered server software for Minecraft: Pocket Edition.
It has a few key advantages over other server software:

* Written in Java, Nukkit is faster and more stable.
* Having a friendly structure, it's easy to contribute to Nukkit's development and rewrite plugins from other platforms into Nukkit plugins.

Nukkit is **under improvement** yet, we welcome contributions.

Links
--------------------

* __[News](https://nukkitx.com)__
* __[Forums](https://nukkitx.com/forums)__
* __[Discord](https://discord.gg/5PzMkyK)__
* __[Download](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master)__
* __[Plugins](https://nukkitx.com/resources/categories/nukkit-plugins.1)__
* __[Wiki](https://nukkitx.com/wiki/nukkit)__

Build JAR file
-------------
- `git clone https://github.com/CloudburstMC/Nukkit`
- `cd Nukkit`
- `git submodule update --init`
- `chmod +x mvnw`
- `./mvnw clean package`

The compiled JAR can be found in the `target/` directory.

Running
-------------
Simply run `java -jar nukkit-1.0-SNAPSHOT.jar`.

Plugin API
-------------
Information on Nukkit's API can be found at the [wiki](https://nukkitx.com/wiki/nukkit/).

Docker
-------------

Running Nukkit in [Docker](https://www.docker.com/) (17.05+ or higher).

Build image from the source,

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

Kubernetes & Helm
-------------

Validate the chart:

`helm lint charts/nukkit`

Dry run and print out rendered YAML:

`helm install --dry-run --debug nukkit charts/nukkit`

Install the chart:

`helm install nukkit charts/nukkit`

Or, with some different values:

```
helm install nukkit \
  --set image.tag="arm64" \
  --set service.type="LoadBalancer" \
    charts/nukkit
```

Or, the same but with a custom values from a file:

```
helm install nukkit \
  -f helm-values.local.yaml \
    charts/nukkit
```

Upgrade the chart:

`helm upgrade nukkit charts/nukkit`

Testing after deployment:

`helm test nukkit`

Completely remove the chart:

`helm uninstall nukkit`


Contributing
------------
Please read the [CONTRIBUTING](.github/CONTRIBUTING.md) guide before submitting any issue. Issues with insufficient information or in the wrong format will be closed and will not be reviewed.
