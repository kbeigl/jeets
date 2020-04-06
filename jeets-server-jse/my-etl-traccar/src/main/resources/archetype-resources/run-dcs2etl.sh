
# run dcs with etl consumer

current=`pwd`

echo --------------------------
echo run dcs with etl consumer
echo --------------------------

etl-folder=`pwd`
etl-libs=`$etl-folder/libs`

java -cp jeets-dcs-traccar-1.3-SNAPSHOT-exec.jar -Dloader.path="file:///$etl-folder/target/ptc-etl-1.0-SNAPSHOT.jar, file:///$etl-libs/tomcat-jdbc-9.0.31.jar, file:///$etl-libs/tomcat-juli-9.0.31.jar, file:///$etl-libs/postgresql-42.2.0.jre6.jar" org.springframework.boot.loader.PropertiesLauncher .\setup\traccar.xml
