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
package de.mirkosertic.invertedindex;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ContentFactory {

    private static void handle(Tika aTika, File aContentFile, File aTargetFile) throws IOException, TikaException {
        if (aContentFile.isDirectory()) {
            for (File theFile : aContentFile.listFiles()) {
                handle(aTika, theFile, aTargetFile);
            }
        } else {

            System.out.println("Processing " + aContentFile);

            try {
                Metadata theMetaData = new Metadata();

                String theStringData;
                try (InputStream theStream = Files.newInputStream(aContentFile.toPath(), StandardOpenOption.READ)) {
                    theStringData = aTika.parseToString(new BufferedInputStream(theStream), theMetaData);
                }

                File theTarget = new File(aTargetFile, aContentFile.getName() + ".txt");
                FileWriter theWriter = new FileWriter(theTarget);
                theWriter.write(theStringData);
                theWriter.close();
            } catch (Exception e) {
            }
        }
    }

    public static void main(String args[]) throws IOException, TikaException {
        Tika theTika = new Tika();
        theTika.setMaxStringLength(1024 * 1024 * 5);

        File theTarget = new File("/home/sertic/ownCloud/Textcontent");

        File theFile = new File("/home/sertic/ownCloud/eBooks");

        handle(theTika, theFile, theTarget);
    }
}
