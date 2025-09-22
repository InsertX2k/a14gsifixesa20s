plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // REMOVED: alias(libs.plugins.kotlin.compose) // Remove this plugin as we are not using Jetpack Compose
}

android {
    namespace = "ziad_mrx.samsung.incall_audio.ds.svc"
    compileSdk = 34

    defaultConfig {
        applicationId = "ziad_mrx.samsung.incall_audio.ds.svc"
        minSdk = 31
        targetSdk = 33
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // REMOVED: buildFeatures { // Remove this block as we are not using Jetpack Compose
    //     compose = true
    // }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.fragment)
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // ADDED: Dependencies for traditional Android Views (AppCompat and ConstraintLayout)
    implementation(libs.androidx.appcompat) // Essential for AppCompatActivity and UI components
    implementation(libs.androidx.constraintlayout) // For ConstraintLayout in activity_main.xml
    implementation(libs.material) // Generally useful for Material Design UI components like Button
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
