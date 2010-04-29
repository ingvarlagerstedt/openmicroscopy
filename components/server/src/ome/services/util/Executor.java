/*
 *   $Id$
 *
 *   Copyright 2008 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.services.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import ome.conditions.InternalException;
import ome.security.SecuritySystem;
import ome.security.basic.CurrentDetails;
import ome.system.EventContext;
import ome.system.OmeroContext;
import ome.system.Principal;
import ome.system.ServiceFactory;
import ome.tools.spring.InternalServiceFactory;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Simple execution/work interface which can be used for <em>internal</em> tasks
 * which need to have a full working implementation. The
 * {@link Executor#execute(Principal, ome.services.util.Executor.Work)} method
 * ensures that {@link SecuritySystem#login(Principal)} is called before the
 * task, that a {@link TransactionCallback} and a {@link HibernateCallback}
 * surround the call, and that subsequently {@link SecuritySystem#logout()} is
 * called.
 * 
 * @author Josh Moore, josh at glencoesoftware.com
 * @since 3.0-Beta3
 */
public interface Executor extends ApplicationContextAware {

    /**
     * Provides access to the context for Work-API consumers who need to publish
     * events, etc.
     */
    public OmeroContext getContext();

    /**
     * Returns a {@link Principal} representing your current session or null,
     * if none is active.
     */
    public Principal principal();

    /**
     * Executes a {@link Work} instance wrapped in two layers of AOP. The first
     * is intended to acquire the proper arguments for
     * {@link Work#doWork(Session, ServiceFactory)} from the
     * {@link OmeroContext}, and the second performs all the standard service
     * actions for any normal method call.
     * 
     * If the {@link Principal} argument is not null, then additionally, a
     * login/logout sequence will be performed in a try/finally block.
     * 
     * {@link Work} implementation must be annotated with {@link Transactional}
     * in order to properly specify isolation, read-only status, etc.
     * 
     * @param p
     *            Possibly null.
     * @param work
     *            Not null.
     */
    public Object execute(final Principal p, final Work work);

    /**
     * Simple submission method which can be used in conjunction with a call to
     * {@link #execute(Principal, Work)} to overcome the no-multiple-login rule.
     */
    public <T> Future<T> submit(final Callable<T> callable);

    /**
     * Helper method to perform {@link Future#get()} and properly unwrap the
     * exceptions. Any {@link RuntimeException} which was thrown during
     * execution will be rethrown. All other exceptions will be wrapped in an
     * {@link InternalException}.
     */
    public <T> T get(final Future<T> future);

    /**
     * Executes a {@link StatelessWork} wrapped with a transaction. Since
     * {@link StatelessSession} does not return proxies, there is less concern
     * about returned values, but this method <em>completely</em> overrides
     * OMERO security, and should be used <b>very</em> carefully. *
     * 
     * As with {@link #execute(Principal, Work)} the {@link StatelessWork}
     * instance must be properly marked with an {@link Transactional}
     * annotation.
     * 
     * @param work
     *            Non-null.
     * @return
     */
    public Object executeStateless(final StatelessWork work);

    /**
     * Work SPI to perform actions within the server as if they were fully
     * wrapped in our service logic. Note: any results which are coming from
     * Hibernate <em>may <b>not</b></em> be assigned directly to a field, rather
     * must be returned as an {@link Object} so that Hibernate proxies can be
     * properly handled.
     */
    public interface Work {

        /**
         * Returns a description of what this work will be doing for logging
         * purposes.
         */
        String description();

        /**
         * Work method. Must return all results coming from Hibernate via the
         * {@link Object} return method.
         * 
         * @param status
         *            non null.
         * @param session
         *            non null.
         * @param sf
         *            non null.
         * @return Any results which will be used by non-wrapped code.
         */
        Object doWork(Session session, ServiceFactory sf);

    }

    /**
     * Work SPI to perform actions related to
     * {@link org.hibernate.SessionFactory#openStatelessSession() stateless}
     * sessions. This overrides <em>ALL</em> security in the server and should
     * only be used as a last resort. Currently accept locations are:
     * <ul>
     * <li>In the {@link ome.services.sessions.SessionManager} to boot strap a
     * {@link ome.model.meta.Session session}
     * <li>In the {@link ome.security.basic.EventHandler} to save
     * {@link ome.model.meta.EventLog event logs}
     * </ul>
     * 
     * Before the JTA fixes of 4.0, this interface provided a
     * {@link org.hibernate.StatelessSession. However, as mentioned in
     * http://jira.springframework.org/browse/SPR-2495, that interface is not
     * currently supported in Spring's transaction management.
     */
    public interface StatelessWork {

        /**
         * Return a description of what this work will be doing for logging
         * purposes.
         */
        String description();

        Object doWork(SimpleJdbcOperations jdbc);
    }

    /**
     * Simple adapter which takes a String for {@link #description}
     */
    public abstract class SimpleWork implements Work {

        final private String description;

        public SimpleWork(Object o, String method, Object...params) {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName());
            sb.append(".");
            sb.append(method);
            boolean first = true;
            if (params.length > 0) {
                sb.append("(");
                for (Object object : params) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(object);
                }
                sb.append(")");
            }
            this.description = sb.toString();
        }

        public String description() {
            return description;
        }

    }

    /**
     * Simple adapter which takes a String for {@link #description}
     */
    public abstract class SimpleStatelessWork implements StatelessWork {

        final private String description;

        public SimpleStatelessWork(Object o, String method) {
            this.description = o.getClass().getName() + "." + method;
        }

        public String description() {
            return description;
        }

    }

    public class Impl implements Executor {

        private final static Log log = LogFactory.getLog(Executor.class);

        protected OmeroContext context;
        final protected List<Advice> advices = new ArrayList<Advice>();
        final protected CurrentDetails principalHolder;
        final protected String[] proxyNames;
        final protected SessionFactory factory;
        final protected SimpleJdbcOperations jdbcOps;
        final protected ExecutorService service;

        public Impl(CurrentDetails principalHolder, SessionFactory factory,
                SimpleJdbcOperations jdbc, String[] proxyNames) {
            this(principalHolder, factory, jdbc, proxyNames,
                    java.util.concurrent.Executors.newCachedThreadPool());
        }

        public Impl(CurrentDetails principalHolder, SessionFactory factory,
                SimpleJdbcOperations jdbc, String[] proxyNames,
                ExecutorService service) {
            this.jdbcOps = jdbc;
            this.factory = factory;
            this.principalHolder = principalHolder;
            this.proxyNames = proxyNames;
            this.service = service;
        }

        public void setApplicationContext(ApplicationContext applicationContext)
                throws BeansException {
            this.context = (OmeroContext) applicationContext;
            for (String name : proxyNames) {
                advices.add((Advice) this.context.getBean(name));
            }
        }

        public OmeroContext getContext() {
            return this.context;
        }

        public Principal principal() {
            if (principalHolder.size() == 0) {
                return null;
            } else {
                EventContext ec = principalHolder.getCurrentEventContext();
                String session = ec.getCurrentSessionUuid();
                return new Principal(session);
            }
        }
        
        /**
         * Executes a {@link Work} instance wrapped in two layers of AOP. The
         * first is intended to acquire the proper arguments for
         * {@link Work#doWork(TransactionStatus, Session, ServiceFactory)} for
         * the {@link OmeroContext}, and the second performs all the standard
         * service actions for any normal method call.
         * 
         * If the {@link Principal} argument is not null, then additionally, a
         * login/logout sequence will be performed in a try/finally block.
         * 
         * @param p
         * @param work
         */
        public Object execute(final Principal p, final Work work) {
            Interceptor i = new Interceptor(factory);
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(work);
            factory.setInterfaces(new Class[] { Work.class });

            for (Advice advice : advices) {
                factory.addAdvice(advice);
            }
            factory.addAdvice(i);

            Work wrapper = (Work) factory.getProxy();

            // First we guarantee that this will cause one and only
            // login to take place.
            if (p == null && principalHolder.size() == 0) {
                throw new IllegalStateException("Must provide principal");
            } else if (p != null && principalHolder.size() > 0) {
                throw new IllegalStateException(
                        "Already logged in. Use Executor.submit() and .get().");
            }

            // Don't need to worry about the login stack below since
            // already checked.
            if (p != null) {
                this.principalHolder.login(p);
            }

            try {
                // Arguments will be replaced after hibernate is in effect
                return wrapper.doWork(null, new InternalServiceFactory(
                        this.context));
            } finally {
                if (p != null) {
                    this.principalHolder.logout();
                }
            }
        }

        public <T> Future<T> submit(final Callable<T> callable) {
            return service.submit(callable);
        }

        public <T> T get(final Future<T> future) {
            try {
                return future.get();
            } catch (InterruptedException e1) {
                throw new InternalException("Future.get interrupted:"
                        + e1.getMessage());
            } catch (ExecutionException e1) {
                if (e1.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e1.getCause();
                } else {
                    throw new InternalException(
                            "Caught exception thrown by Future.get:"
                                    + e1.getMessage());
                }
            }
        }

        /**
         * Executes a {@link StatelessWork} in transaction.
         * 
         * @param work
         *            Non-null.
         * @return
         */
        public Object executeStateless(final StatelessWork work) {

            if (principalHolder.size() > 0) {
                throw new IllegalStateException(
                        "Currently logged in. \n"
                                + "JDBC will then take part in transaction directly. \n"
                                + "Please have the proper JDBC or data source injected.");
            }

            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(work);
            factory.setInterfaces(new Class[] { StatelessWork.class });
            factory.addAdvice(advices.get(2)); // TX FIXME
            StatelessWork wrapper = (StatelessWork) factory.getProxy();
            return wrapper.doWork(this.jdbcOps);
        }

        /**
         * Interceptor class which properly lookups and injects the session
         * objects in the
         * {@link Work#doWork(TransactionStatus, Session, ServiceFactory)}
         * method.
         */
        static class Interceptor implements MethodInterceptor {
            private final SessionFactory factory;

            public Interceptor(SessionFactory sf) {
                this.factory = sf;
            }

            public Object invoke(final MethodInvocation mi) throws Throwable {
                final Object[] args = mi.getArguments();
                args[0] = SessionFactoryUtils.getSession(factory, false);
                return mi.proceed();
            }
        }

    }
}