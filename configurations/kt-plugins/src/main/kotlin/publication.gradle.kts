plugins {
    id("maven-publish")
    id("signing")
}

val sonatypeUsername = rootProject.getExtraString("sonatypeUsername") ?: ""
val sonatypePassword = rootProject.getExtraString("sonatypePassword") ?: ""

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
            description.set(rootProject.getExtraString("pomDescription"))
            url.set(rootProject.getExtraString("pomUrl"))

            licenses {
                license {
                    name.set(rootProject.getExtraString("pomLicenseName"))
                    url.set(rootProject.getExtraString("pomLicenseUrl"))
                    distribution.set(rootProject.getExtraString("pomLicenseDistribution"))
                }
            }
            developers {
                developer {
                    id.set(rootProject.getExtraString("pomDeveloperId"))
                    name.set(rootProject.getExtraString("pomDeveloperName"))
                    organization.set(rootProject.getExtraString("pomDeveloperOrganization"))
                    organizationUrl.set(rootProject.getExtraString("pomDeveloperOrganizationUrl"))
                }
            }
            scm {
                url.set(rootProject.getExtraString("pomScmUrl"))
            }
        }
    }
}

val hasSigningKeyId = listOf("signing.keyId", "signingKeyId")
    .mapNotNull { rootProject.getExtraString(it) }.isNotEmpty()

signing {
    isRequired = hasSigningKeyId
    sign(publishing.publications)
}