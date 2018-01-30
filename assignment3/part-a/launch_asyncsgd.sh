#!/bin/bash
source tfdefs.sh
start_cluster startserver.py
# start multiple clients
stdbuf -o0 nohup python asyncsgd.py --task_index=0 > asynclog-0.out 2>&1&
sleep 5s # wait for variable to be initialized
echo 'Starting other nodes'
stdbuf -o0 nohup python asyncsgd.py --task_index=1 > asynclog-1.out 2>&1&
stdbuf -o0 nohup python asyncsgd.py --task_index=2 > asynclog-2.out 2>&1&
stdbuf -o0 nohup python asyncsgd.py --task_index=3 > asynclog-3.out 2>&1&
stdbuf -o0 nohup python asyncsgd.py --task_index=4 > asynclog-4.out 2>&1&