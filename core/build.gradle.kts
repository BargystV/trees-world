java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

eclipse.project.name = "${Versions.appName}-core"

repositories {
    maven("https://jitpack.io")
    mavenCentral()
    google()
}

dependencies {
    api("com.badlogicgames.gdx:gdx:${Versions.gdx}")
    api("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    implementation("com.github.BargystV:ai-math:main-SNAPSHOT")
    implementation("com.github.BargystV:logger:main-SNAPSHOT")


    if (Versions.enableGraalNative) {
        implementation("io.github.berstanio:gdx-svmhelper-annotations:${Versions.graalHelper}")
    }
}

