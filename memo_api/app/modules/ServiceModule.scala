package modules

import com.google.inject.AbstractModule
import services._

class ServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[MemoService]).to(classOf[MemoServiceImpl]).asEagerSingleton()
  }
}

