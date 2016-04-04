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

package com.haulmont.cuba.gui.components;

import java.util.Set;

/**
 */
public interface UploadComponentSupport extends Component, Component.HasCaption, Component.BelongToFrame, Component.HasIcon {

    abstract class FileUploadEvent {
        private final String fileName;
        private final long contentLength;

        protected FileUploadEvent(String fileName, long contentLength) {
            this.fileName = fileName;
            this.contentLength = contentLength;
        }

        public long getContentLength() {
            return contentLength;
        }

        public String getFileName() {
            return fileName;
        }
    }

    class FileUploadStartEvent extends FileUploadEvent {
        public FileUploadStartEvent(String fileName, long contentLength) {
            super(fileName, contentLength);
        }
    }

    interface FileUploadStartListener {
        void fileUploadStart(FileUploadStartEvent e);
    }

    class FileUploadFinishEvent extends FileUploadEvent {
        public FileUploadFinishEvent(String fileName, long contentLength) {
            super(fileName, contentLength);
        }
    }

    interface FileUploadFinishListener {
        void fileUploadFinish(FileUploadFinishEvent e);
    }

    class FileUploadErrorEvent extends FileUploadEvent {

        private final Exception cause;

        public FileUploadErrorEvent(String fileName, long contentLength, Exception cause) {
            super(fileName, contentLength);

            this.cause = cause;
        }

        public Exception getCause() {
            return cause;
        }
    }

    interface FileUploadErrorListener {
        void fileUploadError(FileUploadErrorEvent e);
    }

    void addFileUploadStartListener(FileUploadStartListener listener);
    void removeFileUploadStartListener(FileUploadStartListener listener);

    void addFileUploadFinishListener(FileUploadFinishListener listener);
    void removeFileUploadFinishListener(FileUploadFinishListener listener);

    void addFileUploadErrorListener(FileUploadErrorListener listener);
    void removeFileUploadErrorListener(FileUploadErrorListener listener);

    /**
     * Returns maximum allowed file size in bytes.
     */
    long getFileSizeLimit();

    /**
     * Sets maximum allowed file size in bytes.
     * Default value is 0. In this case component uses system value
     */
    void setFileSizeLimit(long fileSizeLimit);

    /**
     * Set white list of file extensions.
     * Example: <pre>{@code fileUpload.setPermittedExtensions(".png,.jpeg")}</pre>
     * @param permittedExtensions set of file extensions.
     */
    void setPermittedExtensions(Set<String> permittedExtensions);

    /**
     * Return white list of file extensions.
     * @return set of file extensions.
     */
    Set<String> getPermittedExtensions();
}