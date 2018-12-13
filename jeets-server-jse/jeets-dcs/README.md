
JeeTS Device Communication Server
========
This project is a Device Communication Server to receive JeeTS Protobuffer messages
defined in jeets-protocols. The implementation is kept as simple as possible 
by applying the Netty4 TCP framework in conjunction with the Camel framework.

Please note that the "jeets-dcs://device" Camel Component described in the book 
was removed from this project and can be found in earlier versions.

Also the camel-protobuf test was removed as it was not relevant 
for the chosen DCS implementation with camel-netty4 
(implying netty4 with protobuffer conversion).

This way the reader can focus on the actual minimum implementation needed
to receive and transform messages.

The "jeets-dcs://" Camel Component will be rolled out in a completely new project
to deal with more than one protocol, configuration options etc.