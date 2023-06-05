package com.adaptris.kie.test.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("laptop")
public class Laptop extends Computer {

  public Laptop() {
    setRam(8192);
  }

}
