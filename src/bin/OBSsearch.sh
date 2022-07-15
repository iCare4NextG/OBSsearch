#!/bin/sh
# -------------------------------------------------------------------------
# OBSSEND  Launcher
# -------------------------------------------------------------------------

MAIN_CLASS=kr.irm.fhir.OBSsearch

DIRNAME="`dirname "$0"`"

# Setup $OBSSEARCH_HOME
if [ "x$OBSSEARCH_HOME" = "x" ]; then
    OBSSEARCH_HOME=`cd "$DIRNAME"/..; pwd`
fi

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA="java"
fi

# Setup the classpath
CP="$OBSSEARCH_HOME/etc/OBSsearch/"
for s in $OBSSEARCH_HOME/lib/*.jar
do
	CP="$CP:$s"
done

# Execute the JVM

exec $JAVA $JAVA_OPTS -cp "$CP" $MAIN_CLASS "$@"