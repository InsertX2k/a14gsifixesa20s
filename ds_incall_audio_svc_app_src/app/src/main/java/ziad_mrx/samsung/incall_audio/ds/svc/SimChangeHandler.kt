package ziad_mrx.samsung.incall_audio.ds.svc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class SimChangeHandler : BroadcastReceiver() {
    val TAG: String = "dsaudiofix.SimChangeHandler"
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d(TAG, "onReceive(): execution begin...")
        if (p1?.action == "android.intent.action.SIM_STATE_CHANGED" ||
            p1?.action == "android.telephony.action.SIM_SLOT_STATUS_CHANGED" ||
            p1?.action == "android.telephony.action.MULTI_SIM_CONFIG_CHANGED" ||
            p1?.action == "android.telephony.action.SIM_CARD_STATE_CHANGED") {
            // display the current action
            Log.d(TAG, "onReceive(): action is: ${p1.action}")
            // implement actual logic for rebooting the CallDetectionService here.
            Log.d(TAG, "onReceive(): stopping the call detection service...")
            // stopping the service
            val intent: Intent = Intent(p0, CallDetectionService::class.java)
            p0?.stopService(intent)
            Log.d(TAG, "onReceive(): stopped the call detection service!")
            // starting the service
            Log.d(TAG, "onReceive(): starting call detection service...")
            p0?.startForegroundService(intent)
            Log.d(TAG,"onReceive(): started call detection service!")
        }
        // -----------------------------
        Log.d(TAG, "onReceive(): execution finished!")

    }
}