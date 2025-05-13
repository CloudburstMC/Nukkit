![nukkit](.github/images/banner.png)

Introduction
-------------

Nukkit is nuclear-powered server software for Minecraft Bedrock Edition.
It has a few key advantages over other server software:

* Written in Java, Nukkit is faster and more stable.
* Having a friendly structure, it's easy to contribute to Nukkit's development and rewrite plugins from other platforms into Nukkit plugins.

Nukkit is under improvement yet, we welcome contributions.

Links
--------------------

* __[Forums](https://cloudburstmc.org/forums/)__
* __[Discord](https://discord.gg/5PzMkyK)__
* __[Wiki](https://cloudburstmc.org/wiki/nukkit)__
* __[Download Nukkit](https://dl.opencollab.dev/nukkit)__
* __[Download Plugins](https://cloudburstmc.org/resources/categories/nukkit-plugins.1/)__

Compile Nukkit
-------------
- `git clone https://github.com/CloudburstMC/Nukkit`
- `cd Nukkit`
- `./gradlew shadowJar`

The compiled JAR can be found in the `target/` directory.

Note: You don't need to compile Nukkit yourself if you don't intend to modify the code.

Running
-------------
Simply run `java -jar nukkit-1.0-SNAPSHOT.jar`. Make sure you have Java 11 or newer installed on your system.

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

Run once to generate the `nukkit-data` volume, default settings, and choose language,

```
docker run -it -p 19132:19132/udp -v nukkit-data:/data nukkit
```
Docker Compose
-------------

Use [docker-compose](https://docs.docker.com/compose/overview/) to start server on port `19132` and with `nukkit-data` volume,

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
