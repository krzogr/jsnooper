/*
 * Copyright (C) 2025 krzogr (krzogr@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.krzogr.jsnooper.tracking;

import java.util.concurrent.atomic.AtomicBoolean;

public class TrackingControl {
    private final AtomicBoolean globalFlag = new AtomicBoolean(false);
    private final DisabledThreadScope disabledScope = new DisabledThreadScope();

    private boolean pendingThreadLocalScope = false;
    private final ThreadLocal<TrackingScope> threadLocalScope = ThreadLocal.withInitial(DefaultThreadScope::new);

    public boolean isTrackingEnabled() {
        return globalFlag.get();
    }

    public void disableTracking() {
        globalFlag.set(false);
    }

    public void enableTracking() {
        globalFlag.set(true);
    }

    public TrackingScope getTrackingScope() {
        if (!globalFlag.get()) {
            return disabledScope;
        }

        return getThreadLocalScope();
    }

    private synchronized TrackingScope getThreadLocalScope() {
        if (pendingThreadLocalScope) {
            return disabledScope;
        }

        pendingThreadLocalScope = true;
        try {
            return threadLocalScope.get();
        } finally {
            pendingThreadLocalScope = false;
        }
    }

    private static final class DisabledThreadScope implements TrackingScope {
        @Override
        public boolean isTrackingEnabled() {
            return false;
        }

        @Override
        public void enableTracking() {
        }

        @Override
        public void disableTracking() {
        }

        @Override
        public StringBuilder getBuffer() {
            return null;
        }
    }

    private static final class DefaultThreadScope implements TrackingScope {
        private boolean threadLocalFlag = true;
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public boolean isTrackingEnabled() {
            return threadLocalFlag;
        }

        @Override
        public void enableTracking() {
            threadLocalFlag = true;
        }

        @Override
        public void disableTracking() {
            threadLocalFlag = false;
        }

        @Override
        public StringBuilder getBuffer() {
            return buffer;
        }
    }
}
