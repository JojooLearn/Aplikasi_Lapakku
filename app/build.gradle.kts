plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.pemrogramanfrontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pemrogramanfrontend"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.cardview:cardview:1.0.0")

//    implementation ("com.amitshekhar.android:android-networking:1.0.2")
////    implementation ("com.github.amitshekhariitbhu.Fast-Android-Networking:android-networking:1.0.4")
}