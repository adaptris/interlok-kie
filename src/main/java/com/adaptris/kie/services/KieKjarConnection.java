package com.adaptris.kie.services;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.BooleanUtils;
import org.kie.api.KieBase;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.TimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Connection that builds up a KieContainer based on {@code KieServices#newReleaseId(String, String, String)}.
 *
 * <p>
 * If you have a custom repository then you may need to override maven behaviour with a custom settings.xml using the system property
 * {@code kie.maven.settings.custom}. If you want to use a {@code KieScanner} then you will need to have the {@code kie-ci} artifact as
 * well.
 * </p>
 *
 * @config kie-kjar-connection
 */
@XStreamAlias("kie-kjar-connection")
@ComponentProfile(summary = "Drools connection that builds up a KieContainer based on KieService#newReleaseId()", since = "3.8.2")
public class KieKjarConnection extends KieConnection {

  private static final TimeInterval RESCAN_INTERVAL = new TimeInterval(10L, TimeUnit.MINUTES);

  @NotBlank
  private String groupId;
  @NotBlank
  private String artifactId;
  @NotBlank
  private String version;
  @NotBlank
  private String kieBaseName;

  @AdvancedConfig
  @InputFieldDefault(value = "false")
  private Boolean rescan;
  @InputFieldDefault(value = "10 minutes")
  @AdvancedConfig
  private TimeInterval rescanInterval;

  public KieKjarConnection() {
  }

  @Override
  protected void prepareConnection() throws CoreException {
    super.prepareConnection();
    try {
      Args.notNull(getGroupId(), "group");
      Args.notNull(getArtifactId(), "artifact");
      Args.notNull(getVersion(), "version");
      Args.notNull(getKieBaseName(), "kieBaseName");
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
  }

  @Override
  protected KieBase buildKieBase() throws Exception {
    ReleaseId releaseId = services.newReleaseId(getGroupId(), getArtifactId(), getVersion());
    KieContainer container = services.newKieContainer(releaseId);
    if (rescan()) {
      KieScanner scanner = services.newKieScanner(container);
      scanner.start(rescanIntervalMillis());
    }
    return container.getKieBase(getKieBaseName());
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public KieKjarConnection withReleaseId(String groupId, String artifactId, String version) {
    setGroupId(groupId);
    setArtifactId(artifactId);
    setVersion(version);
    return this;
  }

  public Boolean getRescan() {
    return rescan;
  }

  /**
   * Whether or not to create a {@code KieScanner} to monitor your maven repository for updated Kie projects.
   *
   * <p>
   * Note that even though you enable a {@code KieScanner} there's no guarantee that it will work; {@code KieScanner} will only pickup
   * changes to deployed jars if it is using a SNAPSHOT, version range, the LATEST, or the RELEASE setting. Fixed versions will not
   * automatically update at runtime.
   * </p>
   *
   * @param b
   */
  public void setRescan(Boolean b) {
    rescan = b;
  }

  protected boolean rescan() {
    return BooleanUtils.toBooleanDefaultIfNull(getRescan(), false);
  }

  public TimeInterval getRescanInterval() {
    return rescanInterval;
  }

  /**
   * Set the rescan interval if {@link #setRescan(Boolean)} is true.
   *
   * @param i
   */
  public void setRescanInterval(TimeInterval i) {
    rescanInterval = i;
  }

  protected long rescanIntervalMillis() {
    return getRescanInterval() != null ? getRescanInterval().toMilliseconds() : RESCAN_INTERVAL.toMilliseconds();
  }

  public KieKjarConnection withRescan(Boolean b) {
    setRescan(b);
    return this;
  }

  public KieKjarConnection withRescan(Boolean b, TimeInterval t) {
    return withRescan(b).withRescanInterval(t);
  }

  public KieKjarConnection withRescanInterval(TimeInterval b) {
    setRescanInterval(b);
    return this;
  }

  public String getKieBaseName() {
    return kieBaseName;
  }

  /**
   * Specify the name of the {@code KieBase} within the {@code KieContainer} that you wish to use.
   *
   * @param s
   */
  public void setKieBaseName(String s) {
    kieBaseName = s;
  }

  public KieKjarConnection withKieBaseName(String s) {
    setKieBaseName(s);
    return this;
  }

}
