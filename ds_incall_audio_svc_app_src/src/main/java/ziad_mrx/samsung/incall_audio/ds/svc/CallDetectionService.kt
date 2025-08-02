package ziad_mrx.samsung.incall_audio.ds.svc

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.media.AudioSystem
import android.telecom.Call
import android.telecom.PhoneAccount
import android.telecom.TelecomManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import android.telecom.PhoneAccountHandle // Import PhoneAccountHandle
import android.telephony.SubscriptionInfo
import androidx.annotation.RequiresPermission



/**
 * A Foreground Service that continuously monitors call states and detects the SIM slot
 * being used during the current active call to set AudioSystem parameters accordingly.
 *
 * This service requires the app to be a system app with READ_PRIVILEGED_PHONE_STATE
 * and READ_PHONE_STATE Permissions to function properly.
 *
 * When installed as a system app, this service can run without a persistent notification.
 *
 */
class CallDetectionService : Service() {

    private val TAG = "dsaudiofix.CallDetectionService"
//    private lateinit var simCallDetector: SimCallDetector
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var telecomManager: TelecomManager

    private lateinit var telMgrSlot0: TelephonyManager
    private lateinit var telMgrSlot1: TelephonyManager


    private var isCallBackRegistered: Boolean = false

    private val handler = Handler(Looper.getMainLooper()) // Handler for TelecomManager callback

    private val telephonyCallback: TelephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            Log.d(TAG, "TelephonyManager Call state changed: $state")
            when (state) {
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d(TAG, "System Call State is OFFHOOK.")
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d(TAG, "System Call State is IDLE. Setting AudioSystem parameter for call off.")
                    AudioSystem.setParameters("g_call_state=1") // CALL_STATUS_CS_VOICE_CP_VIDEO_CALL_OFF
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d(TAG, "System Call State is RINGING.")
                }
            }
        }
    }

    // A TelephonyCallback.CallStateListener object to be registered to the SIM 1's TelephonyManager as a Call Callback Object.
    private val telCallBackSim0: TelephonyCallback = object: TelephonyCallback(), TelephonyCallback.CallStateListener {
        // we will use overrides to react to specific events
        override fun onCallStateChanged(callS: Int) {
            // we have the current call state as p0
            when (callS) {
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d(TAG, "CALL_STATE_OFFHOOK FOR SIM 0!")
                    Log.d(TAG, "Will set corresponding audio parameters")
                    setAudioParametersForSim(0)
                    Log.d(TAG, "Set audio parameters for SIM slot index 0")
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d(TAG, "CALL_STATE_IDLE FOR SIM 0!")
                    Log.d(TAG, "Will not set corresponding audio parameters.")
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d(TAG, "CALL_STATE_RINGING FOR SIM 0!")
                }
            }
        }
    }

    // a TelephonyCallback.CallStateListener object to be registered to SIM 2's TelephonyManager as a Call Callback object.
    private val telCallBackSim1: TelephonyCallback = object: TelephonyCallback(), TelephonyCallback.CallStateListener {
        // we will use overrides to react to specific events
        override fun onCallStateChanged(callSS: Int) {
            when (callSS) {
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d(TAG, "CALL_STATE_OFFHOOK FOR SIM SLOT 1!")
                    Log.d(TAG, "Will set corresponding audio parameters.")
                    setAudioParametersForSim(1)
                    Log.d(TAG, "Set audio parameters for SIM slot index 1")
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d(TAG, "CALL_STATE_IDLE FOR SIM SLOT 1!")
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d(TAG, "CALL_STATE_RINGING FOR SIM SLOT 1!")
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "CallDetectionService onCreate")
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate(): READ_PHONE_STATE Permission is GRANTED!, Will continue!")
            val subIdForSlotIndex0: Int = getSubIdForSlot(0)
            Log.d(TAG, "onCreate(): Detected Subscription ID for sim slot index 0 is: $subIdForSlotIndex0")
            val subIdForSlotIndex1: Int = getSubIdForSlot(1)
            Log.d(TAG, "onCreate(): Detected Subscription ID for sim slot index 1 is: $subIdForSlotIndex1")
            if (subIdForSlotIndex0 != -1) {
                telMgrSlot0 = telephonyManager.createForSubscriptionId(subIdForSlotIndex0)
            }
            if (subIdForSlotIndex1 != -1){
                telMgrSlot1 = telephonyManager.createForSubscriptionId(subIdForSlotIndex1)
            }
            Log.d(TAG, "onCreate(): Successfully created TelephonyManager instances for each SIM slot index!")
            Log.d(TAG,"onCreate(): registerCallCallbacks() will handle registration of each call callback!")
        } else {
            Log.e(TAG, "onCreate(): READ_PHONE_STATE Permission is DENIED!, Cannot continue, Please GRANT permission then restart service from the UI!!!")
        }
        // we need to create a TelephonyManager instance for each sim slot index


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "CallDetectionService onStartCommand. isCallBackRegistered: $isCallBackRegistered")

//        startForeground(1, null) // no persistent notification
        // Start as foreground service (notification required for non-system apps, but good practice)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "CallDetectionServiceChannel"
        val channelName = "DSInCallAudioSvcPersistentNotifChannel"
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "InCall_Audio_DS_SVC_Running"
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("InCallAudioForDSSVC")
            .setContentText("Service is running...")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        startForeground(1, notification)
        Log.d(TAG, "Displaying persistent notification successful.")
        Log.d(TAG, "Service started in foreground.")

        // Register callbacks ONLY if not already registered
        if (!isCallBackRegistered) { // if isCallBackRegistered == false
            Log.d(TAG, "Call Callback WAS NOT Previously registered, will register it now.")
            registerCallCallbacks()

            Log.d(TAG, "Registered Call Callback and managed control variable!!!")
        } else { // isCallBackRegistered == true
            Log.d(TAG, "Call Callback already registered, Will not register it again.")
        }
        Log.d(TAG, "CallDetectionService has started!")
//        Log.d(TAG, "Enter loop for each Call in getCall() element")


        return START_STICKY // Service will be restarted if killed by system
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "CallDetectionService onDestroy. isCallBackRegistered: $isCallBackRegistered")
        unregisterCallCallbacks()
        Log.d(TAG, "Call Detection Service destroyed, unregistered Call Callbacks and set control variable to false.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service does not provide binding
        return null
    }

    /**
     * Uses telephonyManager.registerTelephonyCallback to register the TelephonyCallback object.
     */
    private fun registerCallCallbacks() {
        // Register TelephonyCallback to handle System Call State changes.
        telephonyManager.registerTelephonyCallback(mainExecutor, telephonyCallback)
        Log.d(TAG, "TelephonyCallback registered in service.")
        Log.d(TAG,
            "Will register Callbacks for each SIM Slot index of the two...")
        if (getSubIdForSlot(0) != -1){
            Log.d(TAG, "registerCallCallbacks(): getSubIdForSlot(0) doesn't return -1")
            telMgrSlot0.registerTelephonyCallback(mainExecutor, telCallBackSim0)
            Log.d(TAG, "registerCallCallbacks(): successfully registered TelephonyCallback on Slot index 0!")
        }
        if (getSubIdForSlot(1) != -1){
            Log.d(TAG, "registerCallCallbacks(): getSubIdForSlot(1) doesn't return -1")
            telMgrSlot1.registerTelephonyCallback(mainExecutor, telCallBackSim1)
            Log.d(TAG, "registerCallCallbacks(): successfully registered TelephonyCallback on Slot index 1!")
        }
        // only register it at the end.
        isCallBackRegistered = true
    }

    /**
     * Uses telephonyManager.unregisterTelephonyCallback to unregister the TelephonyCallback object.
     */
    private fun unregisterCallCallbacks() {
        // Unregister TelephonyCallback
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        telephonyManager.unregisterTelephonyCallback(telephonyCallback)
        Log.d(TAG,"main TelephonyCallback is unregistered!")
        Log.d(TAG, "Will Unregister Callbacks for each SIM Slot index of the two...")
        if (getSubIdForSlot(0) != -1){
            telMgrSlot0.unregisterTelephonyCallback(telCallBackSim0)
        }
        if (getSubIdForSlot(1) != -1){
            telMgrSlot1.unregisterTelephonyCallback(telCallBackSim1)
        }
        Log.d(TAG, "Successfully unregistered TelephonyCallBack for each of the two SIM Slots!")
        // only mark it as unregistered at the end!
        isCallBackRegistered = false
        Log.d(TAG, "TelephonyCallback unregistered from service.")
    }


    /**
     * Converts Call state integer to a readable string.
     */
    private fun stateToString(state: Int): String {
        return when (state) {
            Call.STATE_NEW -> "NEW"
            Call.STATE_RINGING -> "RINGING"
            Call.STATE_DIALING -> "DIALING"
            Call.STATE_ACTIVE -> "ACTIVE"
            Call.STATE_HOLDING -> "HOLDING"
            Call.STATE_DISCONNECTED -> "DISCONNECTED"
            Call.STATE_CONNECTING -> "CONNECTING"
            Call.STATE_DISCONNECTING -> "DISCONNECTING"
            else -> "UNKNOWN ($state)"
        }
    }

    /**
     * Sets audio parameters based on the detected SIM slot.
     * This is a placeholder for your specific audio logic.
     */
    private fun setAudioParametersForSim(simSlotIndex: Int) {
        val simSlotParameter = when(simSlotIndex) {
            0 -> "0x01" // SIM 1
            1 -> "0x02" // SIM 2
            else -> "0x01" // Default/Unknown
        }
        try {
            AudioSystem.setParameters("g_call_sim_slot=${simSlotParameter}")
            Log.d(TAG, "AudioSystem.setParameters called with g_call_sim_slot=${simSlotParameter}")
            AudioSystem.setParameters("g_call_state=514") // Always set g_call_state to 514 for active calls
            Log.d(TAG, "AudioSystem.setParameters called with g_call_state=514")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set audio parameters via AudioSystem: ${e.message}", e)
        }
    }


    /**
     * A function that attempts to retrieve the Active subscription ID for a specific given
     * SIM Slot index (as a parameter).
     *
     *
     * It returns -1 on failure.
     * */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private fun getSubIdForSlot(simSlotIndex: Int): Int {
        Log.d(TAG, "getSubIdForSlot() called, with parameter: $simSlotIndex")
        val subMgr: SubscriptionManager? = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
        if (subMgr != null) {
            Log.d(TAG, "getSubIdForSlot(): subMgr is not null")
            val subInfo: SubscriptionInfo? = subMgr.getActiveSubscriptionInfoForSimSlotIndex(simSlotIndex) ?: null
            Log.d(
                TAG,
                "getSubIdForSlot(): getActiveSubscriptionInfoForSimSlotIndex() called with parameter: $simSlotIndex"
            )
            if (subInfo != null) {
                Log.d(TAG, "getSubIdForSlot(): subInfo is not null")
                val subId: Int = subInfo.subscriptionId
                Log.d(TAG, "retrieved subscriptionId for SIM slot $simSlotIndex!: $subId")
                return subId
            } else { // when subInfo is null
                Log.e(
                    TAG,
                    "getSubIdForSlot(): subInfo (for simSlotIndex: $simSlotIndex) IS NULL"
                )
            }
        } else {
            Log.e(TAG, "getSubIdForSlot(): subMgr IS NULL!")
        }
        // returns -1 on failure.
        return -1
    }

}

