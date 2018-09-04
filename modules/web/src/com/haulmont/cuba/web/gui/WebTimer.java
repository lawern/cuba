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
package com.haulmont.cuba.web.gui;

import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.core.global.RemoteException;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.security.global.NoUserSessionException;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.cuba.web.widgets.CubaTimer;
import com.vaadin.ui.Label;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class WebTimer extends WebAbstractComponent<Label> implements com.haulmont.cuba.gui.components.Timer {

    private static final Logger log = LoggerFactory.getLogger(CubaTimer.class);

    protected CubaTimer timerImpl;

    public WebTimer() {
        component = new Label();
        timerImpl = new CubaTimer();
        timerImpl.setExceptionHandler(e -> {
            int reIdx = ExceptionUtils.indexOfType(e, RemoteException.class);
            if (reIdx > -1) {
                RemoteException re = (RemoteException) ExceptionUtils.getThrowableList(e).get(reIdx);
                for (RemoteException.Cause cause : re.getCauses()) {
                    //noinspection ThrowableResultOfMethodCallIgnored
                    if (cause.getThrowable() instanceof NoUserSessionException) {
                        log.warn("NoUserSessionException in timer {}, timer will be stopped",
                                timerImpl.getLoggingTimerId());
                        stop();
                        break;
                    }
                }
            } else if (ExceptionUtils.indexOfThrowable(e, NoUserSessionException.class) > -1) {
                log.warn("NoUserSessionException in timer {}, timer will be stopped", timerImpl.getLoggingTimerId());
                stop();
            }

            throw new RuntimeException("Exception in timer", e);
        });
    }

    @Override
    public void start() {
        timerImpl.start();
    }

    @Override
    public void stop() {
        timerImpl.stop();
    }

    @Override
    public boolean isRepeating() {
        return timerImpl.isRepeating();
    }

    @Override
    public void setRepeating(boolean repeating) {
        timerImpl.setRepeating(repeating);
    }

    @Override
    public int getDelay() {
        return timerImpl.getDelay();
    }

    @Override
    public void setDelay(int delay) {
        timerImpl.setDelay(delay);
    }

    @Override
    public Subscription addTimerActionListener(Consumer<TimerActionEvent> listener) {
        timerImpl.addActionListener(new CubaTimerActionListenerWrapper(listener));

        return Timer.super.addTimerActionListener(listener);
    }

    @Override
    public void removeTimerActionListener(Consumer<TimerActionEvent> listener) {
        timerImpl.removeActionListener(new CubaTimerActionListenerWrapper(listener));

        Timer.super.removeTimerActionListener(listener);
    }

    @Override
    public Subscription addTimerStopListener(Consumer<TimerStopEvent> listener) {
        timerImpl.addStopListener(new CubaTimerStopListenerWrapper(listener));

        return Timer.super.addTimerStopListener(listener);
    }

    @Override
    public void removeTimerStopListener(Consumer<TimerStopEvent> listener) {
        timerImpl.removeStopListeners(new CubaTimerStopListenerWrapper(listener));

        Timer.super.removeTimerStopListener(listener);
    }

    @Override
    public void setId(String id) {
        super.setId(id);

        timerImpl.setTimerId(id);
    }

    public CubaTimer getTimerImpl() {
        return timerImpl;
    }

    protected class CubaTimerActionListenerWrapper implements CubaTimer.ActionListener {

        private final Consumer<TimerActionEvent> listener;

        public CubaTimerActionListenerWrapper(Consumer<TimerActionEvent> listener) {
            this.listener = listener;
        }

        @Override
        public void timerAction(CubaTimer timer) {
            listener.accept(new TimerActionEvent(WebTimer.this));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }

            CubaTimerActionListenerWrapper that = (CubaTimerActionListenerWrapper) obj;

            return this.listener.equals(that.listener);
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }
    }

    protected class CubaTimerStopListenerWrapper implements CubaTimer.StopListener {

        private final Consumer<TimerStopEvent> listener;

        public CubaTimerStopListenerWrapper(Consumer<TimerStopEvent> listener) {
            this.listener = listener;
        }

        @Override
        public void timerStopped(CubaTimer timer) {
            listener.accept(new TimerStopEvent(WebTimer.this));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }

            CubaTimerStopListenerWrapper that = (CubaTimerStopListenerWrapper) obj;

            return this.listener.equals(that.listener);
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }
    }
}