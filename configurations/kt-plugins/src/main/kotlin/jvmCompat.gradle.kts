import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion: String = if (rootProject.extra.has("javaVersion")) {
    val version = rootProject.extra.get("javaVersion") as String
    println("using $version")
    version
} else {
    println("Warning : you didn't define any javaVersion in your rootProject. Using 17 by default")
    "17"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = javaVersion
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
