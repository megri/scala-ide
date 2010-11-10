#!/bin/sh

. $(dirname $0)/env.sh

SCALA_VERSION=2.8.2-SNAPSHOT

${MAVEN} \
  -U \
  -P local-scala-2.8.2,!scala-2.8.x \
  -Dscala.version=${SCALA_VERSION} \
  clean install $*
