package dev.gaphunter.ansiblecompanion.detection

/**
 * Responde la queja #1 de out/evidence_7792.md: el plugin gratis lider
 * reclama TODO archivo .yml/.yaml como Ansible y rompe Kubernetes, Helm,
 * Cloudformation y Docker-compose en el proceso.
 *
 * Logica pura y testeable a proposito (sin VirtualFile/PSI) para que se
 * pueda validar con JUnit normal sin levantar la plataforma. Conectada a
 * la plataforma real via AnsibleFileTypeOverrider.
 */
object AnsibleFileDetector {

    private val ROLE_SUBDIRS = setOf("tasks", "handlers", "defaults", "vars", "meta", "library")
    private val PLAYBOOK_SHAPE_KEYS = listOf("hosts:", "roles:", "tasks:", "import_playbook:", "become:")
    private val TASK_SHAPE_KEYS = listOf("ansible.builtin.", "name:", "when:", "with_items:", "loop:", "register:")

    fun looksLikeAnsible(path: String, content: String): Boolean {
        val normalized = path.replace('\\', '/')
        if (!normalized.endsWith(".yml") && !normalized.endsWith(".yaml")) return false
        return pathSignalsAnsible(normalized) || contentSignalsAnsible(content)
    }

    /** Chequeo sin I/O, para que el overrider evite leer el archivo cuando la ruta ya alcanza. */
    fun pathAloneSignalsAnsible(path: String): Boolean {
        val normalized = path.replace('\\', '/')
        if (!normalized.endsWith(".yml") && !normalized.endsWith(".yaml")) return false
        return pathSignalsAnsible(normalized)
    }

    private fun pathSignalsAnsible(path: String): Boolean {
        val segments = path.split('/')
        if (segments.contains("playbooks")) return true

        val rolesIdx = segments.indexOf("roles")
        if (rolesIdx in 0..(segments.size - 3) && segments[rolesIdx + 2] in ROLE_SUBDIRS) return true

        return false
    }

    private fun contentSignalsAnsible(content: String): Boolean {
        val head = content.lineSequence().take(40).joinToString("\n")
        val hasPlaybookShape = PLAYBOOK_SHAPE_KEYS.any { head.contains(it) }
        val hasTaskShape = TASK_SHAPE_KEYS.count { head.contains(it) } >= 2
        return hasPlaybookShape || hasTaskShape
    }
}
