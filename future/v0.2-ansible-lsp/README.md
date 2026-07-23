# v0.2.0 — FQCN + Jinja2 + scope de YAML (en pausa)

Este codigo compila y sus tests pasan (9/9 al momento de apartarlo), pero
se saco de `src/main/kotlin` para que v0.1.0 (solo vault) no dependa de
LSP4IJ ni del plugin de YAML — asi `verifyPlugin` no marca una referencia
a clases de otro plugin sin declarar la dependencia.

Bloqueador real antes de reactivar esto (ver ARCHITECTURE.md, sección 2):
**empaquetar Node + `@ansible/ansible-language-server` dentro del plugin.**
Hoy `AnsibleLanguageServer.kt` asume `node` en el PATH y una ruta relativa
a `node_modules/` que solo resuelve por casualidad en el sandbox de
desarrollo — eso se rompe apenas se instala el plugin de verdad. Ansible
tampoco corre en Windows nativo (confirmado a mano: falla al importar
`fcntl`), asi que no se puede pedirle al usuario "instala Ansible" como
atajo.

## Para reactivar

1. Mover `detection/` y `lsp/` de vuelta a
   `src/main/kotlin/dev/gaphunter/ansiblecompanion/`, y `detection-tests/`
   a `src/test/kotlin/dev/gaphunter/ansiblecompanion/detection/`.
2. Devolver a `build.gradle.kts`: `bundledPlugin("org.jetbrains.plugins.yaml")`
   y `plugin("com.redhat.devtools.lsp4ij", "0.20.1")` (o version vigente).
3. Devolver a `plugin.xml`: los `<depends>` de yaml/lsp4ij y los dos
   bloques `<extensions>` (fileTypeOverrider + server/fileTypeMapping).
4. Resuelto el bundling de Node, actualizar `AnsibleLanguageServer.kt`
   para apuntar al runtime empaquetado, no a `node` del sistema.
