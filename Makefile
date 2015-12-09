
build: clean-build-folder
	echo build java projects
	./gradlew clean build --stacktrace
release-dev: clean-build-folder
	./gradlew clean build release uploadArchives -Prelease.scope=minor -Prelease.stage=dev --info --stacktrace
release-rc: clean-build-folder
	./gradlew clean build release uploadArchives -Prelease.scope=minor -Prelease.stage=rc --refresh-dependencies --info --stacktrace
release-rc-patch: clean-build-folder
	./gradlew clean build release uploadArchives -Prelease.scope=patch -Prelease.stage=rc --refresh-dependencies --info --stacktrace
release: clean-build-folder
	./gradlew clean build release uploadArchives -Prelease.scope=minor -Prelease.stage=final --refresh-dependencies --info --stacktrace
build-containers:
	: ${CUSTOM_TAG:=custom}
	echo build containers with $(CUSTOM_TAG) tags.
	ROOT=$(pwd)
	find . -name "Dockerfile"|while read fname; do
	  echo $fname
	  DIR=$(dirname "${fname}") && cd $DIR && LAST_DIR=$(basename $PWD) && CONTAINER_NAME=$(echo $LAST_DIR|sed 's,docker-,,g') && docker build -t $CONTAINER_NAME:$CUSTOM_TAG . && cd $ROOT
	done
clean-build-folder:
	rm -rf build/
images: $(TARGET)
	cd ./cloudbreak-images && make $(TARGET)
git-update:
	git submodule update
