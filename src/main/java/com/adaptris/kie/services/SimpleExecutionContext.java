package com.adaptris.kie.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.command.CommandFactory;

import com.adaptris.annotation.InputFieldHint;
import com.adaptris.core.AdaptrisMarshaller;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ExecutionContext using a configured {@link AdaptrisMarshaller}.
 *
 * <p>
 * When building the command array it does this :
 * <ul>
 * <li>Use the configured marshaller to turn the {@link AdaptrisMessage} payload into an object</li>
 * <li>Build a {@code InsertObjectCommand} using that object with the specified {@code insertId}</li>
 * <li>Build a {@code FireAllRulesCommand}</li>
 * <li>Build a {@code QueryCommand} using the specified {@code queryName}; you will also need to specify
 * {@code queryResultId, queryResultRowId} in order to traverse the {@code QueryResult} object; if these are not configured, no QueryCommand
 * is generated.</li>
 * </ul>
 * </p>
 *
 * When evalutating the results of the execution it does :
 * <ul>
 * <li>Get the QueryResults object stored against {@code queryResultId}. If no query was ever generated, then {@code insertId} is used,
 * assuming that what was inserted will have been modified</li>
 * <li>Use the configured marshaller to turn the result into its String representation</li>
 * <li>Use that as the payload</li>
 * </ul>
 *
 * @config kie-simple-execution-context
 *
 */
@XStreamAlias("kie-simple-execution-context")
public class SimpleExecutionContext implements ExecutionContext {

  @NotBlank
  @InputFieldHint(expression = true)
  private String insertId;
  @InputFieldHint(expression = true)
  private String queryResultId;
  @InputFieldHint(expression = true)
  private String queryResultRowId;
  @InputFieldHint(expression = true)
  private String queryName;
  @Valid
  private AdaptrisMarshaller marshaller;

  @Override
  public List<Command<?>> buildCommands(AdaptrisMessage msg) throws Exception {
    List<Command<?>> commands = new ArrayList<>();
    try (InputStream in = msg.getInputStream()) {
      commands.add(CommandFactory.newInsert(marshaller().unmarshal(in), msg.resolve(getInsertId())));
    }
    commands.add(CommandFactory.newFireAllRules());
    if (usesQuery()) {
      commands.add(CommandFactory.newQuery(msg.resolve(getQueryResultId()), msg.resolve(getQueryName()), (Object[]) null));
    }
    return commands;
  }

  @Override
  public void handleResults(ExecutionResults results, AdaptrisMessage msg) throws Exception {
    if (usesQuery()) {
      StreamSupport.stream(((QueryResults) results.getValue(msg.resolve(getQueryResultId()))).spliterator(), true).findFirst()
          .ifPresent(e -> {
            AdaptrisMarshaller.uncheckedMarshal(marshaller(), e.get(msg.resolve(getQueryResultRowId())), () -> {
              return msg.getOutputStream();
            });
          });
    } else {
      AdaptrisMarshaller.uncheckedMarshal(marshaller(), results.getValue(msg.resolve(getInsertId())), () -> {
        return msg.getOutputStream();
      });
    }
  }

  public AdaptrisMarshaller getMarshaller() {
    return marshaller;
  }

  public void setMarshaller(AdaptrisMarshaller marshaller) {
    this.marshaller = marshaller;
  }

  public SimpleExecutionContext withMarshaller(AdaptrisMarshaller m) {
    setMarshaller(m);
    return this;
  }

  protected AdaptrisMarshaller marshaller() {
    return DefaultMarshaller.defaultIfNull(getMarshaller());
  }

  protected boolean usesQuery() {
    // JDK8 you have a lot to answer for :(
    return BooleanUtils.and(new boolean[]
    {
        StringUtils.isNotBlank(getQueryName()), StringUtils.isNotBlank(getQueryResultId()),
        StringUtils.isNotBlank(getQueryResultRowId())
    });
  }

  public String getInsertId() {
    return insertId;
  }

  public void setInsertId(String insertId) {
    this.insertId = Args.notBlank(insertId, "insert id");
  }

  public SimpleExecutionContext withInsertId(String s) {
    setInsertId(s);
    return this;
  }

  public String getQueryResultId() {
    return queryResultId;
  }

  public void setQueryResultId(String queryResultId) {
    this.queryResultId = queryResultId;
  }

  public SimpleExecutionContext withQueryResultId(String s) {
    setQueryResultId(s);
    return this;
  }

  public String getQueryResultRowId() {
    return queryResultRowId;
  }

  public void setQueryResultRowId(String queryResultRowId) {
    this.queryResultRowId = queryResultRowId;
  }

  public SimpleExecutionContext withQueryResultRowId(String s) {
    setQueryResultRowId(s);
    return this;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }

  public SimpleExecutionContext withQueryName(String s) {
    setQueryName(s);
    return this;
  }
  
}
