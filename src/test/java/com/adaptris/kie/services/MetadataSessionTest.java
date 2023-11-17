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

public class MetadataSessionTest {

  @Test
  public void testGetKieBase() throws Exception {
    KieBase mockKieBase = Mockito.mock(KieBase.class);
    KieSession session1 = Mockito.mock(KieSession.class);
    KieSession session2 = Mockito.mock(KieSession.class);
    when(mockKieBase.newKieSession()).thenReturn(session1, session2);
    MetadataSession strat = new MetadataSession("newSessionRequired");
    try {
      LifecycleHelper.initAndStart(strat);
      AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
      assertEquals(session1, strat.get(mockKieBase, msg));
      assertEquals(session1, strat.get(mockKieBase, msg));
      AdaptrisMessage msg2 = AdaptrisMessageFactory.getDefaultInstance().newMessage();
      msg2.addMetadata("newSessionRequired", "blah blah");
      assertEquals(session2, strat.get(mockKieBase, msg2));
    } finally {
      LifecycleHelper.stopAndClose(strat);
    }
  }

}
