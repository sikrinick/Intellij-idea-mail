package github.jk1.smtpidea.components

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

import com.intellij.openapi.components.StoragePathMacros.*
import com.intellij.openapi.components.StorageScheme.*
import github.jk1.smtpidea.server.pop3.Pop3ServerManager
import github.jk1.smtpidea.config.Pop3Config

/**
 * Manages embedded POP3 server plugin component & provides pop3 configuration persistence.
 */
State(
        name = "Pop3ServerComponent",
        storages = array(
                Storage(id = "default", file = "${PROJECT_FILE}"),
                Storage(id = "dir", file = "${PROJECT_CONFIG_DIR}/pop3.xml", scheme = DIRECTORY_BASED)
        )
)
public class Pop3ServerComponent(val project: Project) : AbstractProjectComponent(project), PersistentStateComponent<Pop3Config> {

    private var config: Pop3Config = Pop3Config()
    private var server: Pop3ServerManager? = null

    public override fun initComponent() {
        server = ServiceManager.getService(project, javaClass<Pop3ServerManager>())
        if (server != null) {
            server!!.configuration = config   //http://youtrack.jetbrains.com/issue/KT-1213
            if (config.launchOnStartup) {
                server?.start()
            }
        }
    }

    public override fun getComponentName(): String = "Pop3ServerComponent"

    public override fun getState(): Pop3Config =  config

    public override fun loadState(pluginConfiguration: Pop3Config?) {
        //may be called anytime, check everything
        if (pluginConfiguration != null) {
            config = pluginConfiguration
            if (server != null) {
                server!!.configuration = config    //http://youtrack.jetbrains.com/issue/KT-1213
            }
        }
    }

    public override fun disposeComponent() {
        server?.stop()
    }
}

