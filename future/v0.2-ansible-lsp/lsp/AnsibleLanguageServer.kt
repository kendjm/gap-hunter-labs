package dev.gaphunter.ansiblecompanion.lsp

import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider

/**
 * Paquete y entrypoint verificados a mano (2026-07-23): `npm view` confirma
 * que el paquete vigente es el con scope, `@ansible/ansible-language-server`
 * (el sin scope, `ansible-language-server`, existe pero esta en 0.1.1-0 —
 * abandonado). Su `bin/ansible-language-server` a su vez solo hace
 * `require("../out/server/src/server.js")`, que es el path de abajo.
 *
 * TODO antes de publicar: hoy asume `node` y el paquete instalados a mano en
 * la maquina de desarrollo (`npm i @ansible/ansible-language-server` en
 * plugin/). Falta empaquetar un runtime de Node portable + el paquete DENTRO
 * del plugin (tarea de Gradle) para que el usuario final no necesite Node
 * instalado — sin esto no es publicable.
 */
class AnsibleLanguageServer : ProcessStreamConnectionProvider() {
    init {
        setCommands(listOf("node", ANSIBLE_LS_ENTRYPOINT, "--stdio"))
    }

    companion object {
        const val ANSIBLE_LS_ENTRYPOINT =
            "node_modules/@ansible/ansible-language-server/out/server/src/server.js"
    }
}
