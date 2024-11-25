# JSnooper
Java instrumentation agent which enables to track object allocations in real time.

# Introduction
JSnooper is a Java instrumentation agent which enables to run object allocation microbenchmarks and track new objects allocated in JVM in real time.

JSnooper can be useful when developing ZeroGC applications to verify that certain code paths do not produce garbage. 

# Build
Requirements:
- JDK 1.8+
- Maven 3.9.6+

```
mvn install
```
# Usage

1. Run target java application with JSnooper instrumentation agent:

```
java -javaagent:jsnooper-1.0.jar=port=PORT -classpath app.jar APP_MAIN_CLASS ARGS
```

JSnooper will open simple Telnet interface on port _PORT_ which will enable to start/stop object tracking at runtime.

2. Use telnet to connect to JSnooper port:

```
telnet localhost PORT
```

When using Putty, select _Passive_ "Telnet negotiation mode" in Connections/Telnet config.

3. Start object tracking

To start object tracking type "start" `<ENTER>` in telnet session. From now on, all new objects created in the target JVM will be tracked. Bu default, tracking details will be written to STDOUT. 

To stop object tracking, type "stop" `<ENTER>`.

# Configuration

JSnooper instrumentation agent supports two optional configuration parameters:
* *port=PORT*: Specifies the port to open for telnet interface.
* *config=PATH*: Path to property file with configuration parameters.

Example:

```
java -javaagent:jsnooper-1.0.jar=port=5555,config=jsnooper.properties -classpath app.jar APP_MAIN_CLASS
```

Both config parameters are optional. Port can be specified in the command line or in the configuration file.

If configuration file is not specified, default setting will be used and tracking output written to STDOUT.

Configuration file is a standard java _properties_ file. All configuration parameters are optional. 

```
port=VALUE
leaf-classes=PREFIX_1,PREFIX_2,...,PREFIX_N
excluded-threads=PREFIX_1,PREFIX_2,...,PREFIX_N
included-threads=PREFIX_1,PREFIX_2,...,PREFIX_N
output-directory=PATH
output-file-prefix=TEXT
```

* *port=VALUE*: Specifies the port to open for telnet interface.
* *leaf-classes=VALUE*: Comma separated list of FQDN class names or prefixes. This is used when printing object allocation paths to make them more concise. Once a leaf class (or prefix) is encountered the allocation path will end. 
* *excluded-threads=VALUE*: Excludes given threads - objects allocated by these threads will not be tracked.
* *included-threads=VALUE*: Includes given threads - objects allocated by these threads will be tracked. Has higher precedence than _excluded-threads_.
* *output-directory=VALUE*: Path where output tracking files will be written.
* *output-file-prefix=VALUE*: Prefix used when creating tracking files. Current timestamp will be used as suffix.

If both _output-directory_ and _output-file-prefix_ are not specified, tracking data will be written to STDOUT.

Port must be specified either in the command line or in the configuration file. Otherwise the telnet interface will be unavailable.   
