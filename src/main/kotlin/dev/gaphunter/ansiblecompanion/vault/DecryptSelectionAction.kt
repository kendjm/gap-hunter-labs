package dev.gaphunter.ansiblecompanion.vault

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class DecryptSelectionAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selected = editor.selectionModel.selectedText
        if (selected.isNullOrEmpty() || !AnsibleVaultCipher.looksEncrypted(selected)) {
            Messages.showWarningDialog(
                "Seleccioná un bloque completo que empiece con \$ANSIBLE_VAULT;1.1;AES256.",
                "Ansible Vault"
            )
            return
        }

        val dialog = VaultPasswordDialog("Desencriptar Ansible Vault")
        if (!dialog.showAndGet()) return
        val password = String(dialog.password())

        try {
            VaultEditorOps.decryptSelection(e.project, editor, password)
        } catch (ex: AnsibleVaultCipher.VaultFormatException) {
            Messages.showErrorDialog(ex.message ?: "no se pudo desencriptar", "Ansible Vault")
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && editor.selectionModel.hasSelection()
    }
}
