# MarketplaceOnStatefun

```
This is an IN-PROGRESS work. 
Details about how to appropriately configure, deploy, and execute 
the application are being added progressively.
```

MarketplaceOnStatefun is the Statefun port of Marketplace, the application prescribed as part of a microservice-based
benchmark being designed by the [Data Management Systems (DMS) group](https://di.ku.dk/english/research/sdps/research-groups/dms/) at the University of Copenhagen.
Further details about the benchmark can be found [here](https://github.com/diku-dk/EventBenchmark).

## Table of Contents
- [Getting Started](#getting-started)
    * [New Statefun Users](#statefun)
    * [Docker Preliminaries](#docker)
    * [Play Around](#play)
- [Running the Benchmark](#running-benchmark)

## <a name="getting-started"></a>Getting Started

### <a name="statefun">New Statefun Users

[Statefun](https://github.com/apache/flink-statefun) provides a runtime to program functions that necessitate managing state as part of their execution. It is built on top of Apache Flink and inherits all the benefits brought about by the stream processing engine.
We highly recommend starting from the [Statefun documentation](https://nightlies.apache.org/flink/flink-statefun-docs-master/) and project examples, that can be found in the [Stateful Functions Playground repository](https://github.com/apache/flink-statefun-playground).

### <a name="docker">Docker Preliminaries

This port runs based on the project [Statefun Playground](https://github.com/apache/flink-statefun-playground). This decision is made to facilitate the packaging of dependencies, the deployment scheme, and the collection of performance metrics.

The original [statefun-playground](https://hub.docker.com/r/apache/flink-statefun-playground/) Docker image runs with default Flink and JVM parameters, which can lead to performance issues. 
This way, we suggest advanced users to either generate a custom image from the [source code](https://github.com/apache/flink-statefun-playground/tree/main/playground-internal/statefun-playground-entrypoint) (class LocalEnvironmentEntryPoint.java) or overwrite the [Flink parameters](https://github.com/apache/flink-statefun-playground/blob/main/playground-internal/statefun-playground-entrypoint/README.md).

Once statefun-playground source code is modified, proceed as follows:

In the flink-statefun-playground root's folder, run:
```
docker build -t flink-statefun-playground-custom .
```

Then go to MarketplaceOnStatefun root's folder and run:
```
docker-compose -f docker-compose-custom.yml build
```

```
docker-compose -f docker-compose-custom.yml up
```

In case you prefer to run MarketplaceOnStatefun with the original statefun-playground image, the commands above are not necessary, so you just run:

```
docker-compose build
```

```
docker-compose up
```

After these commands, the application is probably up and running.

### <a name="play">Play Around!



If you want to modify the code, you can perform a hot deploy by running
```
docker-compose up -d --build marketplace
```

## <a name="getting-started"></a>Running the Benchmark

1. Make sure that MarketplaceOnStatefun is up and running
2. 

