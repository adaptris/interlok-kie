package com.adaptris.kie.services;

import java.util.Arrays;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ComponentLifecycle;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stateful KIE/Drools Engine execution.
 * 
 * @config kie-service
 */
@XStreamAlias("kie-service")
@AdapterComponent
@ComponentProfile(summary = "Stateful KIE/Drools Engine execution", tag = "service,drools,kie", recommended =
{
    KieConnection.class
})
@DisplayOrder(order =
{
    "connection", "executionContext", "sessionManagementStrategy"
})
public class KieService extends KieServiceImpl<KieSession> {

  @NotNull(message = "KIE SessionManagement may not be null")
  @AutoPopulated
  @Valid
  @InputFieldDefault(value = "per-message")
  private SessionManagement sessionManagement;

  public KieService() {
    super();
    setSessionManagement(new PerMessageSession());
  }

  protected KieSession getExecutor(KieBase base, AdaptrisMessage msg) throws Exception {
    return getSessionManagement().get(base, msg);
  }


  /**
   * @return the sessionStrategy
   */
  public SessionManagement getSessionManagement() {
    return sessionManagement;
  }

  /**
   * Set the strategy for managing sessions.
   *
   * @see SessionManagement
   * @param s the sessionStrategy to set, if not specified, defaults to {@link PerMessageSession}.
   */
  public void setSessionManagement(SessionManagement s) {
    sessionManagement = Args.notNull(s, "sessionManagement");
  }

  @Override
  protected Collection<ComponentLifecycle> wrappedLifecycleObjects() {
    return Arrays.asList(getSessionManagement());
  }

}
