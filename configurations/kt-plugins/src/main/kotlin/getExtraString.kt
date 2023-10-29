import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

fun Project.getExtraString(name: String, default: String = "") =
    if (extra.has(name)) extra[name]?.toString()
    else default