package tech.mlsql.serviceframework.platform.action

import tech.mlsql.common.utils.serder.json.JSONTool
import tech.mlsql.serviceframework.platform.PluginLoader

class RegisterPluginAction extends CustomAction {
  override def run(params: Map[String, String]): String = {
    val url = params("url")
    val className = params("className")
    val loader = PluginLoader.load(url, className)
    JSONTool.toJsonStr(List())
  }
}
