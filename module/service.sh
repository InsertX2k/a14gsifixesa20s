#!/system/bin/sh
echo 160 > /proc/sys/vm/swappiness
chmod 775 /system/bin/sswap
/system/bin/sswap -s -z -f 2048
# will automatically grant permission on every single boot.
pm grant ziad_mrx.samsung.incall_audio.ds.svc android.permission.READ_PHONE_STATE
