import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.2.6.2")

        // v0.1.0 es solo vault (javax.crypto puro) -> sin dependencia de
        // YAML/LSP4IJ. Ese codigo sigue en future/v0.2-ansible-lsp/,
        // apartado de la compilacion para que verifyPlugin no marque una
        // referencia a clases de otro plugin sin declarar la dependencia.
        // Ver future/v0.2-ansible-lsp/README.md para reactivarlo.

        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            // 243 = 2024.3, para no dejar afuera la base instalada real.
            sinceBuild = "243"
            // Sin techo: un untilBuild estrecho es la forma mas comun de morir
            // en este marketplace (se desactiva solo en el siguiente release).
            untilBuild = provider { null }
        }
    }

    // El instrumentador de bytecode (agrega asserts de @NotNull/@Nullable en
    // runtime) falla en esta combinacion Gradle 9.5/plugin 2.16/IDE 2025.2.6.2
    // con "instrumentIdeaExtensions doesn't support the nested element" -> bug
    // de tooling documentado en varias versiones del plugin, no de este codigo
    // (compileKotlin/compileTestKotlin compilan bien). No es requerido para
    // build/test/verifyPlugin; reactivar si una version futura del plugin lo
    // arregla y se quiere el chequeo extra en runtime.
    instrumentCode = false
}
