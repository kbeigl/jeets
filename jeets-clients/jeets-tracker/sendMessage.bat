echo off
rem goto https://raw.githubusercontent.com/tananaev/traccar/master/tools/test-integration.py
rem   or traccar3/server/test directory
rem to find valid message strings for different protocols

java -jar target\jeets-tracker-1.2.0-jar-with-dependencies.jar localhost 5001 "imei:359587010124999,help me,1201011201,,F,120100.000,A,4903.0000,N,1210.0000,E,0.00,;"
