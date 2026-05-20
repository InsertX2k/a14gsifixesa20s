# GSI Root Directory Patches
This folder/directory contains the script files that applies appropriate patches to GSI image files to fix all the following:

* Boot on TrebleDroid-Based AOSP 14+ GSIs
* Incorrect device model reported to the system
* Bootlooping sometimes when build fingerprint doesn't match stock OS build fingerprint

## How to apply?
* Extract the GSI Image using any tool you like
* Copy all files in this folder and paste it into the extracted GSI folder.
* Double-click/Run script file `apply_all_patches.bat`
* Wait for it to finish, then delete it (only the script file `apply_all_patches.bat`)
* Compress/Build the GSI image, then flash it onto your A20s `system` partition (if using Odin, rename built GSI image to `system.img.ext4`, then compress into a POSIX TAR archive, then choose the result TAR Archive as the AP File, then flash).