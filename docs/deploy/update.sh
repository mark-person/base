#!/bin/bash
rm -rf ./BOOT-INF/classes;
unzip -q repository-0.0.1-SNAPSHOT.jar.original -d ./BOOT-INF/classes;
zip -qd repository-0.0.1-SNAPSHOT.jar BOOT-INF/classes/*;
zip -ur repository-0.0.1-SNAPSHOT.jar ./BOOT-INF/*;