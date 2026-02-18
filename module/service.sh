#!/system/bin/sh
# modify kernel swappiness & make /system/bin/sswap binary executable.
echo 160 > /proc/sys/vm/swappiness
chmod 775 /system/bin/sswap
# create a zram disk with 2048 (2GB) size
# (this is currently the best zram size as far as my tests have gone).
/system/bin/sswap -s -z -f 2048
# tweak low memory killer
# but first we must wait for sys.boot_completed signal
while [ "$(getprop sys.boot_completed)" != "1" ]; do sleep 1; done
# at this stage sys.boot_completed signal must've been sent, we can set lmk minfree params
echo "2560,12800,25600,99840,115200,131072" > /sys/module/lowmemorykiller/parameters/minfree
# will automatically grant permission on every single boot.
pm grant ziad_mrx.samsung.incall_audio.ds.svc android.permission.READ_PHONE_STATE
# THESE COMMANDS ARE FOR THE COMPANION P2P FIX XPOSED MODULE, SO THEY MUST BE THE LAST LINES TO EXECUTE.
# ADD COMMANDS TO EXECUTE ABOVE THIS COMMENT.
# run the logcat wpa_supplicant P2P events listener script in the background.
# this will log all P2P events to /data/p2p_evt_recent, which can be read by the companion Xposed module.
chmod 554 /system/bin/log_p2p_evts.sh
/system/bin/log_p2p_evts.sh&
