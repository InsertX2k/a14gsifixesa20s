# a14gsifixesa20s
A Magisk module that applies various fixes for running Android 11-14 TD-based GSIs on the Galaxy A20s

## What does it currently do?
This magisk module currently does all of the following:

* Fixes MTP on TrebleDroid-based GSIs by reverting [this patch](https://github.com/TrebleDroid/device_phh_treble/commit/60b9a5c81d602c853e5b4d62d02c8409b21f0d63), still requires the modified kernel with [this patch](https://github.com/GalaxyA20s/android_kernel_samsung_a20s/commit/2c4a2487a2d3abf50b9206ad792bb4492e3c5382) and [this patch](https://github.com/GalaxyA20s/android_kernel_samsung_a20s/commit/5f4f599a475c77cd5aadbca8d234acbf3dcb1ec1) too.
* Fixes Broken ZRam partition on all TrebleDroid-based GSIs, by enabling a ZRam Partition.
* Disables **phh-remotectl** by deleting its binaries and other dependencies and enforcing a system property upon system startup.
* Removes the script that removes the telephony subsystem from Phhtreble settings app.
* Modifies a `system.prop` value with the hope of enabling **Wireless Screen Mirroring (Cast)** feature (apparently wifi direct doesn't work on GSIs)

And it will be improved to fix other issues or at least try to stabilize the GSI experience on this phone, for the changelog you can always refer to the latest Release.

## IMPORTANT!!!
**ALWAYS remove the previous module and reboot** before installing a new update.