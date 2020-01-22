package tech.mlsql.serviceframework.platform

import net.csdn.ServiceFramwork
import net.csdn.bootstrap.Application
import tech.mlsql.common.utils.shell.command.ParamsUtil
import tech.mlsql.serviceframework.platform.action.RegisterPluginAction
import tech.mlsql.serviceframework.platform.app.AppManager

import scala.collection.JavaConverters._

object AppRuntime {
  def main(args: Array[String]): Unit = {
    val params = new ParamsUtil(args)
    loadPlugin(params)
    val applicationYamlName = params.getParam("config", "application.yml")
    ServiceFramwork.applicaionYamlName(applicationYamlName)
    ServiceFramwork.scanService.setLoader(classOf[AppRuntime])
    ServiceFramwork.enableNoThreadJoin()

    AppRuntimeStore.store.getApps().foreach { appItem =>
      AppManager.call(appItem.name, params.getParamsMap.asScala.toMap)
    }

    Application.main(args)
    Thread.currentThread().join()

  }

  def loadPlugin(params: ParamsUtil) = {
    if (params.hasParam("pluginPaths")) {
      val paths = params.getParam("pluginPaths").split(",")
      val names = params.getParam("pluginNames").split(",")
      names.zip(paths).map { case (name, path) =>
        PluginLoader.load(path, name)
      }
    }

    val actionName = "registerPlugin"
    if (!AppRuntimeStore.store.getAction(actionName).isDefined) {
      val defaultPlugin = new Plugin() {
        override def entries: List[PluginItem] = {
          List(PluginItem(actionName, classOf[RegisterPluginAction].getName, PluginType.action, None))
        }
      }
      AppRuntimeStore.store.registerAction(actionName, classOf[RegisterPluginAction].getName,
        PluginLoader(Thread.currentThread().getContextClassLoader, defaultPlugin))
    }
  }
}

class AppRuntime {

}
