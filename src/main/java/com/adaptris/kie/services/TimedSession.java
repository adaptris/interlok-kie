package com.adaptris.kie.services;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.util.TimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Session management strategy that retains the same session for a configurable length of time.
 * <p>
 * The {@code KieBase} is created upon the first invocation of {@link #get(KieBase, AdaptrisMessage)} and is disposed when the
 * interval between calls to {@link #get(KieBase, AdaptrisMessage)} exceeds the configured interval.
 * </p>
 * 
 * @config kie-timed-session
 * 
 */
@XStreamAlias("kie-timed-session")
public class TimedSession extends SessionManagementImpl {

  private static final TimeInterval DEFAULT_LIFETIME = new TimeInterval(10L, TimeUnit.MINUTES);

  @InputFieldDefault(value = "10 minutes")
  private TimeInterval sessionLifetime;
  private transient Calendar sessionEndDate;

  public TimedSession() {
  }

  public TimedSession(TimeInterval lifetime) {
    this();
    setSessionLifetime(lifetime);
  }

  @Override
  public synchronized KieSession get(KieBase b, AdaptrisMessage msg)
      throws Exception {
    if (sessionExpired()) {
      SessionManagement.disposeQuietly(session);
      session = createSession(b);
    }
    return session;
  }

  private KieSession createSession(KieBase b) {
    KieSession mySession = b.newKieSession();
    sessionEndDate = Calendar.getInstance();
    sessionEndDate.add(Calendar.MILLISECOND, sessionLifetime());
    return mySession;
  }

  private boolean sessionExpired() {
    return BooleanUtils.or(new boolean[]
    {
        session == null, sessionEndDate == null, Calendar.getInstance().after(sessionEndDate)
    });
  }

  public TimeInterval getSessionLifetime() {
    return sessionLifetime;
  }

  public void setSessionLifetime(TimeInterval i) {
    sessionLifetime = i;
  }

  protected int sessionLifetime() {
    return Long.valueOf(getSessionLifetime() != null ? getSessionLifetime().toMilliseconds() : DEFAULT_LIFETIME.toMilliseconds())
        .intValue();
  }
}
