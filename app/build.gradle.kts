plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.capstone.storyappsubmission"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.capstone.storyappsubmission"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.espresso.idling.resource)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")

    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)
    implementation (libs.logging.interceptor)
    implementation (libs.androidx.lifecycle.runtime.ktx)

    implementation ("com.github.bumptech.glide:glide:4.15.1") // Pastikan menggunakan versi terbaru
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1") // Menambahkan compiler untuk Glide
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation(libs.androidx.exifinterface)

    implementation ("com.squareup.picasso:picasso:2.8")
    ksp("androidx.room:room-compiler:2.5.0")
    implementation(libs.androidx.room.runtime) // Pastikan versi terbaru
    implementation(libs.room.paging) // Tambahkan dependensi ini

}