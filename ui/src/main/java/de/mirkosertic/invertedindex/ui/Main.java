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

import de.mirkosertic.invertedindex.core.*;
import de.mirkosertic.invertedindex.ui.electron.Electron;
import de.mirkosertic.invertedindex.ui.electron.Remote;
import de.mirkosertic.invertedindex.ui.electron.fs.FS;
import de.mirkosertic.invertedindex.ui.electron.fs.Stats;
import de.mirkosertic.invertedindex.ui.electron.path.Path;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;

public class Main {

    private static final Window WINDOW = Window.current();

    private static void visitFile(String aFileName, FS aFS, String aDelimiter) {
        for (String thePath : aFS.readdirSync(aFileName)) {

            String theFullpath = aFileName + (aDelimiter.equals(":") ? "/" : "\\")  + thePath;

            HTMLElement theDiv2 = WINDOW.getDocument().createElement("div");
            theDiv2.setInnerHTML(theFullpath);
            WINDOW.getDocument().getBody().appendChild(theDiv2);

            Stats theStats = aFS.statSync(theFullpath);
            if (theStats.isDirectory()) {
                visitFile(theFullpath, aFS, aDelimiter);
            }
        }
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
        visitFile(theUserHome, theFilesystem, thePath.getDelimiter());

        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(theIndexHandler);

        theTokenizer.process(new Document("doc1", "this is a test"));
        theTokenizer.process(new Document("doc2", "this is another test"));
        theTokenizer.process(new Document("doc3", "welcome home"));
        theTokenizer.process(new Document("doc4", "nothing here"));
        theTokenizer.process(new Document("doc4", "nothing already"));
        theTokenizer.process(new Document("doc4", "nothing already there"));

        Suggester theSuggester = new TokenSequenceSuggester("nothing");
        SuggestResult theResult = theIndex.suggest(theSuggester);
    }
}
