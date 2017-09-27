rem TODO setup batch once *.protos are in place

cd F:\virtex\jeets\jeets-protocols

protoc --proto_path=protobuffers --java_out=src\main\java protobuffers\WorldClockProtocol.proto

protoc --proto_path=protobuffers --java_out=src\main\java protobuffers\jeetsGpsMessages.proto

protoc --proto_path=protobuffers --java_out=src\main\java protobuffers\traccar.proto