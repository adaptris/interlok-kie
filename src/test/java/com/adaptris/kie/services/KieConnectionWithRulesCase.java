package com.adaptris.kie.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.w3c.dom.Document;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.ServiceException;
import com.adaptris.core.util.DocumentBuilderFactoryBuilder;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.core.util.XmlHelper;
import com.adaptris.kie.test.model.Computer;
import com.adaptris.kie.test.model.Desktop;
import com.adaptris.kie.test.model.Person;
import com.adaptris.util.text.xml.XPath;

public abstract class KieConnectionWithRulesCase extends KieServiceExample {

  @Test
  public void testUncheckedLifecycle() throws Exception {
    KieServiceImpl.uncheckedLifecycle(Arrays.asList(new ComponentLifetimeSession()), e -> {
      LifecycleHelper.initAndStart(e);
    });
    SessionManagement working = (kieBase, msg) -> null;
    SessionManagement failing = new SessionManagement() {

      @Override
      public KieSession get(KieBase kieBase, AdaptrisMessage msg) throws Exception {
        return null;
      }

      @Override
      public void start() throws CoreException {
        throw new CoreException();
      }
    };
    try {
      KieServiceImpl.uncheckedLifecycle(Arrays.asList(working, failing), e -> {
        LifecycleHelper.initAndStart(e);
      });
      fail();
    } catch (RuntimeException expected) {
    } finally {
      KieServiceImpl.uncheckedLifecycle(Arrays.asList(working, failing), e -> {
        LifecycleHelper.stopAndClose(e);
      });
    }
  }

  @Test
  public void testLifecycle() throws Exception {
    KieServiceImpl<?> service = addConnection(createForTests());
    try {
      LifecycleHelper.prepare(service);
      fail();
    } catch (CoreException expected) {

    }
    service.withExecutionContext(new SimpleExecutionContext().withInsertId("payload"));
    LifecycleHelper.stopAndClose(LifecycleHelper.initAndStart(service));
  }

  @Test
  public void testService_WithQuery() throws Exception {
    KieServiceImpl<?> service = addConnection(createForTests()).withExecutionContext(new SimpleExecutionContext()
        .withQueryName("getComputerRecommendation").withInsertId("payload").withQueryResultId("computer").withQueryResultRowId("$c"));
    try {
      LifecycleHelper.initAndStart(service);
      AdaptrisMessage msg = createMessage(playGod("manager", "travelling"));
      service.doService(msg);
      DocumentBuilderFactoryBuilder builder = DocumentBuilderFactoryBuilder.newInstance();
      XPath xpath = XPath.newXPathInstance(builder, null);
      Document doc = XmlHelper.createDocument(msg, builder);
      assertEquals("laptop", xpath.selectSingleTextItem(doc, "local-name(/*)"));
      assertEquals(Computer.OperatingSystem.Windows.name(), xpath.selectSingleTextItem(doc, "/laptop/operating-system"));

      AdaptrisMessage msg2 = createMessage(playGod("developer", "gaming"));
      service.doService(msg2);
      try (InputStream in = msg2.getInputStream()) {
        Computer c = (Computer) DefaultMarshaller.getDefaultMarshaller().unmarshal(in);
        assertEquals(Desktop.class, c.getClass());
        assertEquals(Computer.OperatingSystem.Linux, c.getOperatingSystem());
      }
    } finally {
      LifecycleHelper.stopAndClose(service);
    }
  }

  @Test
  public void testService_WithoutQuery() throws Exception {
    KieServiceImpl<?> service = addConnection(createForTests()).withExecutionContext(new SimpleExecutionContext().withInsertId("payload"));
    try {
      LifecycleHelper.initAndStart(service);

      AdaptrisMessage msg = createMessage(playGod(1979)); // Should resolve to GenX.
      service.doService(msg);
      DocumentBuilderFactoryBuilder builder = DocumentBuilderFactoryBuilder.newInstance();
      XPath xpath = XPath.newXPathInstance(builder, null);
      Document doc = XmlHelper.createDocument(msg, builder);
      assertEquals("person", xpath.selectSingleTextItem(doc, "local-name(/*)"));
      assertEquals(Person.Generation.GenerationX.name(), xpath.selectSingleTextItem(doc, "/person/generation"));

      AdaptrisMessage msg2 = createMessage(playGod(1990)); // Should resolve to Millenial
      service.doService(msg2);
      try (InputStream in = msg2.getInputStream()) {
        Person p = (Person) DefaultMarshaller.getDefaultMarshaller().unmarshal(in);
        assertEquals(Person.Generation.Millenial, p.getGeneration());
      }
    } finally {
      LifecycleHelper.stopAndClose(service);
    }
  }

  @Test
  public void testRule_CompileFails() throws Exception {
    KieServiceImpl<?> service = createForTests().withConnection(new KieConnectionWithRules().withRules(PROPERTIES.getProperty(BROKEN_DRL)))
        .withExecutionContext(new SimpleExecutionContext().withInsertId("payload"));
    try {
      LifecycleHelper.initAndStart(service);
      AdaptrisMessage msg = createMessage(playGod("manager", "travelling"));
      execute(service, msg);
      fail();
    } catch (ServiceException expected) {
      expected.printStackTrace();
    } finally {
      LifecycleHelper.stopAndClose(service);
    }
  }

  protected KieServiceImpl<?> addConnection(KieServiceImpl<?> s) {
    return s.withConnection(
        new KieConnectionWithRules().withRules(PROPERTIES.getProperty(COMPUTER_CHOOSER), PROPERTIES.getProperty(GENERATION_CHOOSER)));
  }

  protected abstract KieServiceImpl<?> createForTests();

  @Override
  protected KieServiceImpl<?> retrieveObjectForSampleConfig() {
    KieServiceImpl<?> service = createForTests();
    service.setExecutionContext(new SimpleExecutionContext().withInsertId("payload"));
    service.setConnection(new KieConnectionWithRules().withRules("file:////path/to/first/rule.drl", "file:////path/to/another/rule.drl"));
    return service;
  }

  protected Person playGod(String profession, String hobby) {
    return new Person().withHobbies(hobby).withProfession(profession);
  }

  protected Person playGod(int birthYear) {
    return new Person().withYearOfBirth(birthYear);
  }

}
