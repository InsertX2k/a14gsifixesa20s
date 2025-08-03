# Notes for integrating DS InCall Audio Service App into GSIs
As I mentioned earlier, the APK included in the module isn’t suitable for direct integration with GSIs, because it still requires some modifications.

**So, in this note I will explain what modifications need to be done in order for the service app to be fully suitable for integration.**

## Modifications
* Modify `AndroidManifest.xml` to do all of the following:

    * Disable the visible launcher icon *(while still keeping the MainActivity exported)*
    * Make the app run with the shared user ID `android.uid.system`
    * Remove intent filters that allow the app to be seen as a Dialer app.
* Integrate [privileged permissions allowlist file](https://source.android.com/docs/core/permissions/perms-allowlist) into `/system/etc/permissions`
* Sign the app with the **platform key**

**I will explain each step (incl. their sub-steps) in detail in the next section of this document.**

## Modifications Explained
The first modification is to modify the [`AndroidManifest.xml` file of the service app](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/ds_incall_audio_svc_app_src/src/main/AndroidManifest.xml) to achieve a few things:

* Disable the visible launcher icon *(while still keeping the MainActivity exported)*

    It makes sense, right?, who needs a system service that has its MainActivity exposed in the app drawer as if it was just a normal user app?
    
    The reason why I didn't do it at first is because Android doesn't allow installation of apks that don't have a visible launcher icon to `/system/priv-app` without at least being signed with the platform key, and since I didn't have the platform key for the GSI I was using, I simply had to expose the launcher icon for the MainActivity.

    As a GSI maintainer, since you can sign the APK with the platform key, I strongly recommend hiding the launcher icon for the app’s MainActivity, as there’s no reason to have it visible in the app drawer. ***However, allowing it to be launched manually — for example, via an Activity Launcher app — can be helpful in cases such as when a user inserts another SIM card and needs to restart the service without rebooting the device.***

    **You can achieve that by opening the file [`AndroidManifest.xml`](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/ds_incall_audio_svc_app_src/src/main/AndroidManifest.xml) in your favorite code editor and making these changes:**

    * **Comment (or remove) this intent filter (or XML Tag):**
    ```xml
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
    ```
    **So, it should be like this if you commented it out:**
    ```xml
    <!-- <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter> -->
    ```
    ***or take the much better approach of completely removing these 4 lines.***

* Make the app run with the same user ID as the process/package `android.uid.system`

    This is necessary to ensure that the system doesn't kill the service (running in the background) because of battery optimizations, it also enforces the "Unrestricted" battery usage optimization option, and prevents the user from messing with battery optimization settings for this package.

    **You can achieve this by adding the line `android:sharedUserId="android.uid.system"` to the beginning (opening) of the XML Tag `manifest` in the file [`AndroidManifest.xml`](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/ds_incall_audio_svc_app_src/src/main/AndroidManifest.xml), like shown below:**

    ```xml
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:persistent="true"
        android:sharedUserId="android.uid.system">
    ```

    The reason I couldn't do this with the main APK is (again) that Android doesn't allow apks that aren't signed with the platform key to run with the same user ID as `android.uid.system`

* Remove intent filters that allow the app to be seen as a Dialer app.

    **Edit the file [`AndroidManifest.xml`](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/ds_incall_audio_svc_app_src/src/main/AndroidManifest.xml) to remove/comment these 2 intent filters, or XML Tags:**

    ```xml
    <intent-filter>
        <action android:name="android.intent.action.DIAL" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="tel" />
    </intent-filter>
    ```

* Integrate [privileged permissions allowlist file](https://source.android.com/docs/core/permissions/perms-allowlist) into `/system/etc/permissions`

    **You may want to learn more about this topic from its [Android documentation page](https://source.android.com/docs/core/permissions/perms-allowlist)**

    ***tl;dr:***
    To ensure the system enforces the granted state of all permissions required by this app — and prevents the user from manually revoking them — we must explicitly declare these permissions in a privileged permission allowlist XML file. This file should be placed in `/system/etc/permissions`. Since the app's APK is signed with the platform key, the system will honor and enforce these permissions as granted, unlike when the app is installed via a Magisk module, which lacks this level of privilege enforcement.

    **You can achieve this in two ways**, with the first one being to create a file named [`privapp-permissions-ziad_mrx.samsung.incall_audio.ds.svc.xml`](https://github.com/InsertX2k/a14gsifixesa20s/blob/main/module/system/etc/permissions/privapp-permissions-ziad_mrx.samsung.incall_audio.ds.svc.xml) **(Please do not change this name)** in `/system/etc/permissions` with these lines as its contents: 

    ```xml
    <permissions>
        <privapp-permissions package="ziad_mrx.samsung.incall_audio.ds.svc">
            <permission name="android.permission.READ_PRIVILEGED_PHONE_STATE"/>
            <permission name="android.permission.READ_PHONE_STATE"/>
            <permission name="android.permission.READ_PHONE_NUMBERS"/>
            <permission name="android.permission.MODIFY_AUDIO_SETTINGS"/>
            <permission name="android.permission.FOREGROUND_SERVICE"/>
            <permission name="android.permission.FOREGROUND_SERVICE_PHONE_CALL"/>
            <permission name="android.permission.RECEIVE_BOOT_COMPLETED"/>
            <permission name="android.permission.INTERACT_ACROSS_USERS"/>
        </privapp-permissions>
    </permissions>
    ```

    And the last one being to append the lines above to your `privapp-permissions-*.xml` (for example: `privapp-permissions-platform.xml`) file in `/system/etc/permissions` ***(Not recommended)***.


**And as always, If you need any help, feel free to contact me on Telegram (my user is: `@ziad_mrx`), or on Discord (my user is: `ziad.mrx`)**

