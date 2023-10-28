plugins {
    id("maven-publish")
    id("signing")
}

fun getExtraString(name: String, default: String = "") =
    if (rootProject.extra.has(name)) rootProject.extra[name]?.toString()
    else default

val sonatypeUsername = getExtraString("sonatypeUsername") ?: ""
val sonatypePassword = getExtraString("sonatypePassword") ?: ""

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from()
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}

publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }

        maven {
            name = "cicd"
            setUrl("file://${rootProject.projectDir}/repository")
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {

        val actualName = this.name

        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set(actualName)
            description.set(getExtraString("pomDescription"))
            url.set(getExtraString("pomUrl"))

            licenses {
                license {
                    name.set(getExtraString("pomLicenseName"))
                    url.set(getExtraString("pomLicenseUrl"))
                    distribution.set(getExtraString("pomLicenseDistribution"))
                }
            }
            developers {
                developer {
                    id.set(getExtraString("pomDeveloperId"))
                    name.set(getExtraString("pomDeveloperName"))
                    organization.set(getExtraString("pomDeveloperOrganization"))
                    organizationUrl.set(getExtraString("pomDeveloperOrganizationUrl"))
                }
            }
            scm {
                url.set(getExtraString("pomScmUrl"))
            }
        }
    }
}

signing {
    isRequired = (null != getExtraString("signingKeyId"))
    sign(publishing.publications)
}