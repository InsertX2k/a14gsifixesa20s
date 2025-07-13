#!/system/bin/sh
chmod +x /system/bin/remotedisplay
echo 160 > /proc/sys/vm/swappiness
chmod +x /system/bin/sswap
/system/bin/sswap -s -z -f 2560
