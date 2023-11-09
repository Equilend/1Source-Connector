#!/bin/bash
# exec - executing a command
# 3<> - creating a file descriptor 3 and connecting it to a network socket
# /dev/tcp/127.0.0.1/8080 - host and port to connect to
exec 3<>/dev/tcp/127.0.0.1/8080

# echo - sending HTTP request to the network socket
# -e - escaping symbols '\n'
# GET /health/ready HTTP/1.1\nhost: 127.0.0.1:8080\n - send GET request
# >&3 - redirecting echo output to the previously created file descriptor 3
echo -e "GET /health/ready HTTP/1.1\nhost: 127.0.0.1:8080\n" >&3

# timeout --preserve-status 1 - runs the next command 'cat' for one-second time limit
# and ensures that the exit status of the 'cat' command is preserved
# cat <&3 - reading the response from file descriptor 3
# grep -m 1 status - searching for the word 'status' in the response
# Expecting keycloak health-check response: { "status": "UP", "checks":[] }
# grep -m 1 UP - searching for the word 'UP' and stop after first match.
# ERROR=$? - the last command in the previous lane is stored to the ERROR variable
timeout --preserve-status 1 cat <&3 | grep -m 1 status | grep -m 1 UP
ERROR=$?

# exec 3<&- - closing file descriptor 3 to release the network socket connection
# exec 3>&- - closing the network in write mode
exec 3<&-
exec 3>&-

exit $ERROR