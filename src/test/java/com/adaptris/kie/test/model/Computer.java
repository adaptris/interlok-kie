package com.adaptris.kie.test.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("computer")
public class Computer {

  public enum OperatingSystem {
    Mac, Linux, Windows
  };

  private OperatingSystem operatingSystem;

  private int ram;

  public OperatingSystem getOperatingSystem() {
    return operatingSystem;
  }

  public void setOperatingSystem(OperatingSystem os) {
    this.operatingSystem = os;
  }

  public int getRam() {
    return ram;
  }

  public void setRam(int ram) {
    this.ram = ram;
  }

}
