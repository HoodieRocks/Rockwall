package me.cobble.rockwall.config

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.block.implementation.Section
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import me.cobble.rockwall.rockwall.Rockwall
import java.io.File

object FormatsConfig {
    private lateinit var document: YamlDocument

    fun setup(plugin: Rockwall) {
        document = YamlDocument.create(
            File(plugin.dataFolder, "formats.yml"),
            plugin.getResource("formats.yml")!!,
            GeneralSettings.DEFAULT,
            LoaderSettings.builder().setAutoUpdate(true).build(),
            DumperSettings.DEFAULT,
            UpdaterSettings
                .builder()
                .setVersioning(BasicVersioning("version"))
                .build()
        )
    }

    private fun get(): YamlDocument {
        return document
    }

    fun getSection(path: String): Section? {
        return get().getSection(path)
    }

    fun reload() {
        document.reload()
    }
}