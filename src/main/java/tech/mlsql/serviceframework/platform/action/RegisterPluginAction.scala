package tech.mlsql.serviceframework.platform.action

import tech.mlsql.common.utils.log.Logging
import tech.mlsql.common.utils.serder.json.JSONTool
import tech.mlsql.serviceframework.platform.app.AppManager
import tech.mlsql.serviceframework.platform.{AppRuntimeStore, PluginLoader, PluginType}

class RegisterPluginAction extends CustomAction with Logging {
  override def run(params: Map[String, String]): String = {
    val url = params("url")
    val className = params("className")
    val loader = PluginLoader.load(Array(url), className)
    val plugin = loader.plugin
    plugin.entries.foreach { pi =>
      if (pi.pluginType == PluginType.app) {
        AppRuntimeStore.store.getApp(pi.name).foreach { appItem =>
          logInfo(s"Plugin: load plugin ${appItem.name}")
          AppManager.call(appItem.name, Map())
        }
      }
    }
    JSONTool.toJsonStr(List())
  }
}
