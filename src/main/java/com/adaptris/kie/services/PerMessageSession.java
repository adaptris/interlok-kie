package com.adaptris.kie.services;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import com.adaptris.core.AdaptrisMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Create a new session each time {@link #get(KieBase, AdaptrisMessage)} is invoked.
 * 
 * 
 * @config kie-per-message-session
 */
@XStreamAlias("kie-per-message-session")
public class PerMessageSession extends SessionManagementImpl {

  @Override
  public synchronized KieSession get(KieBase b, AdaptrisMessage msg) {
    SessionManagement.disposeQuietly(session);
    return b.newKieSession();
  }
}
