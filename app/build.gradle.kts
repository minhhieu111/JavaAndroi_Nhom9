plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")

}


android {
    namespace = "com.example.movieapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.movieapp"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    implementation(libs.commons.io)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

//    client app
    implementation(libs.appcompat.v7)
    implementation(libs.support.v4)
    implementation(libs.design)
    implementation(libs.exoplayer)
    implementation(libs.mediarouter.v7)
    implementation(libs.play.services.cast.framework)
    implementation(libs.android.query)
    implementation(libs.cardview)
    implementation(libs.glide)
    implementation(libs.firebase.auth)
    annotationProcessor(libs.glide.compiler)


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

