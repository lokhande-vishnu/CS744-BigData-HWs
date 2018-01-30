#!/bin/bash
cd ~/
rm -rf ~/postrun_stats/
mkdir ~/postrun_stats/

cat /proc/diskstats > ~/postrun_stats/vm1_diskstats
cat /proc/net/dev > ~/postrun_stats/vm1_netdev

ssh vm-11-2 '
  cat /proc/diskstats > ~/vm2_diskstats
  cat /proc/net/dev > ~/vm2_netdev
'
scp ubuntu@vm-11-2:~/vm2_diskstats ubuntu@vm-11-2:~/vm2_netdev ~/postrun_stats/

ssh vm-11-3 '
  cat /proc/diskstats > ~/vm3_diskstats
  cat /proc/net/dev > ~/vm3_netdev
'
scp ubuntu@vm-11-3:~/vm3_diskstats ubuntu@vm-11-3:~/vm3_netdev ~/postrun_stats/

ssh vm-11-4 '
  cat /proc/diskstats > ~/vm4_diskstats
  cat /proc/net/dev > ~/vm4_netdev
'
scp ubuntu@vm-11-4:~/vm4_diskstats ubuntu@vm-11-4:~/vm4_netdev ~/postrun_stats/

ssh vm-11-5 '
  cat /proc/diskstats > ~/vm5_diskstats
  cat /proc/net/dev > ~/vm5_netdev
'
scp ubuntu@vm-11-5:~/vm5_diskstats ubuntu@vm-11-5:~/vm5_netdev ~/postrun_stats/
