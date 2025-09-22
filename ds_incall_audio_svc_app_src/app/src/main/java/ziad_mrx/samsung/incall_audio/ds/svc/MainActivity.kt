package ziad_mrx.samsung.incall_audio.ds.svc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.media.AudioSystem

class MainActivity : AppCompatActivity() {

    private val TAG = "dsaudiofix.MainActivity"
    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Assuming you have an activity_main.xml layout

        // Request permissions at runtime
        requestPhonePermissions()

        // Optional: Add buttons to manually start/stop the service for testing
        val startServiceButton: Button = findViewById(R.id.startServiceButton) // Assuming you have a button with this ID
        val stopServiceButton: Button = findViewById(R.id.stopServiceButton)   // Assuming you have a button with this ID
        val setaudioparamstosim1: Button = findViewById(R.id.setAudioParamsToSim1)
        val setaudioparamstosim2: Button = findViewById(R.id.setAudioParamsToSim2)

        startServiceButton.setOnClickListener {
            if (checkPhonePermissions()) {
                startCallDetectionService()
                Toast.makeText(this, "Call Detection Service started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Some permissions aren't granted, but will run service anyway.", Toast.LENGTH_LONG).show()
//                requestPhonePermissions() // Re-request permissions
                startCallDetectionService() // Start the call detection service
            }
        }

        stopServiceButton.setOnClickListener {
            stopCallDetectionService()
            Toast.makeText(this, "Call Detection Service stopped", Toast.LENGTH_SHORT).show()
        }

        // for Manual audio params changing buttons
        setaudioparamstosim1.setOnClickListener {
            Log.d(TAG, "User has pressed the button to manually configure AudioParameters to SIM 1 (index 0)")
            manualSimSlotAudioParmConfig(0) // SIM slot index 0 is for SIM 1
            Toast.makeText(this, "Set AudioSystem SIM Slot parameter to SIM 1!", Toast.LENGTH_SHORT).show()
        }
        setaudioparamstosim2.setOnClickListener {
            Log.d(TAG, "User has pressed the button to manually configure AudioParameters to SIM 2 (index 1)")
            manualSimSlotAudioParmConfig(1) // SIM slot index 1 is for SIM 2
            Toast.makeText(this, "Set AudioSystem SIM Slot parameter to SIM 2!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        // On app start, if permissions are already granted, you could start the service.
        // However, since BootReceiver handles boot, this is more for manual control/testing.
        // If you want the service to always run when the app is opened, uncomment this:
//         if (checkPhonePermissions()) {
         startCallDetectionService()
    }

    override fun onStop() {
        super.onStop()
        // No need to stop service here if it's meant to run continuously in background
    }

    /**
     * Starts the CallDetectionService as a foreground service.
     */
    private fun startCallDetectionService() {
        val serviceIntent = Intent(this, CallDetectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    /**
     * Stops the CallDetectionService.
     */
    private fun stopCallDetectionService() {
        val serviceIntent = Intent(this, CallDetectionService::class.java)
        stopService(serviceIntent)
    }

    /**
     * Checks if the required phone-related permissions are granted.
     * Includes READ_PRIVILEGED_PHONE_STATE for system apps.
     * This returns true if all required permissions are granted.
     */
    private fun checkPhonePermissions(): Boolean {
        /**
         * This returns True if all required Phone Permissions are granted
         * */
        val readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
//        val readCallLog = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        val readPhoneNumbers = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
//        val readPrivilegedPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PRIVILEGED_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        val modifyAudioSettings = ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED
        val foregroundService = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
        // Note: RECEIVE_BOOT_COMPLETED is a normal permission, granted automatically if declared.

        return readPhoneState && readPhoneNumbers && modifyAudioSettings && foregroundService
    }

    /**
     * Requests the necessary phone-related permissions from the user.
     */
    private fun requestPhonePermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
             permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//            permissionsToRequest.add(Manifest.permission.READ_CALL_LOG)
//        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_NUMBERS)
        }
        // READ_PRIVILEGED_PHONE_STATE is a privileged permission, not typically granted via runtime dialog for non-system apps.
        // If your app is a system app, it will be granted automatically.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        // For Android 14 (API 34) and above, if targeting API 34+ and using foregroundServiceType="phoneCall"
        // you might also need to request FOREGROUND_SERVICE_PHONE_CALL if it's not automatically granted.
        // However, it's usually implicitly granted with FOREGROUND_SERVICE if your manifest declares the type.

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * Handles the result of the permission request.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!checkPhonePermissions()) { // not all phone permissions are granted.
            requestPhonePermissions()
        }
        startCallDetectionService()  // service will start both cases.
    }

    /**
     * Modifies AudioSystem parameters according to the given simSlotIndex
     * With simSlotIndex value being an Integer, and value 0 represents SIM 1
     * value 1 represents SIM 2, any other value will automatically correspond to SIM 1
     * (aka. Automatically assuming SIM slot 1, or value 0).
     */
    private fun manualSimSlotAudioParmConfig(simSlotIndex: Int) {
        when(simSlotIndex) { // value 0 is for SIM 1, value 1 is for SIM 2, and if neither values are given
            // we will fall back to defaults, aka SIM 1 (value 0)
            0 -> AudioSystem.setParameters("g_call_sim_slot=0x01") // Sets the active SIM card for calls to SIM 1
            1 -> AudioSystem.setParameters("g_call_sim_slot=0x02") // Sets the active SIM card for calls to SIM 2
            else -> AudioSystem.setParameters("g_call_sim_slot=0x01") // Sets the active SIM card for calls to SIM 1
        }
    }
}
