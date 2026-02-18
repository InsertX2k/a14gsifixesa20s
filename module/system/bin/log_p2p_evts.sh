#!/system/bin/sh
logcat -s wpa_supplicant:I --format=raw | while read -r line; do
    if echo "$line" | grep -q "P2P-"; then
        case "$line" in
            *P2P-DEVICE-FOUND*|*P2P-DEVICE-LOST*)
                echo "Ignoring P2P-DEVICE-FOUND and P2P-DEVICE-LOST P2P events!"
                ;;
            *P2P-*)
                echo "Received P2P Event: \"$line\""
                echo "$line" > /data/p2p_evt_recent
                ;;
            *)
                echo "Non-P2P Event Ignored: \"$line\""
                ;;
        esac
    fi
done