package com.adaptris.kie.services;

import com.adaptris.kie.services.KieService;

public class StatefulSessionServiceTest extends
    KieConnectionWithRulesCase {

  @Override
  protected KieService createForTests() {
    return new KieService();
  }

}
