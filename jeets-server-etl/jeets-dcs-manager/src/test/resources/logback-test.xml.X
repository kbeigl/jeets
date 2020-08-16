<?xml version="1.0" encoding="UTF-8"?>
<!-- By editing logback.xml you can gain full control over your application’s log files. -->
<configuration>
	<appender name="STDOUT"
		class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>
				%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
			</pattern>
		</encoder>
	</appender>
	<logger name="root" level="INFO" />
	<root level="INFO">
		<appender-ref ref="STDOUT" />
	</root>
</configuration>

<!-- 
<configuration>
	<include
		resource="org/springframework/boot/logging/logback/base.xml" />
	<logger name="org.springframework" level="OFF" />
</configuration>
 -->
<!-- strange behavior: this file turns off excessive io.netty and org.ap.camel 
	logging, but switching the level doesn't change anything -->