import com.soywiz.korge.gradle.*
import com.soywiz.korlibs.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.korge)
    alias(libs.plugins.serialization)
}


kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bundles.offlinebrain)
                implementation(libs.bundles.kotlinx)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


korge {
    id = "com.offlinebrain.hexrl"
// To enable all targets at once

    //targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    //targetJs()
    //targetDesktop()
    //targetIos()
    //targetAndroidIndirect() // targetAndroidDirect()
}
