import java.util.Locale

rootProject.name = "FlameTech"

listOf("api", "Server").forEach { name ->
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    project(":$projName").projectDir = file(name)
}
