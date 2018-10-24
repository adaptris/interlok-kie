package com.adaptris.kie.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.collection.CompositeCollection;
import org.kie.api.KieBase;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;

import com.adaptris.core.AdaptrisConnection;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ComponentLifecycle;
import com.adaptris.core.ConnectedService;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.core.util.LifecycleHelper;

/**
 * 
 * Abstract base class for KIE services.
 *
 */
public abstract class KieServiceImpl<S extends CommandExecutor> extends ServiceImp implements ConnectedService {

  @NotNull(message = "Connection may not be null")
  @Valid
  private AdaptrisConnection connection;

  @NotNull(message = "execution context may not be null")
  @Valid
  private ExecutionContext executionContext;

  public KieServiceImpl() {
  }

  @Override
  public void prepare() throws CoreException {
    try {
      Args.notNull(getExecutionContext(), "executionContext");
      Args.notNull(getConnection(), "connection");
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    uncheckedLifecycle(lifecycleObjects(), e -> {
      LifecycleHelper.prepare(e);
    });
  }

  @Override
  protected void initService() throws CoreException {
    uncheckedLifecycle(lifecycleObjects(), e -> {
      LifecycleHelper.init(e);
    });

  }

  public void start() throws CoreException {
    uncheckedLifecycle(lifecycleObjects(), e -> {
      LifecycleHelper.start(e);
    });

  }

  public void stop() {
    uncheckedLifecycle(lifecycleObjects(), e -> {
      LifecycleHelper.stop(e);
    });
  }


  protected void closeService() {
    uncheckedLifecycle(lifecycleObjects(), e -> {
      LifecycleHelper.close(e);
    });
  }

  // Wrapper so that we include the connection & resolver in the list of lifecycle objects.
  private Collection<ComponentLifecycle> lifecycleObjects() {
    return new CompositeCollection(Arrays.asList(connection, executionContext), wrappedLifecycleObjects());
  }
  
  /**
   * Return all the objects that need lifecycle applying to them.
   * 
   * @return all the objects that need lifecycle or an empty array.
   */
  protected abstract Collection<ComponentLifecycle> wrappedLifecycleObjects();

  @Override
  public final void doService(AdaptrisMessage msg) throws ServiceException {

    try {
      KieBase base = getConnection().retrieveConnection(KieConnection.class).getKieBase();
      List<Command<?>> commands = getExecutionContext().buildCommands(msg);
      BatchExecutionCommand myCommands = CommandFactory.newBatchExecution(commands);
      ExecutionResults results = getExecutor(base, msg).execute(myCommands);
      getExecutionContext().handleResults(results, msg);
    } catch (Exception e) {
      throw new ServiceException(e);
    }
  }

  protected abstract S getExecutor(KieBase base, AdaptrisMessage msg) throws Exception;

  public ExecutionContext getExecutionContext() {
    return executionContext;
  }

  public void setExecutionContext(ExecutionContext f) {
    executionContext = Args.notNull(f, "executionContext");
  }

  public <T extends KieServiceImpl> T withExecutionContext(ExecutionContext c) {
    setExecutionContext(c);
    return (T) this;
  }

  public AdaptrisConnection getConnection() {
    return connection;
  }

  public void setConnection(AdaptrisConnection connection) {
    this.connection = Args.notNull(connection, "connection");
  }

  public <T extends KieServiceImpl> T withConnection(AdaptrisConnection c) {
    setConnection(c);
    return (T) this;
  }

  protected static void uncheckedLifecycle(Collection<ComponentLifecycle> components, WrappedLifecycle wrapper) {
    RuleRuntimeException lastException = null;
    for (ComponentLifecycle c : components) {
      try {
        wrapper.doLifecycle(c);
      } catch (CoreException e) {
        lastException = new RuleRuntimeException(e);
      }
    }
    if (lastException != null) {
      throw lastException;
    }
  }

  /**
   * Functional interface for wrapping lifecycle operations.
   * 
   */
  @FunctionalInterface
  protected static interface WrappedLifecycle {
    public void doLifecycle(ComponentLifecycle c) throws CoreException;
  }

  private static class RuleRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 2018102301L;

    public RuleRuntimeException(Throwable cause) {
      super(cause);
    }
  }
}
