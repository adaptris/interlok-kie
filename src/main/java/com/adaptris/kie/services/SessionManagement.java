package com.adaptris.kie.services;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ComponentLifecycle;
import com.adaptris.core.ComponentLifecycleExtension;
import com.adaptris.core.CoreException;

/**
 * Strategy for managing {@code KieSession} instances.
 * 
 */
public interface SessionManagement extends ComponentLifecycle, ComponentLifecycleExtension {

  /**
   * Get an existing or a new {@code KieSession}.
   * 
   */
  KieSession get(KieBase kieBase, AdaptrisMessage msg) throws Exception;

  @Override
  default void close() {
  }

  @Override
  default void init() throws CoreException {
  }

  @Override
  default void start() throws CoreException {
  }

  @Override
  default void stop() {
  }

  @Override
  default void prepare() throws CoreException {
  }

  static void disposeQuietly(KieSession session) {
    if (session != null) {
      session.dispose();
    }
  }
}
