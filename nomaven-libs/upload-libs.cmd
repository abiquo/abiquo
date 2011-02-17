call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=vboxws -Dversion=3.0.8 -Dpackaging=jar -Dfile=vboxws-java-3.0.8.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=wiseman -Dversion=1.6 -Dpackaging=jar -Dfile=wiseman16.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=wiseman-tools -Dversion=1.0 -Dpackaging=jar -Dfile=wiseman-tools.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=vboxws -Dversion=1.6 -Dpackaging=jar -Dfile=vboxws_java16.jar
call mvn install:install-file -DgroupId=org.virtualbox -DartifactId=vboxjws -Dversion=4.0.0 -Dpackaging=jar -Dfile=$DIRNAME/vboxjws.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=flex-messaging-common -Dversion=3.0 -Dpackaging=jar -Dfile=flex-messaging-common-3.0.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=flex-messaging-core -Dversion=3.0 -Dpackaging=jar -Dfile=flex-messaging-core-3.0.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=flex-messaging-opt -Dversion=3.0 -Dpackaging=jar -Dfile=flex-messaging-opt-3.0.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=flex-messaging-proxy -Dversion=3.0 -Dpackaging=jar -Dfile=flex-messaging-proxy-3.0.jar
call mvn install:install-file -DgroupId=com.abiquo.nomavenlibs -DartifactId=flex-messaging-remoting -Dversion=3.0 -Dpackaging=jar -Dfile=flex-messaging-remoting-3.0.jar
call mvn install:install-file -DgroupId=com.asfusion -DartifactId=Mate -Dversion=0.8.8.1 -Dpackaging=swc -Dfile=Mate_08_8_1.swc
call mvn install:install-file -DgroupId=com.adobe -DartifactId=as3corelib -Dversion=0.92.1 -Dpackaging=swc -Dfile=as3corelib.swc
call mvn install:install-file -DgroupId=com.google -DartifactId=map_flex -Dversion=1.16 -Dpackaging=swc -Dfile=map_flex_1_16.swc
call mvn install:install-file -DgroupId=com.adobe.flex.sdk -DartifactId=datavisualization -Dversion=3.4.0 -Dpackaging=swc -Dfile=datavisualization-3.4.0.swc
call mvn install:install-file -DgroupId=com.adobe.flex.sdk -DartifactId=datavisualization -Dversion=3.4.0 -Dpackaging=rb.swc -Dfile=datavisualization-3.4.0_rb.swc
call mvn install:install-file -DgroupId=com.adobe.flex.sdk -DartifactId=datavisualization -Dversion=3.4.0 -Dpackaging=rb.swc -Dfile=datavisualization-3.4.0_rb.swc -Dclassifier=en_US
call mvn install:install-file -DgroupId=com.adobe.flex.sdk -DartifactId=datavisualization -Dversion=3.4.0 -Dpackaging=rb.swc -Dfile=datavisualization-3.4.0_rb.swc -Dclassifier=es_ES
call mvn install:install-file -DgroupId=com.adobe.flex.sdk -DartifactId=datavisualization -Dversion=3.4.0 -Dpackaging=rb.swc -Dfile=datavisualization-3.4.0_rb.swc -Dclassifier=ja_JP
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=framework -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=framework-3.4.0_rb.swc
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=framework -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=framework-3.4.0_rb.swc -Dclassifier=en_US
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=framework -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=framework-3.4.0_rb.swc -Dclassifier=es_ES
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=framework -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=framework-3.4.0_rb.swc -Dclassifier=ja_JP
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=rpc -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=rpc-3.4.0_rb.swc
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=rpc -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=rpc-3.4.0_rb.swc -Dclassifier=en_US
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=rpc -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=rpc-3.4.0_rb.swc -Dclassifier=es_ES
call mvn install:install-file -DgroupId=com.adobe.flex.framework -DartifactId=rpc -Dversion=3.4.0.9271 -Dpackaging=rb.swc -Dfile=rpc-3.4.0_rb.swc -Dclassifier=ja_JP
call mvn install:install-file -DgroupId=com.adobe.flex.compiler -DartifactId=license -Dversion=3.4.0 -Dpackaging=jar -Dfile=license.jar
call mvn install:install-file -DgroupId=com.abiquo.heartbeat -DartifactId=heartbeat-client -Dversion=0.1-SNAPSHOT -Dpackaging=jar -Dfile=heartbeat-client-0.1-SNAPSHOT.jar

call mvn install:install-file -DgroupId=com.abiquo.heartbeat -DartifactId=heartbeat-server-shared -Dversion=0.1-SNAPSHOT -Dpackaging=jar -Dfile=heartbeat-server-shared-0.1-SNAPSHOT.jar
call mvn install:install-file -DgroupId=j-interop -DartifactId=j-interop -Dversion=2.06 -Dpackaging=jar -Dfile=j-interop-2.06.jar
call mvn install:install-file -DgroupId=j-interop -DartifactId=j-interopdeps -Dversion=2.06 -Dpackaging=jar -Dfile=j-interopdeps-2.06.jar
call mvn install:install-file -DgroupId=com.hyper9 -DartifactId=jwbem -Dversion=0.0.1 -Dpackaging=jar -Dfile=jwbem-0.0.1.jar
call mvn install:install-file -DgroupId=org.samba.jcifs -DartifactId=jcifs -Dversion=1.2.19 -Dpackaging=jar -Dfile=jcifs-1.2.19.jar
call mvn install:install-file -DgroupId=com.softwarementors.bzngine -DartifactId=bzngine -Dversion=0.9.2-SNAPSHOT -Dpackaging=jar -Dfile=bzngine-0.9.2-SNAPSHOT.jar
call mvn install:install-file -DgroupId=redis.clients -DartifactId=jedis -Dversion=1.0.0-RC5 -Dpackaging=jar -Dfile=jedis-1.0.0-RC5.jar
call mvn install:install-file -DgroupId=org.apache -DartifactId=thrift -Dversion=0.2.0 -Dpackaging=jar -Dfile=thrift.jar
call mvn install:install-file -DgroupId=com.xensource -DartifactId=xenserver -Dversion=5.6.0-1 -Dpackaging=jar -Dfile=xenserver-5.6.0-1.jar
call mvn install:install-file -DgroupId=com.degrafa -DartifactId=degrafa -Dversion=3.1 -Dpackaging=swc -Dfile=degrafa-3.1.swc
call mvn install:install-file -DgroupId=com.flexspy -DartifactId=flexspy -Dversion=1.3 -Dpackaging=swc -Dfile=flexspy-1.3.swc
call mvn install:install-file -DgroupId=redis.clients -DartifactId=johm -Dversion=0.5.0-SNAPSHOT -Dpackaging=jar -Dfile=johm-0.5.0-SNAPSHOT.jar
