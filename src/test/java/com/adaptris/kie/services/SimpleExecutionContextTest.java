package com.adaptris.kie.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.adaptris.core.XStreamJsonMarshaller;

public class SimpleExecutionContextTest {
  @Rule
  public TestName testName = new TestName();

  @Test
  public void testMarshaller() throws Exception {
    SimpleExecutionContext ctx = new SimpleExecutionContext();
    assertNull(ctx.getMarshaller());
    assertNotNull(ctx.marshaller());
    ctx.withMarshaller(new XStreamJsonMarshaller());
    assertEquals(XStreamJsonMarshaller.class, ctx.marshaller().getClass());
  }

  @Test
  public void testInsertId() throws Exception {
    SimpleExecutionContext ctx = new SimpleExecutionContext();
    assertNull(ctx.getInsertId());
    ctx.withInsertId("testInsertId");
    assertEquals("testInsertId", ctx.getInsertId());
  }

  @Test
  public void testQueryResultId() throws Exception {
    SimpleExecutionContext ctx = new SimpleExecutionContext();
    assertNull(ctx.getQueryResultId());
    ctx.withQueryResultId("testQueryResultId");
    assertEquals("testQueryResultId", ctx.getQueryResultId());
  }

  @Test
  public void testQueryName() throws Exception {
    SimpleExecutionContext ctx = new SimpleExecutionContext();
    assertNull(ctx.getQueryName());
    ctx.withQueryName("testQueryName");
    assertEquals("testQueryName", ctx.getQueryName());
  }

  @Test
  public void testQueryResultRowId() throws Exception {
    SimpleExecutionContext ctx = new SimpleExecutionContext();
    assertNull(ctx.getQueryResultRowId());
    ctx.withQueryResultRowId("testQueryResultRowId");
    assertEquals("testQueryResultRowId", ctx.getQueryResultRowId());
  }

  @Test
  public void testUsesQuery() throws Exception {
    SimpleExecutionContext ctx = new SimpleExecutionContext();
    assertFalse(ctx.usesQuery());
    ctx.withQueryName("queryName").withQueryResultId("queryResult").withQueryResultRowId("queryResultRowId");
    assertTrue(ctx.usesQuery());
  }

}
