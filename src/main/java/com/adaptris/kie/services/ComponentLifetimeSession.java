package com.adaptris.kie.services;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import com.adaptris.core.AdaptrisMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Session management that retains the same session for the lifetime of the component.
 * <p>
 * The {@code KieSession} is created upon the first invocation of {@link #get(KieBase, AdaptrisMessage)} and is disposed when this
 * component's {@link #stop()} method is invoked as part of the parent {@link KieService} lifecycle.
 * </p>
 * 
 * @config kie-component-lifecycle-session
 */
@XStreamAlias("kie-component-lifecycle-session")
public class ComponentLifetimeSession extends SessionManagementImpl {

  @Override
  public synchronized KieSession get(KieBase b, AdaptrisMessage msg) {
    if (session == null) {
      session = b.newKieSession();
    }
    return session;
  }

}
