package dev.gaphunter.ansiblecompanion.detection

import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.yaml.YAMLFileType
import org.jetbrains.yaml.YAMLLanguage

/**
 * Mismo lenguaje que YAML (misma sintaxis, folding, PSI: reutiliza
 * YAMLLanguage.INSTANCE) pero un FileType distinto, para que LSP4IJ pueda
 * mapear el language server de Ansible SOLO a estos archivos y no a
 * cualquier .yml/.yaml — ver AnsibleFileTypeOverrider.
 */
object AnsibleYamlFileType : LanguageFileType(YAMLLanguage.INSTANCE) {
    override fun getName() = "Ansible YAML"
    override fun getDescription() = "Playbook, role o task file de Ansible"
    override fun getDefaultExtension() = "yml"
    override fun getIcon() = YAMLFileType.YML.icon
}
