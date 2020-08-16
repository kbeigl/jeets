
# run dcs with etl consumer

current=`pwd`

echo --------------------------
echo run dcs with etl consumer
echo --------------------------

etl-folder=`pwd`
etl-libs=`$etl-folder/libs`

java -cp jeets-dcs-traccar-1.3-SNAPSHOT-exec.jar -Dloader.path="file:///$etl-folder/target/ptc-etl-1.0-SNAPSHOT.jar, file:///$etl-libs" org.springframework.boot.loader.PropertiesLauncher .\setup\traccar.xml
