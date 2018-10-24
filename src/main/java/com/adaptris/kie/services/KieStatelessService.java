package com.adaptris.kie.services;

import java.util.Collection;
import java.util.Collections;

import org.kie.api.KieBase;
import org.kie.api.runtime.StatelessKieSession;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ComponentLifecycle;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stateless JBoss Rules Engine execution.
 * 
 * @config kie-stateless-service
 */
@XStreamAlias("kie-stateless-service")
@AdapterComponent
@ComponentProfile(summary = "Stateless KIE/Drools Engine execution", tag = "service,drools,kie", recommended =
{
    KieConnection.class
})
@DisplayOrder(order =
{
    "connection", "executionContext"
})
public class KieStatelessService extends KieServiceImpl<StatelessKieSession> {

  public KieStatelessService() {
  }

  @Override
  protected Collection<ComponentLifecycle> wrappedLifecycleObjects() {
    return Collections.EMPTY_LIST;
  }

  @Override
  protected StatelessKieSession getExecutor(KieBase base, AdaptrisMessage msg) throws Exception {
    return base.newStatelessKieSession();
  }

}
