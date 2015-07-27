# Demonstration of ActiveMQ Broker Network

This project demonstrates the use of the Broker Networks in ActiveMQ

Included is the configuration files to set up two ActiveMQ Brokers

There are also test classes to test message transmission to one broker and reception of messages from another broker using a Distributed Queue

There is also a class which uses the 'failover' protocol to allow the client to transmit and receive from a cluster with auto-failover.

JBoss Fuse which uses ActiveMQ also permits a Network of Brokers and two demonstration config files are included.

Note: The code is contained in Unit Tests and is best run in Eclipse as there are no command line parameters.

