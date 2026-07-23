package dev.gaphunter.ansiblecompanion.vault

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Nunca guarda ni cachea el password entre llamadas — cada encriptar/
 * desencriptar lo vuelve a pedir. Mas fricción, cero riesgo de dejar un
 * secreto en memoria mas tiempo del necesario.
 */
class VaultPasswordDialog(title: String) : DialogWrapper(true) {
    private val field = JBPasswordField()

    init {
        this.title = title
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridLayout(2, 1, 0, 6))
        panel.add(JBLabel("Vault password:"))
        panel.add(field)
        return panel
    }

    override fun getPreferredFocusedComponent(): JComponent = field

    fun password(): CharArray = field.password
}
