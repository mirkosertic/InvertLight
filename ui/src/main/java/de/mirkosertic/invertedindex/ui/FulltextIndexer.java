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

import de.mirkosertic.invertedindex.core.Document;
import de.mirkosertic.invertedindex.core.Tokenizer;
import de.mirkosertic.invertedindex.ui.node.fs.FS;
import de.mirkosertic.invertedindex.ui.pdfjs.PDFJS;
import de.mirkosertic.invertedindex.ui.pdfjs.TextItem;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.typedarrays.Uint8Array;

import java.util.Map;
import java.util.TreeMap;

public class FulltextIndexer {

    private final Tokenizer tokenizer;
    private final FS fileSystem;
    private final PDFJS pdfjs;

    public FulltextIndexer(Tokenizer aTokenizer, FS aFilesystem, PDFJS aPDFJS) {
        tokenizer = aTokenizer;
        fileSystem = aFilesystem;
        pdfjs = aPDFJS;
    }

    public void submit(String aFileName) {
        Thread theThread = new Thread() {
            @Override
            public void run() {
                process(aFileName);
            }
        };
        theThread.start();
    }

    private void process(String aFileName) {

        Console.log("Reading PDF file " + aFileName);

        Uint8Array theData = fileSystem.readFileSync(aFileName);

        pdfjs.getDocument(theData).then(aValue -> {

            Map<Integer, StringBuilder> thePageData = new TreeMap<>();

            Console.log(" File has " + aValue.getNumPages() + " pages");

            for (int i = 1; i <= aValue.getNumPages(); i++) {

                final int thePageCounter = i;

                aValue.getPage(i).then(aPage -> {
                    aPage.getTextContent().then(aContent -> {

                        StringBuilder theResult = new StringBuilder();
                        JSArray<TextItem> theItems = aContent.getItems();
                        for (int j = 0; j < theItems.getLength(); j++) {
                            theResult.append(theItems.get(j).getStr());
                        }
                        thePageData.put(thePageCounter, theResult);
                        if (thePageData.size() == aValue.getNumPages()) {

                            StringBuilder theFullData = new StringBuilder();
                            for (Map.Entry<Integer, StringBuilder> theEntry : thePageData.entrySet()) {
                                theFullData.append(theEntry.getValue());
                                theFullData.append(" ");
                            }

                            Console.log(" Full document extracted. length = " + theFullData.length());

                            tokenizer.process(new Document(aFileName, theFullData.toString()));
                        }
                    });
                });
            }
        });
    }
}