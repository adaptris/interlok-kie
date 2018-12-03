package com.adaptris.kie.services;

import org.kie.api.KieBase;
import org.kie.api.KieServices;

import com.adaptris.core.AdaptrisConnectionImp;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.ExceptionHelper;

public abstract class KieConnection extends AdaptrisConnectionImp {

  protected transient KieServices services;
  protected transient KieBase kieBase;

  public KieConnection() {
  }

  @Override
  protected void prepareConnection() throws CoreException {
    services = KieServices.Factory.get();
  }

  @Override
  protected void initConnection() throws CoreException {
  }

  @Override
  protected void startConnection() throws CoreException {
  }

  @Override
  protected void stopConnection() {
  }

  @Override
  protected void closeConnection() {
    kieBase = null;
  }

  public KieBase getKieBase() throws CoreException {
    try {
      if (kieBase == null) {
        kieBase = buildKieBase();
      }
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return kieBase;
  }

  protected abstract KieBase buildKieBase() throws Exception;

}
