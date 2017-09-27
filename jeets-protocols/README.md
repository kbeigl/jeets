JeeTS Protocols
===============
This project creates a jar module '[name?]' with google protobuffer java accessors.
The artefact includes the *.proto source files for easier debugging (remove if needed).

How to generate java files
==========================
Before running maven the java accessors have to be created with an installation of the protobuffer compiler 'protoc'.
	> protoc --version
	> libprotoc 3.1.0

To compile one or more *.proto files 
	> cd to dir with src folder
	> \jeets-protocols>protoc protobuffers/WorldClockProtocol.proto --java_out=src/main/java

	> \jeets-protocols>protoc --proto_path=./protobuffers --java_out=src/main/java 	
		protobuffers/WorldClockProtocol.proto

How to compile with patterns? 
eHor*.proto *.proto etc. ?
protoc --proto_path=/usr/local/include --proto_path=./csgo --ruby_out=./outlib csgo/**/*.proto

As an extra convenience, if the DST_DIR ends in .zip or .jar, the compiler will write the output to a single ZIP-format archive file with the given name. .jar outputs will also be given a manifest file as required by the Java JAR specification. 

Alternatively, if the .proto file contains:
	option optimize_for = LITE_RUNTIME;
then Foo will include fast implementations of all methods, but will implement the MessageLite interface, which only contains a subset of the methods of Message. In particular, it does not support descriptors or reflection. However, in this mode, the generated code only needs to link against libprotobuf-lite.jar instead of libprotobuf.jar. The "lite" library is much smaller than the full library, and is more appropriate for resource-constrained systems such as mobile phones.

TODO: check 'maven protobuf plugin' to create *.java files !!

Add @SuppressWarnings("all") to the generated code not to pollute IDE task list.
