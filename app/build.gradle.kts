import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "app.avoor.planbot"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.avoor.planbot"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.b1"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "GOOGLE_APIKEY",
                "${properties["GOOGLE_APIKEY_RELEASE"]}")
            buildConfigField("String", "SIGNING_CERT",
                "${properties["SIGNING_CERT_RELEASE"]}")
        }

        debug {
            buildConfigField("String", "GOOGLE_APIKEY",
                "${properties["GOOGLE_APIKEY_DEBUG"]}")
            buildConfigField("String", "SIGNING_CERT",
                "${properties["SIGNING_CERT_DEBUG"]}")
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        // Sets Java compatibility to Java 11
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        // Extension level
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("11")
            languageVersion = KotlinVersion.fromVersion("2.3")
            apiVersion = KotlinVersion.fromVersion("2.3")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // compose and androidx UI related libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // use an alpha version of the material3 library for m3 expressive components
    implementation(libs.androidx.material3)
    // use a beta version of compose foundation for list item animations
    implementation(libs.androidx.compose.foundation)

    // androidx navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    // shapes
    implementation(libs.androidx.graphics.shapes)

    // retrofit (web requests)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // coil (image loader)
    implementation(libs.coil.compose)

    // accompanist permissions
    implementation(libs.accompanist.permissions)

    // m3x (extra UI library)
    implementation(libs.m3x)

    // socket.io (used for fluff)
    implementation(libs.socketio.client) {
        // excluding org.json which is provided by Android
        exclude("org.json", "json")
    }

    // avoor symbols
    implementation(project(":app:symbols"))

    // jni related
    implementation(project(":app:jniCommon"))
    implementation(project(":app:liboc"))

    // kotlin datetime
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.lifecycle.service)

    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // workmanager
    implementation(libs.androidx.work.runtime.ktx)

    // sign in with google
    implementation(libs.androidx.credentials) //noinspection LoginCredentials
    implementation(libs.androidx.credentials.play) //noinspection LoginCredentials
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}