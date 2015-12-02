# Docker, dig, consul

This is a description on how to use [Consul](https://www.consul.io) in Docker so that it can serv other containers,
as a DNS server.

## start consul

To start a consul container locally on boot2docker:
```
$ BRIDGE_IP=$(docker run --rm debian:jessie ip ro | grep default | cut -d" " -f 3)
$ docker run -d \
    -h node1 \
    --name=consul \
    -p ${BRIDGE_IP}:53:53/udp \
    -p ${BRIDGE_IP}:8400:8400 \
    -p ${BRIDGE_IP}:8500:8500 \
    sequenceiq/consul:v0.4.1.ptr -server -bootstrap
```

To see that its started up:
```
$ docker exec -it consul consul members

Node   Address          Status  Type    Build     Protocol
node1  172.19.0.4:8301  alive   server  0.4.1ptr  2
```

To see the logs of the consul use: `docker logs consul`

## Using consul as dns server in containers

Now you can start other containers which uses consul as DNS server 
```
docker run -it --rm \
  --dns=$BRIDGE_IP \
  --dns=8.8.8.8 \
  --dns-search=node.consul \
  --dns-search=service.consul \
  sequenceiq/busybox
```

In that container you are able to get the consul A record via the DNS api
```
dig consul.service.consul
```

Getting the members via the HTTP api:
```
curl $(dig consul.service.consul +short):8500/v1/agent/members|jq .
```

##

If you want that all future containers use the dns and dns-search settings, you have to add the 
following options to docker daemon:
`--dns=<BRIDGE_IP> --dns=8.8.8.8 --dns-search=node.consul --dns-search=service.consul`

For boot2docker you can use the `EXTRA_ARGS` env variable in `/var/lib/boot2docker/profile`. Instead of
hand editing, here is the one-liner:
```
boot2docker ssh sudo "sed '$ a\EXTRA_ARGS=\"\$EXTRA_ARGS --dns=$BRIDGE_IP --dns=8.8.8.8 --dns-search=node.consul --dns-search=service.consul\"' /var/lib/boot2docker/profile"
boot2docker restart
```

## Registrator

The next component to add to the secret sauce is [registrator](https://github.com/progrium/registrator)
This will automatically register docker containers as services into consul. Registrator attaches to
docker event stream,

```
docker run -d \
  --name=registrator \
  -v /var/run/docker.sock:/tmp/docker.sock \
  progrium/registrator consul://${BRIDGE_IP}:8500
```

## Starting autoregistered services

Now if we start a postgres container, it will be automatically registered, because it exposes a port:
```
docker run -d \
  --name="postgresql" \
  -e "SERVICE_NAME=db" \
  -e "SERVICE_TAGS=master,backups" \
  -p 5432:5432 \
  postgres
```

To check that it's registered query the whole consul catalog:

```
docker run -it  sequenceiq/busybox bash -c "curl -s $BRIDGE_IP:8500/v1/catalog/services|jq ."
```

# tl;dr

If you want to know the fine details read on ...

## Understanding /etc/resolv.conf

```
nameserver 172.19.0.1
search node.consul service.consul
```

when not specified otherwise cli tools like: `dig`, `host`, `nslookup` will talk to **nameserver**.
If you want to spare a couple of keystrokes, You can define the **search** domains, they will be appended to
the short name

```
$ nslookup consul
Server:         172.19.0.1
Address:        172.19.0.1#53

Name:   consul.service.consul
Address: 172.19.0.18
```

dig doesn't honors **search** domanis by default, you have to force it with the `+search` option

```
$ dig +search consul +short
172.19.0.18
```

## dig aliases

The firs alias to show **query** and **answer** sections only.
The second alias produces the minimal output (cli-fu) with dns-search enabled.
```
alias digq='dig +search +nocmd +noall +answer +que'
alias digs='dig +search +short'
```

