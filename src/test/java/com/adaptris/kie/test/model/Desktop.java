package com.adaptris.kie.test.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("desktop")
public class Desktop extends Computer {

  public Desktop() {
    setRam(65536);
  }
}
