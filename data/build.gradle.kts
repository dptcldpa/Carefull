plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.cases.carefull.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

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
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.ui)
    implementation(libs.material)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    // 로그 확인용
    implementation(libs.logging.interceptor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.coil.compose)
    
    //ML Kit Pose Detection
    implementation(libs.pose.detection)
    implementation(libs.pose.detection.accurate)
    
    //camera
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.video)
    
    implementation(libs.androidx.camera.mlkit.vision)
    implementation (libs.androidx.camera.extensions)
    
    implementation(libs.kotlinx.coroutines.play.services)
}