package com.adaptris.kie.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.holiday.model.Holiday;
import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.interlok.junit.scaffolding.services.ExampleServiceCase;
import com.adaptris.util.TimeInterval;

public class KieKjarConnectionTest extends BaseCase {
  protected static final String GROUP_ID = "drools.kjar.groupId";
  protected static final String ARTIFACT_ID = "drools.kjar.artifactId";
  protected static final String VERSION = "drools.kjar.version";
  protected static final String KIE_BASE = "drools.kjar.kiebase";

  @Test
  public void testService_RescanEnabled() throws Exception {
    KieServiceImpl<?> service = new KieService()
        .withConnection(new KieKjarConnection()
            .withReleaseId(PROPERTIES.getProperty(GROUP_ID), PROPERTIES.getProperty(ARTIFACT_ID), PROPERTIES.getProperty(VERSION))
            .withKieBaseName(PROPERTIES.getProperty(KIE_BASE)).withRescan(true, new TimeInterval(1L, TimeUnit.HOURS)))
        .withExecutionContext(new SimpleExecutionContext().withInsertId("payload"));
    try {
      AdaptrisMessage msg = KieServiceExample.createMessage(new Holiday().withDestination("cornwall"));
      ExampleServiceCase.execute(service, msg);
      try (InputStream in = msg.getInputStream()) {
        Holiday holiday = (Holiday) DefaultMarshaller.getDefaultMarshaller().unmarshal(in);
        assertEquals(Holiday.TransportType.Automobile, holiday.getTransportType());
        assertTrue(holiday.getStopOffs().contains("Stonehenge"));
      }
    } finally {
      LifecycleHelper.stopAndClose(service);
    }
  }

  @Test
  public void testService_RescanDisabled() throws Exception {
    KieServiceImpl<?> service = new KieService().withConnection(new KieKjarConnection()
        .withReleaseId(PROPERTIES.getProperty(GROUP_ID), PROPERTIES.getProperty(ARTIFACT_ID), PROPERTIES.getProperty(VERSION))
        .withKieBaseName(PROPERTIES.getProperty(KIE_BASE))).withExecutionContext(new SimpleExecutionContext().withInsertId("payload"));
    try {
      AdaptrisMessage msg = KieServiceExample.createMessage(new Holiday().withDestination("sydney"));
      ExampleServiceCase.execute(service, msg);
      try (InputStream in = msg.getInputStream()) {
        Holiday holiday = (Holiday) DefaultMarshaller.getDefaultMarshaller().unmarshal(in);
        assertEquals(Holiday.TransportType.Plane, holiday.getTransportType());
        assertTrue(holiday.getStopOffs().contains("Hong Kong"));
      }
    } finally {
      LifecycleHelper.stopAndClose(service);
    }
  }

  @Test
  public void testRescan() throws Exception {
    KieKjarConnection conn = new KieKjarConnection();
    assertNull(conn.getRescan());
    assertFalse(conn.rescan());
    conn.withRescan(Boolean.TRUE);
    assertEquals(Boolean.TRUE, conn.getRescan());
    assertTrue(conn.rescan());
  }

  @Test
  public void testRescanInterval() throws Exception {
    KieKjarConnection conn = new KieKjarConnection();
    assertNull(conn.getRescanInterval());
    assertEquals(new TimeInterval(10L, TimeUnit.MINUTES).toMilliseconds(), conn.rescanIntervalMillis());
    TimeInterval t = new TimeInterval(1L, TimeUnit.HOURS);
    conn.withRescanInterval(t);
    assertEquals(t, conn.getRescanInterval());
    assertEquals(t.toMilliseconds(), conn.rescanIntervalMillis());
  }

  @Test
  public void testLifecycle() throws Exception {
    KieKjarConnection conn = new KieKjarConnection();
    try {
      LifecycleHelper.initAndStart(conn);
      fail();
    } catch (CoreException expected) {

    }
    conn.withReleaseId(PROPERTIES.getProperty(GROUP_ID), PROPERTIES.getProperty(ARTIFACT_ID), PROPERTIES.getProperty(VERSION))
        .withKieBaseName(PROPERTIES.getProperty(KIE_BASE));
    LifecycleHelper.stopAndClose(LifecycleHelper.initAndStart(conn));
  }

}
