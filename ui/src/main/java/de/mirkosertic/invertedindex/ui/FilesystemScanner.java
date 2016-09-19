/*
 * Copyright 2016 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.invertedindex.ui;

import de.mirkosertic.invertedindex.ui.node.fs.FS;
import de.mirkosertic.invertedindex.ui.node.fs.Stats;
import de.mirkosertic.invertedindex.ui.node.path.Path;

public class FilesystemScanner {

    public interface FileProcessor {

        boolean accepts(String aFileName, Stats aStats);

        void handle(String aFileName);
    }

    private final FS fileSystem;
    private final Path path;

    public FilesystemScanner(FS aFileSystem, Path aPath) {
        fileSystem = aFileSystem;
        path = aPath;
    }

    public void submitFile(String aBaseDirectory, FileProcessor aProcessor) {
        Thread theRunner = new Thread() {
            @Override
            public void run() {
                visitFile(aBaseDirectory, aProcessor);
            }
        };
        theRunner.start();
    }

    private boolean visitFile(String aFileName, FileProcessor aProcessor) {
        try {
            for (String thePath : fileSystem.readdirSync(aFileName)) {

                Thread.yield();

                String theFullpath = aFileName + (path.getDelimiter().equals(":") ? "/" : "\\") + thePath;

                Stats theStats = fileSystem.statSync(theFullpath);

                if (theStats.isDirectory()) {
                    if (!visitFile(theFullpath, aProcessor)) {
                        return false;
                    }
                } else {
                    if (aProcessor.accepts(theFullpath, theStats)) {
                        aProcessor.handle(theFullpath);
                    }
                }
            }

            return true;
        } catch (Throwable aThrowable) {
            // Something went wrong here
            return false;
        }
    }
}
