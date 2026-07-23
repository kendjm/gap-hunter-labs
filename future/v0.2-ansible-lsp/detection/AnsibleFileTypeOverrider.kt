package dev.gaphunter.ansiblecompanion.detection

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.impl.FileTypeOverrider
import com.intellij.openapi.vfs.VirtualFile

private const val HEAD_BYTES = 4096

/**
 * Punto de conexion real de la queja #1: sin esto, cualquier .yml/.yaml
 * queda como YAML plano y el language server de Ansible nunca se activa
 * de forma selectiva (que es justo el bug que hunde a "YAML/Ansible
 * support", ver out/evidence_7792.md).
 *
 * Evita leer el archivo cuando la ruta sola ya alcanza (dentro de roles,
 * subcarpeta tasks/handlers/etc., o de playbooks/) — solo lee las primeras
 * bytes para el caso ambiguo.
 */
class AnsibleFileTypeOverrider : FileTypeOverrider {
    override fun getOverriddenFileType(file: VirtualFile): FileType? {
        if (AnsibleFileDetector.pathAloneSignalsAnsible(file.path)) return AnsibleYamlFileType

        if (!file.name.endsWith(".yml") && !file.name.endsWith(".yaml")) return null

        val head = try {
            val bytes = file.contentsToByteArray()
            String(bytes, 0, minOf(bytes.size, HEAD_BYTES), Charsets.UTF_8)
        } catch (e: Exception) {
            return null
        }

        return if (AnsibleFileDetector.looksLikeAnsible(file.path, head)) AnsibleYamlFileType else null
    }
}
