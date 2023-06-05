package com.adaptris.kie.services;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.kie.api.KieBase;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.core.CoreException;
import com.adaptris.core.fs.FsHelper;
import com.adaptris.core.util.Args;
import com.adaptris.util.URLHelper;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Connection that builds up the {@code KieContainer} programatically rather than using a {@code kjar+releaseId}.
 *
 * @config kie-connection-with-rules
 */
@XStreamAlias("kie-connection-with-rules")
@ComponentProfile(summary = "Drools connection that builds up a KieContainer iteratively, adding the specified rules", since = "3.8.2")
public class KieConnectionWithRules extends KieConnection {
  private static final String KIE_FS_PREFIX = "src/main/resources/";

  @XStreamImplicit(itemFieldName = "rule")
  @AutoPopulated
  @NotNull
  @Valid
  private List<String> rules;

  public KieConnectionWithRules() {
    setRules(new ArrayList<>());
  }

  // KieHelper doesn't assign good names for the resources if they're input streams, so we have to do the
  // *proper way*
  // private KieBase buildKieBaseViaHelper() throws Exception {
  // KieHelper kieHelper = new KieHelper();
  // for (String rule : getRules()) {
  // kieHelper.addResource(ResourceFactory.newInputStreamResource(URLHelper.connect(rule)), ResourceType.DRL);
  // }
  // Results buildResults = kieHelper.verify();
  // validateBuildResults(buildResults);
  // KieBase base = kieHelper.build();
  // return base;
  // }

  @Override
  protected KieBase buildKieBase() throws Exception {
    KieFileSystem fs = services.newKieFileSystem();
    for (String rule : getRules()) {
      String name = Paths.get(FsHelper.createUrlFromString(rule, true).toURI()).getFileName().toString();
      fs.write(KIE_FS_PREFIX + name, ResourceFactory.newInputStreamResource(URLHelper.connect(rule)));
    }
    validateBuildResults(services.newKieBuilder(fs).buildAll().getResults());
    KieRepository repository = services.getRepository();
    KieContainer container = services.newKieContainer(repository.getDefaultReleaseId());
    KieBase base = container.getKieBase();
    return base;
  }

  protected void validateBuildResults(Results buildResults) throws CoreException {
    if (buildResults.hasMessages(Level.ERROR)) {
      throw new CoreException(buildResults.getMessages(Level.ERROR).toString());
    }
  }

  public List<String> getRules() {
    return rules;
  }

  public void setRules(List<String> rules) {
    this.rules = Args.notNull(rules, "rules");
  }

  public KieConnection withRules(String... rules) {
    setRules(new ArrayList<>(Arrays.asList(rules)));
    return this;
  }

}
