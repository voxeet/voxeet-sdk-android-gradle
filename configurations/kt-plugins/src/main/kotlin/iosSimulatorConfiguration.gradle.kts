import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest

data class TasksDefinition(
    val name: String,
    val args: List<String>,
    val next: String? = null
)

afterEvaluate {
    val iOSSimulatorUuid: String = with(rootProject.getExtraString("iOSSimulatorUuid")) {
        if (null == this || this.isEmpty()) {
            println("skipping ios simulator configuration")
        }
        this
    } ?: return@afterEvaluate

    val isMultiplatform = project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

    val newTasks = listOf(
        TasksDefinition(
            "bootIos",
            listOf("/usr/bin/xcrun", "simctl", "boot", iOSSimulatorUuid)
        ),
        TasksDefinition(
            "openSimulator",
            listOf("open", "-a", "Simulator"), "bootIos"
        ),
        TasksDefinition(
            "shutDownIos",
            listOf("/usr/bin/xcrun", "simctl", "shutdown", iOSSimulatorUuid)
        )
    )

    if (!iOSSimulatorUuid.isEmpty() && isMultiplatform) {
        println("Having a multiplatform project ${project}")
        newTasks.forEach { triple ->
            tasks.register(triple.name) {
                if (null != triple.next) {
                    dependsOn(triple.next)
                }
                doLast {
                    exec {
                        commandLine(triple.args)
                    }
                }
            }
        }

        tasks.withType<KotlinNativeSimulatorTest> {
            dependsOn(newTasks[1].name)
            finalizedBy(newTasks[2].name)
            device.set(iOSSimulatorUuid)
            standalone.set(false)
        }
    }
}
