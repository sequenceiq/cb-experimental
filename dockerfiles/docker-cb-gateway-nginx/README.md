# nginx-cb-gateway

This is a docker image for an nginx server that's used by [Cloudbreak](https://github.com/sequenceiq/cloudbreak) to reverse proxy requests made from the Cloudbreak deployment to the clusters in the cloud.
It contains a hardcoded configuration with the following endpoints:

- /docker - proxied to the docker daemon 
- /swarm  - proxied to the swarm manager
- /v1     - proxied to the local consul server
- /       - proxied to Ambari server

### To start it on the gateway node:
```
docker run --name gateway-proxy -d --net=host -p 80:80 sequenceiq/nginx-cb-gateway
```
