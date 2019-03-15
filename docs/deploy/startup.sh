#!/bin/sh
JDK_PATH="/home/deploy/jdk/jdk1.8.0_131/"
APP_PATH="/home/deploy/"
LOG_PATH="/home/deploy/log/"
JAVA_OPTS="-server -Xmx1024m -Xms256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_PATH}OutOfMemory_`date +"%Y%m%d"`.dump"

read oldPid < ${APP_PATH}pid
kill -9 ${oldPid}
LOG_FILE=${LOG_PATH}log_`date +"%Y%m%d"`.txt
nohup ${JDK_PATH}bin/java -Dspring.profiles.active=client ${JAVA_OPTS} -jar ${APP_PATH}deploy-0.1.0.jar  >> ${LOG_FILE} 2>&1 &
pid=$!
echo ${pid} > ${APP_PATH}pid
ps ${pid}
echo 'Start Begin! >>>>>>>>>>>>'
tail -f ${LOG_FILE}



