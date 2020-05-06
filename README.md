[![Project status](https://img.shields.io/badge/Project%20status-stable%20%26%20actively%20maintaned-green.svg)](https://github.com/homecentr/docker-dns-exporter/graphs/commit-activity) 
[![](https://img.shields.io/github/issues-raw/homecentr/docker-dns-exporter/bug?label=open%20bugs)](https://github.com/homecentr/docker-dns-exporter/labels/bug) 
[![](https://images.microbadger.com/badges/version/homecentr/dns-exporter.svg)](https://hub.docker.com/repository/docker/homecentr/dns-exporter)
[![](https://img.shields.io/docker/pulls/homecentr/dns-exporter.svg)](https://hub.docker.com/repository/docker/homecentr/dns-exporter) 
[![](https://img.shields.io/docker/image-size/homecentr/dns-exporter/latest)](https://hub.docker.com/repository/docker/homecentr/dns-exporter)

![CI/CD on master](https://github.com/homecentr/docker-dns-exporter/workflows/CI/CD%20on%20master/badge.svg)
![Regular Docker image vulnerability scan](https://github.com/homecentr/docker-dns-exporter/workflows/Regular%20Docker%20image%20vulnerability%20scan/badge.svg)


# HomeCenter - DNS exporter
[BIND9 DNS](https://www.isc.org/bind/) Prometheus [exporter](https://github.com/prometheus-community/bind_exporter).

## Usage

```yml
version: "3.7"
services:
  dns_exporter:
    build: .
    restart: unless-stopped
    ports:
      - "9119:9119/tcp"
```

## Configuration

The exporter needs to know on which URL to look for the BIND9 statistics. The url should be passed by the [command line argument](https://github.com/prometheus-community/bind_exporter/blob/fc03b6d2be741d30b2769368b7d5506d058e2609/bind_exporter.go#L472) `-bind.stats-url`. 

> The command line arguments also include an option to change the port on which the metrics are exposed. **Do not** do this as it breaks the health check. Instead map the port 9119 to any external port you like using Docker configuration.

### Configuring BIND to expose statistics

BIND9 does not expose the statistics by default. You need to include the [statistics-channels section](http://www.ipamworldwide.com/ipam/bind-stats.html) in your named.conf file.

## Environment variables

| Name | Default value | Description |
|------|---------------|-------------|
| PUID | 7077 | UID of the user dns-exporter should be running as. The UID must have sufficient rights to read from the Docker socket. |
| PGID | 7077 | GID of the user dns-exporter should be running as. You must set the PUID if you want to set the PGID variable. |
| EXPORTER_ARGS | | Command line arguments for the exporter binary |

## Exposed ports

| Port | Description |
|------|-------------|
| 9119/tcp | Prometheus metrics over HTTP |

## Volumes

The container does not expose any volumes.

## Security
The container is regularly scanned for vulnerabilities and updated. Further info can be found in the [Security tab](https://github.com/homecentr/docker-dns-exporter/security).

### Container user
The container supports privilege drop. Even though the container starts as root, it will use the permissions only to perform the initial set up. The dns-exporter process runs as UID/GID provided in the PUID and PGID environment variables.

:warning: Do not change the container user directly using the `user` Docker compose property or using the `--user` argument. This would break the privilege drop logic.