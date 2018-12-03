package com.adaptris.kie.services;

import com.adaptris.kie.services.KieStatelessService;

public class StatelessSessionServiceTest extends
    KieConnectionWithRulesCase {

  @Override
  protected KieStatelessService createForTests() {
    return new KieStatelessService();
  }

}
