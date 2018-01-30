#!/bin/bash

prerun_readsectors=$(cat ~/prerun_stats/*diskstats | grep vda1 | awk '{sum += $6}; END {print sum}')
postrun_readsectors=$(cat ~/postrun_stats/*diskstats | grep vda1 | awk '{sum += $6}; END {print sum}')
readsectors=$((postrun_readsectors - prerun_readsectors))
readmegabytes=$((readsectors*512/1048576))
echo "Storage read MBs:" $readmegabytes "MBs" 

prerun_readtime_ms=$(cat ~/prerun_stats/*diskstats | grep vda1 | awk '{sum += $7}; END {print sum}')
postrun_readtime_ms=$(cat ~/postrun_stats/*diskstats | grep vda1 | awk '{sum += $7}; END {print sum}')
readtime_ms=$((postrun_readtime_ms - prerun_readtime_ms))
readtime=$((readtime_ms/1000))
echo "Storage read time:" $readtime "secs" 

readbw_MBps=$((readmegabytes/readtime))
echo "Storage read Bandwidth:" $((readbw_MBps)) "MBps"

prerun_writesectors=$(cat ~/prerun_stats/*diskstats | grep vda1 | awk '{sum += $10}; END {print sum}')
postrun_writesectors=$(cat ~/postrun_stats/*diskstats | grep vda1 | awk '{sum += $10}; END {print sum}')
writesectors=$((postrun_writesectors - prerun_writesectors))
writemegabytes=$((writesectors*512/1048576))
echo "Storage write MB:" $writemegabytes "MBs" 

prerun_writetime_ms=$(cat ~/prerun_stats/*diskstats | grep vda1 | awk '{sum += $11}; END {print sum}')
postrun_writetime_ms=$(cat ~/postrun_stats/*diskstats | grep vda1 | awk '{sum += $11}; END {print sum}')
writetime_ms=$((postrun_writetime_ms - prerun_writetime_ms))
writetime=$((writetime_ms/1000))
echo "Storage write time:" $writetime "secs" 

writebw_MBps=$((writemegabytes/writetime))
echo "Storage write Bandwidth:" $((writebw_MBps)) "MBps"


prerun_readbytes=$(cat ~/prerun_stats/*netdev | grep eth0 | awk '{sum += $10}; END {print sum}')
postrun_readbytes=$(cat ~/postrun_stats/*netdev | grep eth0 | awk '{sum += $10}; END {print sum}')
readbytes=$((postrun_readbytes-prerun_readbytes))
readmegabytes=$((readbytes/1048576))
echo "Network read MBs" $((readmegabytes)) "MBs"

prerun_writebytes=$(cat ~/prerun_stats/*netdev | grep eth0 | awk '{sum += $2}; END {print sum}')
postrun_writebytes=$(cat ~/postrun_stats/*netdev | grep eth0 | awk '{sum += $2}; END {print sum}')
writebytes=$((postrun_writebytes-prerun_writebytes))
writemegabytes=$((writebytes/1048576))
echo "Network write MBs" $((writemegabytes)) "MBs"

