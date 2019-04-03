/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.haulmont.cuba.web;

import com.google.common.base.Strings;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.app.loginwindow.AppLoginWindow;
import com.haulmont.cuba.web.security.AnonymousUserCredentials;
import com.haulmont.cuba.web.security.events.AppLoggedInEvent;
import com.haulmont.cuba.web.security.events.AppLoggedOutEvent;
import com.haulmont.cuba.web.security.events.AppStartedEvent;
import com.haulmont.cuba.web.sys.VaadinSessionScope;
import com.vaadin.server.*;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.haulmont.cuba.web.Connection.StateChangeEvent;
import static com.haulmont.cuba.web.Connection.UserSubstitutedEvent;
import static com.haulmont.cuba.web.security.ExternalUserCredentials.isLoggedInWithExternalAuth;

/**
 * Default {@link App} implementation that shows {@link AppLoginWindow} on start. Single instance of App is bound to
 * single HTTP session.
 */
@Component(App.NAME)
@Scope(VaadinSessionScope.NAME)
public class DefaultApp extends App {

    private static final Logger log = LoggerFactory.getLogger(DefaultApp.class);

    public DefaultApp() {
    }

    @Override
    protected Connection createConnection() {
        Connection connection = super.createConnection();
        connection.addStateChangeListener(this::connectionStateChanged);
        return connection;
    }

    protected void connectionStateChanged(StateChangeEvent event) {
        Connection connection = event.getSource();

        log.debug("connectionStateChanged connected: {}, authenticated: {}",
                connection.isConnected(), connection.isAuthenticated());

        cleanupBackgroundTasks();
        removeAllWindows();
        clearSettingsCache();

        if (connection.isConnected()) {
            UserSession userSession = connection.getSessionNN();
            setLocale(userSession.getLocale());

            // substitution listeners are cleared by connection on logout
            connection.addUserSubstitutionListener(this::userSubstituted);

            preventSessionFixation(connection, userSession);

            initExceptionHandlers(true);

            AppUI currentUi = AppUI.getCurrent();
            if (currentUi != null) {
                UserSession oldUserSession = currentUi.getCurrentSession();

                currentUi.setCurrentSession(connection.getSession());

                // "logout" all UIs with the same UserSession
                getAppUIs()
                        .stream()
                        .filter(AppUI::hasAuthenticatedSession)
                        .filter(ui -> Objects.equals(ui.getCurrentSession(), oldUserSession))
                        .collect(Collectors.toList())
                        .forEach(ui -> ui.setCurrentSession(userSession));
            }

            if (connection.isAuthenticated()) {
                // notify all UIs bound with different sessions
                getAppUIs()
                        .stream()
                        .filter(ui -> ui.hasAuthenticatedSession()
                                && !Objects.equals(ui.getCurrentSession(), userSession))
                        .forEach(ui -> {
                            Messages messages = beanLocator.get(Messages.class);

                            String sessionChangedCaption = messages.getMainMessage("sessionChangedCaption");
                            String sessionChanged = messages.getMainMessage("sessionChanged");

                            Notification notification = new Notification(sessionChangedCaption, sessionChanged,
                                    Notification.Type.WARNING_MESSAGE);
                            notification.setDelayMsec(-1);
                            notification.show(ui.getPage());
                        });
            }

            initializeUi();

            if (linkHandler != null && linkHandler.canHandleLink()) {
                linkHandler.handle();
                linkHandler = null;
            }

            if (redirectHandler != null && redirectHandler.scheduled()) {
                redirectHandler.redirect();
            }

            publishAppLoggedInEvent();
        } else {
            initExceptionHandlers(false);

            VaadinRequest currentRequest = VaadinService.getCurrentRequest();
            if (currentRequest != null) {
                Locale requestLocale = currentRequest.getLocale();
                setLocale(resolveLocale(requestLocale));
            }

            try {
                connection.login(new AnonymousUserCredentials(getLocale()));
            } catch (LoginException e) {
                throw new RuntimeException("Unable to login as anonymous!", e);
            }

            publishAppLoggedOutEvent(event.getPreviousSession());
        }
    }

    protected void userSubstituted(UserSubstitutedEvent event) {
        cleanupBackgroundTasks();
        clearSettingsCache();
        removeAllWindows();

        initializeUi();
    }

    protected void publishAppLoggedOutEvent(UserSession previousSession) {
        AppLoggedOutEvent event = new AppLoggedOutEvent(this, previousSession);
        events.publish(event);

        String loggedOutUrl = event.getRedirectUrl();
        if (loggedOutUrl != null) {
            redirectAfterLogout(loggedOutUrl);
        }
    }

    protected void redirectAfterLogout(String loggedOutUrl) {
        if (!Strings.isNullOrEmpty(loggedOutUrl)) {
            AppUI currentUi = AppUI.getCurrent();
            // it can be null if we handle request in a custom RequestHandler
            if (currentUi != null) {
                currentUi.setContent(null);
                currentUi.getPage().setLocation(loggedOutUrl);
            } else {
                VaadinResponse response = VaadinService.getCurrentResponse();
                try {
                    ((VaadinServletResponse) response).getHttpServletResponse().
                            sendRedirect(loggedOutUrl);
                } catch (IOException e) {
                    log.error("Error on send redirect to client", e);
                }
            }

            VaadinSession vaadinSession = VaadinSession.getCurrent();
            for (UI ui : vaadinSession.getUIs()) {
                if (ui != currentUi) {
                    ui.access(() -> {
                        ui.setContent(null);
                        ui.getPage().setLocation(loggedOutUrl);
                    });
                }
            }
        }
    }

    protected void publishAppLoggedInEvent() {
        AppLoggedInEvent event = new AppLoggedInEvent(this);
        events.publish(event);
    }

    protected void initializeUi() {
        AppUI currentUi = AppUI.getCurrent();
        if (currentUi != null) {
            createTopLevelWindow(currentUi);
        }

        getAppUIs()
                .stream()
                .filter(ui -> Objects.equals(userSessionSource.getUserSession(), ui.getCurrentSession()))
                .collect(Collectors.toList())
                .forEach(ui -> {
                    if (currentUi != ui) {
                        ui.accessSynchronously(() ->
                                createTopLevelWindow(ui)
                        );
                    }
                });
    }

    protected void preventSessionFixation(Connection connection, UserSession userSession) {
        if (connection.isAuthenticated()
                && !isLoggedInWithExternalAuth(userSession)
                && webConfig.getUseSessionFixationProtection()
                && VaadinService.getCurrentRequest() != null) {

            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());

            WrappedSession session = VaadinSession.getCurrent().getSession();
            int timeout = webConfig.getHttpSessionExpirationTimeoutSec();
            session.setMaxInactiveInterval(timeout);

            HttpSession httpSession = session instanceof WrappedHttpSession ?
                    ((WrappedHttpSession) session).getHttpSession() : null;
            log.debug("Session reinitialized: HttpSession={}, timeout={}sec, UserSession={}",
                    httpSession, timeout, connection.getSession());
        }
    }

    @Override
    protected String routeTopLevelWindowId() {
        if (connection.isAuthenticated()) {
            return webConfig.getMainScreenId();
        } else {
            return webConfig.getLoginScreenId();
        }
    }

    @Override
    public void loginOnStart() throws LoginException {
        publishAppStartedEvent();

        if (!connection.isConnected()) {
            try {
                connection.login(new AnonymousUserCredentials(getLocale()));
            } catch (LoginException e) {
                throw new RuntimeException("Unable to login as anonymous!", e);
            }
        }
    }

    protected void publishAppStartedEvent() throws LoginException {
        try {
            events.publish(new AppStartedEvent(this));
        } catch (UndeclaredThrowableException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof LoginException) {
                throw (LoginException) cause;
            } else {
                throw ex;
            }
        }
    }

    /**
     * @deprecated Use {@link Screens#create(Class, Screens.LaunchMode)} with {@link OpenMode#ROOT}
     */
    @Deprecated
    @Override
    public void navigateTo(String topLevelWindowId) {
        cleanupBackgroundTasks();
        clearSettingsCache();
        removeAllWindows();

        super.navigateTo(topLevelWindowId);
    }
}