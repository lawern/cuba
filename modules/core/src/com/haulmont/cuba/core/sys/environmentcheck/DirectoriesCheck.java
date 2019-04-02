/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */

package com.haulmont.cuba.core.sys.environmentcheck;

import com.haulmont.cuba.core.sys.AppContext;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DirectoriesCheck implements EnvironmentCheck {

    @Override
    public List<CheckFailedResult> doCheck() {
        List<CheckFailedResult> result = new ArrayList<>();
        String dataDir = AppContext.getProperty("cuba.dataDir");
        if (dataDir != null) {
            File dataDirFile = new File(dataDir);
            boolean readable = Files.isReadable(dataDirFile.toPath());
            boolean writable = Files.isWritable(dataDirFile.toPath());
            boolean isDir = dataDirFile.isDirectory();
            if (!writable && !isDir) {
                try {
                    isDir = dataDirFile.mkdirs();
                    readable = Files.isReadable(dataDirFile.toPath());
                    writable = Files.isWritable(dataDirFile.toPath());
                } catch (SecurityException e) {
                    result.add(new CheckFailedResult(
                            String.format("Wrong permissions for work directory. Readable: %b, Writable: %b",
                                    readable, writable), e));
                }
            }
            if (!writable || !readable || !isDir) {
                result.add(new CheckFailedResult(
                        String.format("Wrong permissions for work directory. Readable: %b, Writable: %b, Is directory: %b",
                                readable, writable, isDir), null));
            }
        } else {
            result.add(new CheckFailedResult("Unable to get work directory path from \'cuba.dataDir\' property",
                    null));
        }

        String tempDir = AppContext.getProperty("cuba.tempDir");
        if (tempDir != null) {
            File tempDirFile = new File(tempDir);
            boolean readable = Files.isReadable(tempDirFile.toPath());
            boolean writable = Files.isWritable(tempDirFile.toPath());
            boolean isDir = tempDirFile.isDirectory();
            if (!writable && !isDir) {
                try {
                    isDir = tempDirFile.mkdirs();
                    readable = Files.isReadable(tempDirFile.toPath());
                    writable = Files.isWritable(tempDirFile.toPath());
                } catch (SecurityException e) {
                    result.add(new CheckFailedResult(
                            String.format("Wrong permissions for temporary directory. Readable: %b, Writable: %b",
                                    readable, writable), e));
                }
            }
            if (!writable || !readable || !isDir) {
                result.add(new CheckFailedResult(
                        String.format("Wrong permissions for temporary directory. Readable: %b, Writable: %b, Is directory: %b",
                                readable, writable, isDir), null));
            }
        } else {
            result.add(new CheckFailedResult("Unable to get temporary directory path from \'cuba.tempDir\' property",
                    null));
        }

        return result;
    }
}
