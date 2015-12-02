#!/bin/bash

DBNAME=cbdb
APP_NAME=CLOUDBREAK
: ${GITHUB_ACCESS_TOKEN:?"Please create GITHUB_ACCESS_TOKEN on GitHub https://github.com/settings/tokens/new"}
: ${DOCKERHUB_USERNAME:?"The DOCKERHUB_USERNAME environment variable must be set!"}
: ${DOCKERHUB_PASSWORD:?"The DOCKERHUB_PASSWORD environment variable must be set!"}

setup() {
  if ! gh-release -v &> /dev/null; then
    go get github.com/progrium/gh-release
  fi
}

start_db(){

  declare ver=${1:? version required}

  echo 'export PUBLIC_IP=1.1.1.1'>Profile
  echo "export DOCKER_TAG_${APP_NAME}=${ver}" >> Profile

  $(cbd env export | grep POSTGRES)

  docker run -d --name cbreak_${DBNAME}_1 postgres:${DOCKER_TAG_POSTGRES}
  cbd regenerate
  cbd migrate ${DBNAME} up
  if cbd migrate ${DBNAME} status 2>&1|grep "Migration SUCCESS" ; then
      echo Migration: OK
  else
      echo Migration: ERROR
      exit 1
  fi
}

db_backup() {

    declare ver=${1:? version required}

    # for gracefull shutdown: run another containe with --volumes from
    # docker exec ${DBNAME} bash -c 'kill -INT $(head -1 /var/lib/postgresql/data/postmaster.pid)'

    mkdir -p release
    docker exec  cbreak_${DBNAME}_1 tar cz -C /var/lib/postgresql/data . > release/${DBNAME}-${ver}.tgz
    docker rm -f cbreak_${DBNAME}_1
    cbd kill
}

clean() {
    rm -rf Profile *.yml release/
}

release() {
    declare ver=${1:? version required}
    gh-release create sequenceiq/docker-${DBNAME} "${ver}"
}

update_dockerfile() {
    declare ver=${1:? version required}

    sed -i "s/^ENV VERSION.*/ENV VERSION ${ver}/" Dockerfile
    git add Dockerfile
    git commit -m "Update Dockerfile to v${ver}"
    git push origin master
}

install_deps() {
  if ! dockerhub-tag --version &>/dev/null ;then
    echo "---> installing dockerhub-tag binary to /usr/local/bin" 1>&2
    curl -L https://github.com/progrium/dockerhub-tag/releases/download/v0.2.0/dockerhub-tag_0.2.0_Darwin_x86_64.tgz | tar -xz -C /usr/local/bin/
  else
    echo "---> dockerhub-tag already installed" 1>&2
  fi
}

trigger_image_build() {
  declare ver=${1:? version required}

  install_deps
  dockerhub-tag set sequenceiq/cbdb "${ver}" "v${ver}" /
}

main() {
    setup
    clean
    update_dockerfile "$@"
    start_db "$@"
    db_backup "$@"
    release "$@"
    trigger_image_build "$@"
}

[[ "$0" ==  "$BASH_SOURCE" ]] && main "$@"
