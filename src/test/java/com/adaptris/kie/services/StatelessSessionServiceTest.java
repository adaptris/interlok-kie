package com.adaptris.kie.services;

public class StatelessSessionServiceTest extends KieConnectionWithRulesCase {

  @Override
  protected KieStatelessService createForTests() {
    return new KieStatelessService();
  }

}
