plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.workio"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.workio"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
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
}

dependencies {
    // Retrofit for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for interceptors and logging
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Gson for JSON parsing
    implementation ("com.google.code.gson:gson:2.10.1")

    // Socket.IO for real-time features
    implementation ("io.socket:socket.io-client:2.1.0")

    // Google Play Services (for GPS location)
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // Optional: RecyclerView for lists
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1") // Đảm bảo có appcompat (phiên bản 1.6.1 trở lên)

    //  Compact Calendar View (Giữ nguyên)
    implementation("com.github.sundeepk:compact-calendar-view:3.0.0") {
        exclude(group = "androidx.appcompat")
        exclude(group = "com.android.support")
        exclude(group = "org.threeten", module = "threetenbp")
    }

    // ThreeTenABP (Giữ nguyên)
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.9")
    implementation ("androidx.navigation:navigation-fragment:2.7.5")
    implementation ("androidx.navigation:navigation-ui:2.7.5")
}