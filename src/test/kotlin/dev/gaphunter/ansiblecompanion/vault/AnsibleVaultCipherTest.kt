package dev.gaphunter.ansiblecompanion.vault

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnsibleVaultCipherTest {

    // Vector de prueba real, tomado tal cual de
    // ansible/ansible: test/units/parsing/vault/test_vault.py
    // (test_encrypt_decrypt_aes256_existing_vault). Password y ciphertext
    // son de la propia suite oficial, no inventados.
    private val realPassword = "test-vault-password"
    private val realVaultText = """
        ${'$'}ANSIBLE_VAULT;1.1;AES256
        33363965326261303234626463623963633531343539616138316433353830356566396130353436
        3562643163366231316662386565383735653432386435610a306664636137376132643732393835
        63383038383730306639353234326630666539346233376330303938323639306661313032396437
        6233623062366136310a633866373936313238333730653739323461656662303864663666653563
        3138
    """.trimIndent()

    @Test
    fun decryptsRealAnsibleTestVector() {
        val plaintext = AnsibleVaultCipher.decrypt(realVaultText, realPassword)
        assertEquals("Setec Astronomy", String(plaintext, Charsets.UTF_8))
    }

    @Test
    fun roundTripsOwnEncryption() {
        val secret = "el password del cliente nunca va al repo".toByteArray(Charsets.UTF_8)
        val vaultText = AnsibleVaultCipher.encrypt(secret, "otra-clave-123")
        val back = AnsibleVaultCipher.decrypt(vaultText, "otra-clave-123")
        assertArrayEquals(secret, back)
    }

    @Test
    fun wrongPasswordFailsHmacInsteadOfSilentlyReturningGarbage() {
        val vaultText = AnsibleVaultCipher.encrypt("dato sensible".toByteArray(), "clave-correcta")
        try {
            AnsibleVaultCipher.decrypt(vaultText, "clave-incorrecta")
            throw AssertionError("deberia haber lanzado VaultFormatException")
        } catch (e: AnsibleVaultCipher.VaultFormatException) {
            assertTrue(e.message!!.contains("HMAC"))
        }
    }

    @Test
    fun looksEncryptedDetectsHeader() {
        assertTrue(AnsibleVaultCipher.looksEncrypted(realVaultText))
        assertTrue(!AnsibleVaultCipher.looksEncrypted("hosts: all\ntasks: []\n"))
    }
}
