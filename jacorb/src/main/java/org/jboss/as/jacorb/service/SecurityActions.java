/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.jacorb.service;

import org.wildfly.security.manager.CreateThreadAction;
import org.wildfly.security.manager.GetClassLoaderAction;
import org.wildfly.security.manager.GetContextClassLoaderAction;
import org.wildfly.security.manager.SetContextClassLoaderAction;
import org.wildfly.security.manager.WildFlySecurityManager;
import org.wildfly.security.manager.WritePropertyAction;

import static java.lang.System.setProperty;
import static java.lang.Thread.currentThread;
import static java.security.AccessController.doPrivileged;

/**
 * <p>
 * This class defines actions that must be executed in privileged blocks.
 * </p>
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
class SecurityActions {

    /**
     * <p>
     * Sets a system property with the specified key and value.
     * </p>
     *
     * @param key   the system property key.
     * @param value the system property value.
     */
    static String setSystemProperty(final String key, final String value) {
        return ! WildFlySecurityManager.isChecking() ? setProperty(key, value) : doPrivileged(new WritePropertyAction(key, value));
    }

    /**
     * <p>
     * Obtains the current thread context class loader.
     * </p>
     *
     * @return a reference to the current thread context {@code ClassLoader}.
     */
    static ClassLoader getThreadContextClassLoader() {
        return ! WildFlySecurityManager.isChecking() ? currentThread().getContextClassLoader() : doPrivileged(GetContextClassLoaderAction.getInstance());
    }

    /**
     * <p>
     * Sets the specified {@code ClassLoader} as the current thread context class loader.
     * </p>
     *
     * @param loader the {@code ClassLoader} to be set.
     */
    static void setThreadContextClassLoader(final ClassLoader loader) {
        if (! WildFlySecurityManager.isChecking()) {
            currentThread().setContextClassLoader(loader);
        } else {
            doPrivileged(new SetContextClassLoaderAction(loader));
        }
    }

    /**
     * <p>
     * Gets the {@code ClassLoader} of the specified {@code Class}.
     * </p>
     *
     * @param clazz the {@code Class} whose {@code ClassLoader} is to be returned.
     * @return the {@code ClassLoader} of the specified {@code Class} object.
     */
    static ClassLoader getClassLoader(final Class<?> clazz) {
        return ! WildFlySecurityManager.isChecking() ? clazz.getClassLoader() : doPrivileged(new GetClassLoaderAction(clazz));
    }

    /**
     * <p>
     * Creates a thread with the specified {@code Runnable} and name.
     * </p>
     *
     * @param runnable   the {@code Runnable} to be set in the new {@code Thread}.
     * @param threadName the name of the new {@code Thread}.
     * @return the construct {@code Thread} instance.
     */
    static Thread createThread(final Runnable runnable, final String threadName) {
        return ! WildFlySecurityManager.isChecking() ? new Thread(runnable, threadName) : doPrivileged(new CreateThreadAction(runnable, threadName));
    }
}
