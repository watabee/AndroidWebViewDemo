import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.File
import java.util.Locale

class BundleJsPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.plugins.withType(AppPlugin::class.java) {
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant ->
                variant.sources.assets
                    ?.let {
                        val variantName = variant.name.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(Locale.US) else it.toString()
                        }
                        val bundleJsTask = project.tasks.register<BundleJsTask>("bundleJs${variantName}") {
                            workingDir = File(project.projectDir, "typescript")
                            typescriptDir.set(workingDir)
                            nodeModulesDir.set(File(workingDir, "node_modules"))
                        }

                        it.addGeneratedSourceDirectory(
                            bundleJsTask,
                            BundleJsTask::outputDirectory
                        )
                    }
            }
        }
    }
}

abstract class BundleJsTask : Exec() {
    @Internal
    val typescriptDir: DirectoryProperty = project.objects.directoryProperty()

    @Internal
    val nodeModulesDir: DirectoryProperty = project.objects.directoryProperty()

    @get:InputFiles
    val inputFiles: FileTree
        get() = typescriptDir.asFileTree.matching {
            // node_modules のディレクトリは除外する
            it.exclude("${nodeModulesDir.get().asFile.name}/**")
        }

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    override fun exec() {
        val outputDir = outputDirectory.get().asFile
        outputDir.mkdirs()

        commandLine = listOf("sh", "bundle.sh", "$outputDir")
        super.exec()
    }
}
