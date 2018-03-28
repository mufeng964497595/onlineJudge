#!/bin/bash

# compile polling.cpp : g++ -Wall --static -std=c++11 polling.cpp -o polling -lmysqlcppconn 

hostname="IP"
username="database username"
passwd="database password"
dbname="database name"
judge="./judge.sh"
workdir="Docker workdir"

./polling ${hostname} ${username} ${passwd} ${dbname} ${judge} ${workdir} 
