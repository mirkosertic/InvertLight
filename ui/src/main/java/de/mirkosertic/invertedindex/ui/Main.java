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
import de.mirkosertic.invertedindex.core.IndexedDoc;
import de.mirkosertic.invertedindex.core.InvertedIndex;
import de.mirkosertic.invertedindex.core.Result;
import de.mirkosertic.invertedindex.core.ToLowercaseTokenHandler;
import de.mirkosertic.invertedindex.core.TokenSequenceQuery;
import de.mirkosertic.invertedindex.core.Tokenizer;
import de.mirkosertic.invertedindex.core.UpdateIndexHandler;
import de.mirkosertic.invertedindex.ui.electron.Electron;
import de.mirkosertic.invertedindex.ui.electron.Remote;
import de.mirkosertic.invertedindex.ui.node.events.EventEmitter;
import de.mirkosertic.invertedindex.ui.node.fs.FS;
import de.mirkosertic.invertedindex.ui.node.fs.Stats;
import de.mirkosertic.invertedindex.ui.node.path.Path;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {

    private static final Window WINDOW = Window.current();

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

        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(new ToLowercaseTokenHandler(theIndexHandler));

        EventEmitter theIPC = theElectron.getIpcRenderer();
        theIPC.on("text-extracted", (aEvent, aArg) -> {
            TextDocumentData theData = (TextDocumentData) aArg;
            Console.log("Extracted text received for " + theData.getFilename() + " " +theData.getData().length());
            theTokenizer.process(new Document(theData.getFilename(), theData.getData()));
        });

        FilesystemScanner theScanner = new FilesystemScanner(theFilesystem, thePath);
        theScanner.submitFile(theUserHome, new FilesystemScanner.FileProcessor() {
            @Override
            public boolean accepts(String aFileName, Stats aStats) {
                return aFileName.toLowerCase().endsWith(".pdf");
            }

            @Override
            public void handle(String aFileName) {
                Console.log("Loading document " + aFileName + " in Background");
                theIPC.send("load-document", JSString.valueOf(aFileName));
            }
        });

        HTMLButtonElement theSearchButton = (HTMLButtonElement) WINDOW.getDocument().getElementById("search");
        HTMLInputElement theSearchPhrase = (HTMLInputElement) WINDOW.getDocument().getElementById("searchphrase");
        HTMLElement theSearchResult = WINDOW.getDocument().getElementById("searchresult");

        theSearchButton.addEventListener("click", evt -> search(theIndex, theSearchPhrase.getValue(), theSearchResult));
    }

    private static void search(InvertedIndex aIndex, String aSearchPhrase, HTMLElement aSearchResult) {
        StringBuilder theResult = new StringBuilder();
        theResult.append(aIndex.getDocumentCount()+ " documents in index");
        theResult.append("<br>");

        ArrayList<String> theTokens = new ArrayList<>();
        for (StringTokenizer theTokenizer = new StringTokenizer(aSearchPhrase, " "); theTokenizer.hasMoreTokens();) {
            theTokens.add(theTokenizer.nextToken());
        }

        TokenSequenceQuery theQuery = new TokenSequenceQuery(theTokens.toArray(new String[theTokens.size()]));
        long theStart = System.currentTimeMillis();
        Result theQueryResult = aIndex.query(theQuery);
        long theDuration = System.currentTimeMillis() - theStart;

        theResult.append(theDuration+"ms query time<br>");
        theResult.append(theQueryResult.getSize()+" documents found<br>");
        for (int i=0;i<theQueryResult.getSize();i++) {
            IndexedDoc theDoc = theQueryResult.getDoc(i);
            theResult.append(theDoc.getName());
            theResult.append("<br/>");
        }

        aSearchResult.setInnerHTML(theResult.toString());
    }
}