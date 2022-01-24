# XML to JSON conversion project (coding test for the job)

##  Run notes
- import maven dependencies with pom.xml provided
- run com.github.ko4evneg.Bootstrap class

**IMPORTANT:** For correct test in IDEA, run configuration should have working directory set to $projectdir/target/classes directory.

## Description
This program:
- provides HTML UI form for submitting XML at "/"
- validate XML is well-formed
- parse this XML to Java object 
- convert XML to JSON
- transform JSON into tcp packet's payload according requirements
- send tcp packet with binary representation of payload
- logs all events along the run into the file and console

###  TCP packet payload requirements
| Item | Description | Format and Value |
| --- | --- | --- |
|Header|fixed magic|FFBBCCDD|
|Length|variable json length|4 bytes little endian integer|
|Json|byte[] of json stringv|utf16-le charset|

##  Technologies used

- HTTP protocol
- TCP sockets
- Basic HTML forms
- JAXB
- HTTP Servlets
- Jetty Servlet Container
- Log4J
- Jackson lib
