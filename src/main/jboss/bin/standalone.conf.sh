## JBoss Bootstrap Script Configuration — Linux

if [ "x$JAVA_OPTS" != "x" ]; then
    echo "JAVA_OPTS already set in environment; overriding default settings with values: $JAVA_OPTS"
else
    # Java 8 requis pour ManyPrices
    export JAVA_HOME=/opt/jdk1.6.0_45
    export PATH=$JAVA_HOME/bin:$PATH

    JAVA_OPTS="-Xms64M -Xmx512M -XX:MaxPermSize=256M"
    JAVA_OPTS="$JAVA_OPTS -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.net.preferIPv4Stack=true"
    JAVA_OPTS="$JAVA_OPTS -Dorg.jboss.resolver.warning=true"
    JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=org.jboss.byteman"
    JAVA_OPTS="$JAVA_OPTS -Djboss.server.default.config=standalone.xml"
fi
