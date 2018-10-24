package com.adaptris.kie.services;

import org.kie.api.runtime.KieSession;

public abstract class SessionManagementImpl implements SessionManagement {
  protected transient KieSession session = null;

  @Override
  public void stop() {
    SessionManagement.disposeQuietly(session);
    session = null;
  }
}
