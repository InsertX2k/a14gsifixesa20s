package ziad_mrx.samsung.incall_audio.ds.svc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * BroadcastReceiver that listens for the ACTION_BOOT_COMPLETED broadcast.
 * When the device finishes booting, it starts the CallDetectionService.
 */
class BootReceiver : BroadcastReceiver() {
    private val TAG = "dsaudiofix.BootReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            // It is stupid to make the user wait to unlock their phone to be able to hear
            // in call audio, isn't it?
            Log.d(TAG, "Boot completed broadcast received. Starting CallDetectionService...")
            context?.let {
                val serviceIntent = Intent(it, CallDetectionService::class.java)
                ContextCompat.startForegroundService(it, serviceIntent)
            }
        }
    }
}
