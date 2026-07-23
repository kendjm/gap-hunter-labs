package dev.gaphunter.ansiblecompanion.vault

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Prueba real de plataforma (levanta un proyecto/editor ligero en memoria,
 * headless) en vez de runIde con clicks de mouse — verifica que la accion
 * de verdad reemplaza el texto del documento, no solo que el cifrado esta
 * bien (eso ya lo prueba AnsibleVaultCipherTest contra el vector oficial).
 */
class VaultEditorOpsTest : BasePlatformTestCase() {

    fun testEncryptThenDecryptSelectionRoundTripsInRealEditor() {
        myFixture.configureByText("secrets.yml", "db_password: <selection>super-secret-123</selection>")
        val editor = myFixture.editor

        VaultEditorOps.encryptSelection(project, editor, "clave-de-prueba")

        val afterEncrypt = editor.document.text
        assertTrue("el documento deberia contener el header de vault", afterEncrypt.contains("\$ANSIBLE_VAULT;1.1;AES256"))
        assertFalse("el secreto en claro no deberia seguir en el documento", afterEncrypt.contains("super-secret-123"))

        val vaultStart = afterEncrypt.indexOf("\$ANSIBLE_VAULT")
        editor.selectionModel.setSelection(vaultStart, afterEncrypt.length)

        VaultEditorOps.decryptSelection(project, editor, "clave-de-prueba")

        val afterDecrypt = editor.document.text
        assertEquals("db_password: super-secret-123", afterDecrypt)
    }

    fun testWrongPasswordThrowsInsteadOfCorruptingDocument() {
        myFixture.configureByText("secrets.yml", "<selection>otro-secreto</selection>")
        val editor = myFixture.editor

        VaultEditorOps.encryptSelection(project, editor, "clave-correcta")
        editor.selectionModel.setSelection(0, editor.document.textLength)

        try {
            VaultEditorOps.decryptSelection(project, editor, "clave-incorrecta")
            fail("deberia haber lanzado VaultFormatException")
        } catch (e: AnsibleVaultCipher.VaultFormatException) {
            assertTrue(editor.document.text.contains("\$ANSIBLE_VAULT"))
        }
    }
}
