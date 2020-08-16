@REM ----------------------------------------------------------------------------
@REM
@REM Copyright 2020 The Java EE Tracking System - JeeTS
@REM Copyright 2020 Kristof Beiglböck kbeigl@jeets.org
@REM
@REM The JEE Tracking System licenses this file to you under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

set "etl-folder=%cd%\"
set "etl-libs=%etl-folder%libs"
@REM replace jars with variables ...
java -cp jeets-dcs-traccar-1.3-SNAPSHOT-exec.jar -Dloader.path="file:///%etl-folder%target\ptc-etl-1.0-SNAPSHOT.jar, file:///%etl-libs%" org.springframework.boot.loader.PropertiesLauncher .\setup\traccar.xml

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off

exit