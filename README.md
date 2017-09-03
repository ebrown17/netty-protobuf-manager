## A small Netty library built for sending and receiving google protocol buffers. 

For custom protocol buffer data, create a proto file in the folder `protobuf_files` and run the script `generate_proto_files`

This will output your custom protocol buffer into the package `src.main.java.protobuf`

You can create custom Client and Server data handler classes that extend the protobuffer type you just created to use

Using ClientDataHandler and ServerDataHandler as examples

`protobufauditor.proto` generates `JdssAuditor.java`. A custom protocol I will be using to send messages to BART platform signs


