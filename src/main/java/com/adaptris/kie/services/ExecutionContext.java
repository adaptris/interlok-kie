package com.adaptris.kie.services;

import java.util.List;

import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ComponentLifecycle;
import com.adaptris.core.ComponentLifecycleExtension;
import com.adaptris.core.CoreException;

/**
 * How AdaptrisMessage objects are inserted and executed within the Drools environment.
 * 
 */
public interface ExecutionContext extends ComponentLifecycle, ComponentLifecycleExtension {

  /**
   * Build the list of commands to execute within the Kie environment.
   * 
   */
  List<Command<?>> buildCommands(AdaptrisMessage msg) throws Exception;

  /** Apply the results of the exeuction to the message.
   * 
   */
  void handleResults(ExecutionResults result, AdaptrisMessage msg) throws Exception;

  /**
   * @implSpec The default implementation is a no-op method and is provided for convienence
   */
  @Override
  default void close() {
  }

  /**
   * @implSpec The default implementation is a no-op method and is provided for convienence
   */
  @Override
  default void init() throws CoreException {
  }

  /**
   * @implSpec The default implementation is a no-op method and is provided for convienence
   */
  @Override
  default void start() throws CoreException {
  }

  /**
   * @implSpec The default implementation is a no-op method and is provided for convienence
   */
  @Override
  default void stop() {
  }

  /**
   * @implSpec The default implementation is a no-op method and is provided for convienence
   */
  @Override
  default void prepare() throws CoreException {
  }
}
