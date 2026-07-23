# Ansible Companion

Plugin de IntelliJ/PyCharm para Ansible. Nace de evidencia real, no de
suposiciones: ver `out/evidence_*.md` en la raiz del repo (generados por
`pipeline/03_evidence.py --from-shortlist 8`) y el veredicto completo en la
conversacion del proyecto.

## Las 3 quejas que construye este plugin

1. **No secuestrar YAML que no es tuyo.** El incumbente gratis (id 7792)
   reclama todo `.yml`/`.yaml` y rompe Kubernetes/Helm/Docker-compose.
   -> `AnsibleFileDetector` (ver `src/main/kotlin/.../detection`), con tests.
2. **Completado consciente de FQCN** (`ansible.builtin.include_role`, no solo
   `include_role`). -> envuelto via `ansible-language-server` (Red Hat, MIT)
   a traves de LSP4IJ, no reimplementado a mano.
3. **Jinja2 dentro de YAML sin falsos positivos** (el incumbente pago, id
   12626, llego a crashear PyCharm con esto). -> mismo language server.

## Que NO hay que romper

Vault de/encriptado en el IDE (incluyendo `ANSIBLE_VAULT_PASSWORD_FILE` como
script), soporte de roles, preview de variables multi-entorno. Ver el
veredicto completo para el detalle de por que.

## Estado

Scaffold + arquitectura decidida (dia 1). El wiring de LSP4IJ compila contra
la version fijada del plugin; falta conectar `AnsibleFileDetector` a un
`FileTypeOverrider` real (ver TODO en `plugin.xml`) y empaquetar el language
server para no depender de que el usuario tenga Node instalado. Ver
`ARCHITECTURE.md` en la raiz del repo.

## Desarrollo

Maquina de referencia: 8GB RAM, i3 5ta gen -> `org.gradle.jvmargs` ya viene
bajado en `gradle.properties`. Dia a dia usar `./gradlew build` y
`verifyPlugin`; reservar `runIde` (levanta un IntelliJ completo) para pruebas
puntuales, no para el loop normal.
