package com.adaptris.kie.services;

import java.io.OutputStream;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.interlok.junit.scaffolding.services.ExampleServiceCase;

public abstract class KieServiceExample extends ExampleServiceCase {

  private static final String BASE_DIR_KEY = "DroolsServiceExamples.baseDir";
  protected static final String COMPUTER_CHOOSER = "drools.computers.drl.url";
  protected static final String GENERATION_CHOOSER = "drools.generation.drl.url";
  protected static final String BROKEN_DRL = "drools.broken.drl.url";

  public KieServiceExample() {
    if (PROPERTIES.getProperty(BASE_DIR_KEY) != null) {
      setBaseDir(PROPERTIES.getProperty(BASE_DIR_KEY));
    }
  }

  @Override
  protected boolean doStateTests() {
    return false;
  }

  public static AdaptrisMessage createMessage(Object p) throws Exception {
    AdaptrisMessage m = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    try (OutputStream out = m.getOutputStream()) {
      DefaultMarshaller.getDefaultMarshaller().marshal(p, out);
    }
    return m;
  }

}
