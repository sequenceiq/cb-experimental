Warmed container for Ambari and HDP
==========

This repository contains a Docker file to build a Docker image containing Ambari and already downloaded HDP rpm packages.

##Pull the image from Docker Repository
```
docker pull sequenceiq/ambari-warmup:2.0.0-consul
```

## Building the image
```
docker build --rm -t sequenceiq/ambari-warmup:2.0.0-consul .
```
