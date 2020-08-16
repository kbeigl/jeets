Examples taken from the Protobuf Java tutorial https://developers.google.com/protocol-buffers/docs/javatutorial

Traccar.java generation (if your operating system is not supporting by Protobuf Java code generator maven plugin)
	cd src/test/resources
	protoc --java_out=. ./traccar.proto