package ziad_mrx.samsung.incall_audio.ds.svc
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.util.Log
import android.net.Uri


class DialerBcReceiver: BroadcastReceiver() {
    lateinit var context: Context
    val TAG: String = "dsaudiofix.DialerBcReceiver"
    val SECRET_CODE: String = "9216"
    override fun onReceive(cont: Context?, intent0: Intent?) {
        Log.d(TAG, "onReceive(): begin execution...")
        /*
        * Put actual logic for the onReceive here!
        * */
        val data: Uri? = intent0?.data
        if (data != null && data.host == SECRET_CODE) {
            Log.d(TAG, "OnReceive(): data is not null && data.host is: ${data.host} , Will start Settings Activity....")
            val intent: Intent = Intent(cont, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            cont?.startActivity(intent)
        }
        // -----------------
        Log.d(TAG, "onReceive(): end execution!")
    }
}

