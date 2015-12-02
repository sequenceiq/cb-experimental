## Generating

quick note for next time/automation
```
export DBNAME=uaadb
export ver=2.7.1
export GITHUB_ACCESS_TOKEN=XXX


cbd delete
cbd generate

# delete users from uaa.yml
sed -i.bak "/users:/,$ d" uaa.yml

/bin/cp -f docker-compose.yml docker-compose-uaa.yml
sed -i "/^logspout:/,/^ *$/ d; /^logsink:/,/^ *$/ d; /^cbdb:/,$ d;" docker-compose-uaa.yml

.deps/bin/docker-compose -f ./docker-compose-uaa.yml up -d

## wait for uaa up:
curl 127.0.0.1:8089/healthz

# export db

rm -rf release
mkdir -p releases

dbid=$(.deps/bin/docker-compose -f ./docker-compose-uaa.yml ps -q uaadb)
docker exec $dbid tar cz -C /var/lib/postgresql/data . > release/${DBNAME}-${ver}.tgz


curl -L https://github.com/progrium/gh-release/releases/download/v2.2.0/gh-release_2.2.0_linux_x86_64.tgz | tar -xz -C /usr/local/bin
/usr/local/bin/gh-release create sequenceiq/docker-${DBNAME} "${ver}"

```
