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

**PowerNukkit** is a modified version of Nukkit that adds support to a huge amount of features like water-logging, all new blocks, more plugin events, offhand slot, bug fixes and many more.

Please note that PowerNukkit is not handled by Cloudburst's staff and is provided with love but without warranty. If you find any issue while running PowerNukkit you should [create a new issue](https://github.com/PowerNukkit/PowerNukkit/issues) in this repository first.

It's also advisable to have a backup schedule set up, use plugins that are made for PowerNukkit and use only stable PowerNukkit versions.

Most Cloudburst Nukkit plugins are supported but they may not understand the new blocks and items and the new stuff that you can do with PowerNukkit.

Links
--------------------

* __[PowerNukkit Website](https://powernukkit.org/)__
* __[PowerNukkit Forum and Guides](https://discuss.powernukkit.org/)__
* __[Download PowerNukkit Releases](https://powernukkit.org/releases)__
* __[Download PowerNukkit Snapshots](https://powernukkit.org/snapshots)__
* __[PowerNukkit Discord](https://powernukkit.org/discord)__
* __[Cloudburst Nukkit Plugins](https://cloudburstmc.org/resources/categories/nukkit-plugins.1/)__

Creating Plugins
----------------
Add PowerNukkit as dependency, it's hosted by maven central and jcenter, so you don't need to specify a custom repository.

[Click here to see the full gradle example](https://github.com/PowerNukkit/ExamplePlugin-Gradle)
```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile group: 'org.powernukkit', name: 'powernukkit', version: '1.5.0.0-PN'
}
```

[Click here to see the full maven example](https://github.com/PowerNukkit/ExamplePlugin-Maven)
```xml
<dependencies>
    <dependency>
        <groupId>org.powernukkit</groupId>
        <artifactId>powernukkit</artifactId>
        <version>1.5.0.0-PN</version>
    </dependency>
</dependencies>
```

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

But to get the best performance on larger public servers, this longer command will be better:
```sh
java -Xms10G -Xmx10G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar powernukkit-<version>-shaded.jar
```

Adjust the -Xmx and -Xms settings and the jar name in the end of the command as needed.

Check [this page](https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/) for information about the arguments above.

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
docker-compose run --rm --service-ports --name powernukkit server
```

But if you want to keep using the same version and update, use this command to create a fixed container

```sh
docker-compose run --service-ports --name powernukkit server
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
* _bleeding_ (⚠️ **use with care, may contains unstable code!** ⚠️)
* 1.5.0.0, 1.5.0, 1.5, 1, latest
* 1.4.0.0, 1.4.0, 1.4
* 1.3.1.5, 1.3.1, 1.3
* 1.3.1.4
* 1.3.1.3
* 1.3.1.2
* 1.3.1.1
* 1.3.0.1, 1.3.0
* 1.2.1.0, 1.2.1, 1.2
* 1.2.0.2, 1.2.0
* 1.1.1.1, 1.1.1, 1.1
* 1.1.1.0

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

---------

![](https://www.yourkit.com/images/yklogo.png)  
YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.  
YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/),
[YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/),
and [YourKit YouMonitor](https://www.yourkit.com/youmonitor/).
