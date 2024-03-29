package com.adaptris.kie.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.mockito.Mockito;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.util.LifecycleHelper;

public class PerMessageSessionTest {

  @Test
  public void testGetKieBase() throws Exception {
    KieBase mockKieBase = Mockito.mock(KieBase.class);
    KieSession session1 = Mockito.mock(KieSession.class);
    KieSession session2 = Mockito.mock(KieSession.class);
    when(mockKieBase.newKieSession()).thenReturn(session1, session2);
    PerMessageSession strat = new PerMessageSession();
    try {
      LifecycleHelper.initAndStart(strat);
      AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
      assertEquals(session1, strat.get(mockKieBase, msg));
      assertEquals(session2, strat.get(mockKieBase, msg));
      assertEquals(session2, strat.get(mockKieBase, msg));
    } finally {
      LifecycleHelper.stopAndClose(strat);
    }
  }

}
