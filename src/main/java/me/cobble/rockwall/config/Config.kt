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
import java.util.*

object Config {

    private lateinit var document: YamlDocument
    fun setup(plugin: Rockwall) {
        document = YamlDocument.create(
            File(plugin.dataFolder, "config.yml"),
            plugin.getResource("config.yml")!!,
            GeneralSettings.DEFAULT,
            LoaderSettings.builder().setAutoUpdate(true).build(),
            DumperSettings.DEFAULT,
            UpdaterSettings
                .builder()
                .setVersioning(BasicVersioning("version"))
                .build()
        )
    }

    fun get(): YamlDocument {
        return document
    }

    fun getSection(path: String): Section? {
        return get().getSection(path)
    }

    fun getBool(path: String): Boolean {
        return get().getBoolean(path)
    }

    fun getInt(path: String): Int {
        return get().getInt(path)
    }

    fun getString(path: String): Optional<String> {
        return get().getOptionalString(path)
    }

    fun reload() {
        document.reload()
    }
}
