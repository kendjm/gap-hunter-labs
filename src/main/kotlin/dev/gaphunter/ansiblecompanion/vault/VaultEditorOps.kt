package dev.gaphunter.ansiblecompanion.vault

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

/**
 * Logica de "aplicar el cambio al documento" separada del AnAction/dialogo
 * a proposito -> permite un test de plataforma real (VaultEditorOpsTest,
 * via CodeInsightTestFixture) sin necesidad de interceptar un dialogo
 * Swing modal en un entorno headless.
 */
object VaultEditorOps {

    fun encryptSelection(project: Project?, editor: Editor, password: String) {
        val model = editor.selectionModel
        val selected = model.selectedText ?: return
        val vaultText = AnsibleVaultCipher.encrypt(selected.toByteArray(Charsets.UTF_8), password)
        val start = model.selectionStart
        val end = model.selectionEnd
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(start, end, vaultText)
        }
    }

    fun decryptSelection(project: Project?, editor: Editor, password: String) {
        val model = editor.selectionModel
        val selected = model.selectedText ?: return
        val plaintext = AnsibleVaultCipher.decrypt(selected, password)
        val start = model.selectionStart
        val end = model.selectionEnd
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(start, end, String(plaintext, Charsets.UTF_8))
        }
    }
}
