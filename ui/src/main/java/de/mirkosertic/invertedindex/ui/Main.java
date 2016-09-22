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
import de.mirkosertic.invertedindex.core.SuggestResult;
import de.mirkosertic.invertedindex.core.ToLowercaseTokenHandler;
import de.mirkosertic.invertedindex.core.TokenSequenceQuery;
import de.mirkosertic.invertedindex.core.TokenSequenceSuggester;
import de.mirkosertic.invertedindex.core.Tokenizer;
import de.mirkosertic.invertedindex.core.UpdateIndexHandler;
import de.mirkosertic.invertedindex.ui.electron.Electron;
import de.mirkosertic.invertedindex.ui.electron.Remote;
import de.mirkosertic.invertedindex.ui.electron.Shell;
import de.mirkosertic.invertedindex.ui.node.events.EventEmitter;
import de.mirkosertic.invertedindex.ui.node.fs.FS;
import de.mirkosertic.invertedindex.ui.node.fs.Stats;
import de.mirkosertic.invertedindex.ui.node.path.Path;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.dom.html.HTMLOptionElement;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {

    private static final Window WINDOW = Window.current();

    public static void main(String[] args) {

        Electron theElectron = Electron.require();
        Remote theRemote = theElectron.getRemote();

        String theUserHome = theRemote.getApp().getPath("home");
        theUserHome = "/home/sertic/ownCloud/Briefe und Schriftverkehr";

        FS theFilesystem = theRemote.require("fs");
        Path thePath = theRemote.require("path");

        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(new ToLowercaseTokenHandler(theIndexHandler));

        HTMLElement theStats = (HTMLElement) WINDOW.getDocument().getElementById("stats");

        EventEmitter theIPC = theElectron.getIpcRenderer();
        theIPC.on("text-extracted", (aEvent, aArg) -> {
            TextDocumentData theData = (TextDocumentData) aArg;
            Console.log("Extracted text received for " + theData.getFilename() + " " +theData.getData().length());
            theTokenizer.process(new Document(theData.getFilename(), theData.getData()));

            theStats.setInnerHTML(theIndex.getDocumentCount() + " documents indexed, " + theIndex.getTokenDictionary().getTokensCount()+" unique tokens");
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

        HTMLDataList theSuggestions = (HTMLDataList) WINDOW.getDocument().getElementById("suggestions");

        HTMLButtonElement theSearchButton = (HTMLButtonElement) WINDOW.getDocument().getElementById("performsearch");
        HTMLInputElement theSearchPhrase = (HTMLInputElement) WINDOW.getDocument().getElementById("searchphrase");
        theSearchPhrase.addEventListener("keypress", evt -> suggest(theIndex, theSearchPhrase.getValue(), theSuggestions));

        HTMLElement theSearchResult = WINDOW.getDocument().getElementById("searchresult");

        theSearchButton.addEventListener("click", evt -> search(theIndex, theSearchPhrase.getValue(), theSearchResult, theElectron.getShell()));

        clearChildren(theSearchResult);
    }

    private static void clearChildren(HTMLElement aElement) {
        while(aElement.getChildNodes().getLength() > 0)  {
            aElement.removeChild(aElement.getChildNodes().item(0));
        }
    }

    private static void suggest(InvertedIndex aIndex, String aSearchPhrase, HTMLDataList aSuggestions) {

        clearChildren(aSuggestions);

        ArrayList<String> theTokens = new ArrayList<>();
        for (StringTokenizer theTokenizer = new StringTokenizer(aSearchPhrase, " "); theTokenizer.hasMoreTokens();) {
            theTokens.add(theTokenizer.nextToken());
        }
        String[] theTokensArray = theTokens.toArray(new String[theTokens.size()]);
        if (theTokensArray.length>1) {
            theTokensArray[theTokensArray.length-1] = theTokensArray[theTokensArray.length-1] + "*";
        }

        TokenSequenceSuggester theSuggester = new TokenSequenceSuggester(theTokensArray);
        SuggestResult theResult = theSuggester.suggestWith(aIndex);
        Console.log("Suggestions for : " + aSearchPhrase);
        for (String theSuggestion : theResult.getSuggestions()) {
            String theValue = "";
            for (int i=0;i<theTokens.size() - 1;i++) {
                if (theValue.length() > 0) {
                    theValue = theValue + " ";
                }
                theValue += theTokens.get(i);
            }
            if (theValue.length() > 0) {
                theValue = theValue + " ";
            }
            theValue += theSuggestion;

            Console.log("Search suggestion : " + theValue);

            HTMLOptionElement theNewOption = (HTMLOptionElement) WINDOW.getDocument().createElement("option");
            theNewOption.setValue(theValue);

            aSuggestions.appendChild(theNewOption);
        }
    }

    private static void search(InvertedIndex aIndex, String aSearchPhrase, HTMLElement aSearchResult, Shell aShell) {

        clearChildren(aSearchResult);

        ArrayList<String> theTokens = new ArrayList<>();
        for (StringTokenizer theTokenizer = new StringTokenizer(aSearchPhrase, " "); theTokenizer.hasMoreTokens();) {
            theTokens.add(theTokenizer.nextToken());
        }

        TokenSequenceQuery theQuery = new TokenSequenceQuery(theTokens.toArray(new String[theTokens.size()]));
        long theStart = System.currentTimeMillis();
        Result theQueryResult = aIndex.query(theQuery);
        long theDuration = System.currentTimeMillis() - theStart;

        for (int i=0;i<theQueryResult.getSize();i++) {
            IndexedDoc theDoc = theQueryResult.getDoc(i);

            HTMLElement theEntry = WINDOW.getDocument().createElement("div");
            theEntry.setAttribute("class", "result");

            HTMLElement theTitle = WINDOW.getDocument().createElement("div");
            theTitle.setAttribute("class", "title");
            theTitle.setInnerHTML(theDoc.getName());
            theTitle.addEventListener("click", evt -> aShell.openItem(theDoc.getName()));

            HTMLElement theFilename = WINDOW.getDocument().createElement("div");
            theFilename.setAttribute("class", "filename");
            theFilename.setInnerHTML(theDoc.getName());

            HTMLElement theHighlights = WINDOW.getDocument().createElement("div");
            theHighlights.setAttribute("class", "highlights");
            theHighlights.setInnerHTML("lala lala lala");

            theEntry.appendChild(theTitle);
            theEntry.appendChild(theFilename);
            theEntry.appendChild(theHighlights);

            aSearchResult.appendChild(theEntry);
        }
    }
}