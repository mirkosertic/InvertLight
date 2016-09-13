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

public class ToLowercaseTokenHandler implements TokenHandler {

    private final TokenHandler tokenHandler;

    public ToLowercaseTokenHandler(TokenHandler aTokenHandler) {
        tokenHandler = aTokenHandler;
    }

    @Override
    public long beginDocument(Document aDocument) {
        return tokenHandler.beginDocument(aDocument);
    }

    @Override
    public void handleToken(String aToken) {
        tokenHandler.handleToken(aToken.toLowerCase());
    }

    @Override
    public void endDocument() {
        tokenHandler.endDocument();
    }
}