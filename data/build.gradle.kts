plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kapt)

    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "com.cases.carefull.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}
kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.bundles.android.core)

    implementation(libs.bundles.compose.libraries)

    implementation(libs.kotlinx.coroutines.play.services)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.libraries)

    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)


    implementation(libs.bundles.retrofit)

    // 로그 확인용
    implementation(libs.logging.interceptor)

    // kakao
    implementation(libs.v2.user)

    // Coil
    implementation(libs.coil.compose)

    // ML Kit Pose Detection
    implementation(libs.bundles.pose.detection)

    // ML Kit Face Detection
    implementation(libs.face.detection)

    // Camerax
    implementation(libs.bundles.camerax)

    // tikxml
    implementation("com.tickaroo.tikxml:core:0.8.13")
    implementation("com.tickaroo.tikxml:annotation:0.8.13")
    implementation("com.tickaroo.tikxml:retrofit-converter:0.8.13")
    kapt("com.tickaroo.tikxml:processor:0.8.13")

    // RoomDB
    implementation(libs.bundles.room.libraries)
    ksp(libs.androidx.room.compiler) {
        exclude(group = "com.intellij", module = "annotations")
    }

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}