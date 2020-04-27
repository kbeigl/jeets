<?xml version="1.0" encoding="UTF-8"?>
<!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd                                   http://camel.apache.org/schema/spring       http://camel.apache.org/schema/spring/camel-spring.xsd">
    <bean class="org.jeets.device.multidevice.MultiDevice" id="MultiDevice"/>
    <bean class="org.jeets.device.multidevice.PrepareExchange" id="PrepareExchange"/>
    <camelContext id="camelContext-multidevice" xmlns="http://camel.apache.org/schema/spring">
        <route id="stream-in">
            <from id="fromCmdline" uri="stream:in?promptMessage=send HEX: "/>
            <to id="toParse1" uri="direct:parse.cmdline"/>
        </route>
        <route id="file-in">
            <!-- <from id="fromFile" uri="file://C:/kris/virtex/github.jeets/jeets-clients/jeets-device/src/test/resources/data?fileName=TwoServers.txt&amp;charset=UTF-8&amp;noop=true&amp;delay=1m30s"/> -->
            <!-- add move > done operations for itest assertions etc. -->
            <from id="fromFile" uri="file://send?charset=UTF-8&amp;move=sent"/>
            <to id="toParse2" uri="direct:parse.cmdline"/>
        </route>
        <route id="parse-cmdline">
            <from id="fromParseCmdline" uri="direct:parse.cmdline"/>
            <split id="splitLine2" streaming="true">
                <tokenize token="\n"/>
                <!-- <log id="logLine" message="textline: ${body}"/> -->
                <when id="whenNotComment">
                    <simple>${body.substring(0,1)} != '#'</simple>
                    <!-- TODO add check for empty lines -->
                    <!-- <log id="logLine2" message="prepare ExChange: ${body}"/> -->
                    <!-- when valid message -->
                    <to id="toPrepareExchange" uri="direct:set.attributes"/>
                    <to id="toDevice" uri="direct:device.send"/>
                    <!-- <log id="_log1" message="stream received HEX: ${body}"/> -->
                </when>
            </split>
        </route>
        <!-- TODO <route id="device-send-bytes" > ... -->
        <route id="device-send-hex">
            <from id="fromSender" uri="direct:device.send"/>
            <transform id="_transform1">
                <simple>${body.toUpperCase()}</simple>
            </transform>
            <choice id="_choice1">
                <when id="_when1">
                    <simple>${in.header.sync}</simple>
                    <log id="_log2" message=" request HEX: ${body}"/>
                    <to id="_to2" uri="direct:device.inout.hex"/>
                </when>
                <otherwise id="_otherwise1">
                    <log id="_log3" message=" send HEX: ${body}"/>
                    <to id="_to3" uri="direct:device.inonly.hex"/>
                </otherwise>
            </choice>
            <log id="_log4" message="end of route HEX: ${body}"/>
        </route>
    </camelContext>
</beans>
