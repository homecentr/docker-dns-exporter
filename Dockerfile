FROM golang:1.15.3 as metrics-build

# Download exporter source code
RUN go get github.com/digitalocean/bind_exporter

# Build the source code
WORKDIR /go/src/github.com/digitalocean/bind_exporter
RUN make	

FROM homecentr/base:2.4.3-alpine

ENV EXPORTER_ARGS=""

# Required for the health checks
RUN apk add --no-cache curl=7.69.1-r0

# Copy the binary
COPY --from=metrics-build /go/src/github.com/digitalocean/bind_exporter/bind_exporter /usr/bin/bind_exporter

# Copy the s6 configuration
COPY ./fs/ /

HEALTHCHECK --interval=15s --timeout=10s --start-period=5s --retries=3 CMD curl -k --fail http://127.0.0.1:9119/metrics || exit 1

# Prometheus metrics
EXPOSE 9119/tcp

ENTRYPOINT [ "/init" ]