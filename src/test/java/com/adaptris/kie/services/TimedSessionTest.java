package com.adaptris.kie.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.mockito.Mockito;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.util.TimeInterval;

public class TimedSessionTest {

  @Rule
  public TestName testName = new TestName();

  @Test
  public void testSessionLifetime() throws Exception {
    TimedSessionn strat = new TimedSessionn();
    assertNull(strat.getSessionLifetime());
    assertEquals(new TimeInterval(10L, TimeUnit.MINUTES).toMilliseconds(), strat.sessionLifetime());
    TimeInterval t = new TimeInterval(10L, TimeUnit.HOURS);
    strat.setSessionLifetime(t);
    assertEquals(t, strat.getSessionLifetime());
    assertEquals(t.toMilliseconds(), strat.sessionLifetime());
  }

  @Test
  public void testGetKieBase() throws Exception {
    KieBase mockKieBase = Mockito.mock(KieBase.class);
    KieSession session1 = Mockito.mock(KieSession.class);
    KieSession session2 = Mockito.mock(KieSession.class);
    when(mockKieBase.newKieSession()).thenReturn(session1, session2);
    TimedSessionn strat = new TimedSessionn(new TimeInterval(1L, TimeUnit.MINUTES));
    try {
      LifecycleHelper.initAndStart(strat);
      AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
      assertEquals(session1, strat.get(mockKieBase, msg));
      assertEquals(session1, strat.get(mockKieBase, msg));
      LifecycleHelper.stopAndClose(strat);
      LifecycleHelper.initAndStart(strat);
      // Should be a new session, because the session is null; so the session should be expired.
      assertEquals(session2, strat.get(mockKieBase, msg));
    } finally {
      LifecycleHelper.stopAndClose(strat);
    }
  }

}
