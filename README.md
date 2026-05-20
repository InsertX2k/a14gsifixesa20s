# a14gsifixesa20s
A Magisk module that attempts to stabilize the GSI experience on the Samsung Galaxy A20s Android Smartphone.

* Please navigate to the [module](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/module) directory if you want to view the source files of the actual magisk module.
* Navigate to the [ds_incall_audio_svc_app_src](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/ds_incall_audio_svc_app_src) directory if you want to view the source files for the Service app that fixes In-Call Audio for Dual-SIMs.

  **(For GSI Maintainers, If you want to integrate this patch into your GSI, Please DO NOT use that apk in the `module/system/priv-app` directory, instead, Please refer to [these notes](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/notes_for_integrating_ds_audio_svc_into_gsis.md) to see how you can make the necessary modifications to integrate this patch into your GSI)**

* Navigate to the [p2p_wfd_fix_xposed_mod_src](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/p2p_wfd_fix_xposed_mod_src) directory if you want to view the source files for the Xposed module that fixes Wi-Fi Direct and WFD (Wi-Fi Display).

* If you want to boot Android 14+ GSIs on Enforcing kernels, Use the `apply_all_patches.*` script files in the [a20s_gsi_root_patches](https://github.com/InsertX2k/a14gsifixesa20s/tree/main/a20s_gsi_root_patches) directory.
