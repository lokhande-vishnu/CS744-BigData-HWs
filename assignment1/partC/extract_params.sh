#!/bin/bash
preread_stats=$(cat ~/prerun_stats/*disk* | grep vda1 | awk '{sum += $6}; END {print sum}')
preread_time=$(cat ~/prerun_stats/*disk* | grep vda1 | awk '{sum += $7}; END {print sum}')

postread_stats=$(cat ~/postrun_stats/*disk* | grep vda1 | awk '{sum += $6}; END {print sum}')
postread_time=$(cat ~/postrun_stats/*disk* | grep vda1 | awk '{sum += $7}; END {print sum}')

prewrite_stats=$(cat ~/prerun_stats/*disk* | grep vda1 | awk '{sum += $10}; END {print sum}')
prewrite_time=$(cat ~/prerun_stats/*disk* | grep vda1 | awk '{sum += $11}; END {print sum}')

postwrite_stats=$(cat ~/postrun_stats/*disk* | grep vda1 | awk '{sum += $10}; END {print sum}')
postwrite_time=$(cat ~/postrun_stats/*disk* | grep vda1 | awk '{sum += $11}; END {print sum}')

read_time=$((postread_time - preread_time))
write_time=$((postwrite_time - prewrite_time))
read_bytes=$((postread_stats - preread_stats))
write_bytes=$((postwrite_stats - prewrite_stats))

read_time=$((read_time/1000))
read_bytes=$((read_bytes*512))
write_time=$((write_time/1000))
write_bytes=$((write_bytes*512))

echo "Read time:" $read_time "secs" 
echo "Write time:" $write_time "secs"
echo "Sectors read:" $read_bytes "bytes" 
echo "Sectors written:" $write_bytes "bytes"
echo "Read BW:" $((read_bytes/read_time)) "bps"
echo "Write BW:" $((write_bytes/write_time)) "bps"
echo "ENTER " $((read_bytes/read_time)) "/" $((write_bytes/write_time))

preread_bytes=$(cat ~/prerun_stats/*netdev* | grep eth0 | awk '{sum += $10}; END {print sum}')
postread_bytes=$(cat ~/postrun_stats/*netdev* | grep eth0 | awk '{sum += $10}; END {print sum}')

prewrite_bytes=$(cat ~/prerun_stats/*netdev* | grep eth0 | awk '{sum += $2}; END {print sum}')
postwrite_bytes=$(cat ~/postrun_stats/*netdev* | grep eth0 | awk '{sum += $2}; END {print sum}')
read_bytes=$((postread_bytes - preread_bytes))
write_bytes=$((postwrite_bytes - prewrite_bytes))

echo "NW read bytes" $((read_bytes)) "bytes"
echo "NW write bytes" $((write_bytes)) "bytes"
echo "ENTER " $((read_bytes)) "/" $((write_bytes))
