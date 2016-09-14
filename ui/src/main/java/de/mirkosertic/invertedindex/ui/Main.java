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
import de.mirkosertic.invertedindex.core.InvertedIndex;
import de.mirkosertic.invertedindex.core.SuggestResult;
import de.mirkosertic.invertedindex.core.Suggester;
import de.mirkosertic.invertedindex.core.TokenSequenceSuggester;
import de.mirkosertic.invertedindex.core.Tokenizer;
import de.mirkosertic.invertedindex.core.UpdateIndexHandler;
import de.mirkosertic.invertedindex.ui.electron.Electron;
import de.mirkosertic.invertedindex.ui.electron.Remote;
import de.mirkosertic.invertedindex.ui.electron.fs.FS;
import de.mirkosertic.invertedindex.ui.electron.fs.Stats;
import de.mirkosertic.invertedindex.ui.electron.path.Path;
import de.mirkosertic.invertedindex.ui.pdfjs.PDFJS;
import de.mirkosertic.invertedindex.ui.pdfjs.TextItem;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.typedarrays.Uint8Array;

public class Main {

    private static final Window WINDOW = Window.current();

    private static boolean visitFile(String aFileName, FS aFS, String aDelimiter, PDFJS aPDFJS, Tokenizer aTokenizer) {
        for (String thePath : aFS.readdirSync(aFileName)) {

            String theFullpath = aFileName + (aDelimiter.equals(":") ? "/" : "\\")  + thePath;

            try {
                Stats theStats = aFS.statSync(theFullpath);
                if (theStats.isDirectory()) {
                    if (!visitFile(theFullpath, aFS, aDelimiter, aPDFJS, aTokenizer)) {
                        return false;
                    }
                } else {

                    if (theFullpath.toLowerCase().endsWith(".pdf")) {
                        Uint8Array theData = aFS.readFileSync(theFullpath);

                        HTMLElement theDiv2 = WINDOW.getDocument().createElement("div");
                        theDiv2.setInnerHTML(theFullpath+" "+ theData.getByteLength() + " bytes");
                        WINDOW.getDocument().getBody().appendChild(theDiv2);

                        aPDFJS.getDocument(theData).then(aValue -> {

                            HTMLElement theDiv3 = WINDOW.getDocument().createElement("div");
                            theDiv3.setInnerHTML(aValue.getNumPages() + " Pages");
                            WINDOW.getDocument().getBody().appendChild(theDiv3);

                            for (int i=1;i<=aValue.getNumPages();i++) {
                                aValue.getPage(i).then(aPage -> {
                                    aPage.getTextContent().then(aContent -> {
                                        StringBuilder theResult = new StringBuilder();
                                        JSArray<TextItem> theItems = aContent.getItems();
                                        for (int j=0;j<theItems.getLength();j++) {
                                            theResult.append(theItems.get(j).getStr());
                                        }

                                        Document theDocument = new Document(theFullpath, theResult.toString());
                                        aTokenizer.process(theDocument);

                                        HTMLElement theDiv4 = WINDOW.getDocument().createElement("div");
                                        theDiv4.setInnerHTML("sent to index");
                                        WINDOW.getDocument().getBody().appendChild(theDiv4);
                                    });
                                });
                            }
                        });
                    }
                }
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Electron theElectron = Electron.require();
        Remote theRemote = theElectron.getRemote();
        String theUserHome = theRemote.getApp().getPath("home");

        HTMLDocument theDocument = WINDOW.getDocument();
        HTMLElement theDiv = theDocument.createElement("div");
        theDiv.setInnerHTML(theUserHome);
        theDocument.getBody().appendChild(theDiv);

        FS theFilesystem = theRemote.require("fs");
        Path thePath = theRemote.require("path");
        PDFJS thePDF = theRemote.require("pdfjs-dist");
        thePDF.initializeWorker();

        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theIndexHandler);

        visitFile(theUserHome, theFilesystem, thePath.getDelimiter(), thePDF, theTokenizer);

        Suggester theSuggester = new TokenSequenceSuggester("nothing");
        SuggestResult theResult = theIndex.suggest(theSuggester);
    }
}
