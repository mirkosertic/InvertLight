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

import java.util.HashMap;
import java.util.Map;

public class TokenDictionary {

    private final Map<String, Integer> tokensToID;
    private final Map<Integer, String> idToTokens;

    public TokenDictionary() {
        tokensToID = new HashMap<>();
        idToTokens = new HashMap<>();
    }

    public int getTokenIDFor(String aToken) {
        Integer theID = tokensToID.get(aToken);
        if (theID == null) {
            theID = tokensToID.size();
            tokensToID.put(aToken, theID);
            idToTokens.put(theID, aToken);
        }
        return theID;
    }

    public String getTokenForID(int aTokenID) {
        return idToTokens.get(aTokenID);
    }
}
