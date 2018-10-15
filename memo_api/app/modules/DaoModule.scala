package modules

import com.google.inject.AbstractModule
import dao._

class DaoModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[MemoDao]).to(classOf[MemoDaoImpl]).asEagerSingleton()
  }
}
