# a14gsifixesa20s
A Magisk module that attempts to stabilize the GSI experience on the Samsung Galaxy A20s Android Smartphone.

* Please navigate to the [module](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/module) directory if you want to view the source files of the actual magisk module.
* Navigate to the [ds_incall_audio_svc_app_src](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/ds_incall_audio_svc_app_src) directory if you want to view the source files for the Service app that fixes In-Call Audio for Dual-SIMs.

  **(For GSI Maintainers, If you want to integrate this patch into your GSI, Please DO NOT use that apk in the `module/system/priv-app` directory, instead, Please refer to [these notes](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/notes_for_integrating_ds_audio_svc_into_gsis.md) to see how you can make the necessary modifications to integrate this patch into your GSI)**

* Navigate to the [p2p_wfd_fix_xposed_mod_src](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/p2p_wfd_fix_xposed_mod_src) directory if you want to view the source files for the Xposed module that fixes Wi-Fi Direct and WFD (Wi-Fi Display).


* Use the Python script [apply_se_patch.py](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/apply_se_patch.py) to apply the SEPolicy fix that allows you to boot Android 14+ GSIs with Enforcing Kernel.

 * How to use this script?
   * Extract your GSI's `.img` file using any tool you like
   * Navigate to `[gsi-extracted-folder]/system/etc/selinux`
   * Copy the script and paste it there
   * Open a new Command Prompt window there (with the Current Directory set to the folder where the script file is in)
   * Run the Python script by running `python apply_se_patch.py`
   * Wait for it to finish, then Build/Compress the extracted GSI `.img` file
   * Flash it and enjoy!
