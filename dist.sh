#!/usr/bin/env bash
activator stage -Dconfig.file=./conf/application_prod.conf -Dbackground=true
rm /home/todaytrend/www/NoChat-Server/target/universal/stage/RUNNING_PID
nohup target/universal/stage/bin/nochat &