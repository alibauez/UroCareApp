plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.urocareapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.urocareapp"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation(libs.material.calendarview)
//    implementation("com.prolificinteractive:material-calendarview:1.7.0")

    implementation ("com.google.android.material:material:1.9.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")



    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")


    implementation ("io.getstream:stream-chat-android-client:6.9.0")
    implementation ("io.getstream:stream-chat-android-state:6.9.0")
    implementation ("io.getstream:stream-chat-android-offline:6.9.0")
    implementation ("io.getstream:stream-chat-android-ui-components:6.9.0")
    implementation ("io.getstream:stream-chat-android-compose:6.9.0")
    //implementation ("stream-chat-android-markdown-transformer:6.9.0")

    //implementation ("com.github.User:Repo:Tag")

    //Generacion token
    implementation ("org.bitbucket.b_c:jose4j:0.7.12")
    implementation ("org.bouncycastle:bcprov-jdk15to18:1.76")

    implementation ("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation ("com.google.code.gson:gson:2.8.8")  // Agrega esta línea




}