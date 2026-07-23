package dev.gaphunter.ansiblecompanion.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider

class AnsibleLanguageServerFactory : LanguageServerFactory {

    override fun createConnectionProvider(project: Project): StreamConnectionProvider =
        AnsibleLanguageServer()

    override fun createLanguageClient(project: Project): LanguageClientImpl =
        LanguageClientImpl(project)
}
