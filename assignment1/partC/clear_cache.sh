#!/bin/sh
sudo sh -c "sync; echo 3 > /proc/sys/vm/drop_caches"
echo 'vm-11-1 Done'
ssh vm-11-2 "sudo sh -c \"sync; echo 3 > /proc/sys/vm/drop_caches\""
echo 'vm-11-2 Done'
ssh vm-11-3 "sudo sh -c \"sync; echo 3 > /proc/sys/vm/drop_caches\""
echo 'vm-11-3 Done'
ssh vm-11-4 "sudo sh -c \"sync; echo 3 > /proc/sys/vm/drop_caches\""
echo 'vm-11-4 Done'
ssh vm-11-5 "sudo sh -c \"sync; echo 3 > /proc/sys/vm/drop_caches\""
echo 'vm-11-5 Done'
