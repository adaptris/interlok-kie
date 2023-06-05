package com.adaptris.kie.services;

import javax.validation.constraints.NotBlank;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Session management strategy that retains the same session until a certain metadata key is set on the message.
 * <p>
 * The {@code KieSession} is created upon the first invocation of {@link #get(KieBase, AdaptrisMessage)} and is disposed when the
 * AdaptrisMessage contains the metadata key specified by {@link #getMetadataKey()} (the value doesn't matter).
 * </p>
 *
 * @config kie-metadata-controlled-session
 *
 */
@XStreamAlias("kie-metadata-controlled-session")
public class MetadataSession extends SessionManagementImpl {

  /**
   * The default metadata key; value is {@value #DEFAULT_METADATA_KEY}.
   *
   */
  public static final String DEFAULT_METADATA_KEY = "kie.session.dispose";

  @NotBlank(message = "metadataKey may not be null")
  @AutoPopulated
  @InputFieldDefault(value = DEFAULT_METADATA_KEY)
  private String metadataKey;

  public MetadataSession() {
    setMetadataKey(DEFAULT_METADATA_KEY);
  }

  public MetadataSession(String s) {
    this();
    setMetadataKey(s);
  }

  @Override
  public synchronized KieSession get(KieBase b, AdaptrisMessage msg) {
    if (session == null) {
      session = b.newKieSession();
    } else if (msg.headersContainsKey(getMetadataKey())) {
      SessionManagement.disposeQuietly(session);
      session = b.newKieSession();
    }
    return session;
  }

  /**
   * @return the metadataKey
   */
  public String getMetadataKey() {
    return metadataKey;
  }

  /**
   * Set the metadata key which will force disposal of the session.
   *
   * @param s
   *          the metadataKey to set, if not explicitly specified defaults to {@value #DEFAULT_METADATA_KEY}
   */
  public void setMetadataKey(String s) {
    metadataKey = Args.notBlank(s, "metadataKey");
  }

}
