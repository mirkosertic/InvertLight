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
package de.mirkosertic.invertedindex.core;

import java.util.StringTokenizer;

public class Tokenizer {

    private final TokenHandler tokenHandler;

    public Tokenizer(TokenHandler aTokenHandler) {
        tokenHandler = aTokenHandler;
    }

    public void process(Document aDocument) {
        tokenHandler.beginDocument(aDocument);
        String theContent = aDocument.getContent();
        for (StringTokenizer theST = new StringTokenizer(theContent, ".,?! \n\t();:[]"); theST.hasMoreTokens();) {
            String theToken = theST.nextToken().trim();
            if (theToken.length() > 0) {
                tokenHandler.handleToken(theToken);
            }
        }
        tokenHandler.endDocument();
    }
}