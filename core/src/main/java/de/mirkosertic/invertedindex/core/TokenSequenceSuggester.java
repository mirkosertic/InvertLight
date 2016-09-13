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

import java.util.*;

public class TokenSequenceSuggester extends AbstractSequenceProcessor implements Suggester {

    public final String[] tokens;

    public TokenSequenceSuggester(String... aTokens) {
        this.tokens = aTokens;
    }

    @Override
    public SuggestResult suggestWith(InvertedIndex aInvertedIndex) {

        ProcessorResult theResult = process(aInvertedIndex, tokens);
        if (theResult == null) {
            return SuggestResult.EMPTY;
        }

        TokenDictionary theDictionary = aInvertedIndex.getTokenDictionary();
        if (theDictionary.isWildCard(tokens[tokens.length - 1])) {
            return getWildcardSuggestResult(theResult, theDictionary);
        }
        return getSuggestResult(theResult, theDictionary);
    }

    private SuggestResult getWildcardSuggestResult(ProcessorResult aResult, TokenDictionary aDictionary) {

        Map<String, Integer> theFollowingTokensWithOccourences = new HashMap<>();
        for (PostingsList theList : aResult.lastPostings.values()) {

            IntSet theOccourences = theList.getOccoursInDocuments();
            IntSet theTest = aResult.documentIDs.retainAll(theOccourences);
            if (theTest.size() > 0) {
                String theToken = aDictionary.getTokenForID(theList.getTokenId());
                theFollowingTokensWithOccourences.put(theToken, theOccourences.size());
            }
        }

        List<String> theFollowingTokens = new ArrayList<>();

        List<Map.Entry<String, Integer>> theList = new ArrayList<>(theFollowingTokensWithOccourences.entrySet());
        Collections.sort(theList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Integer> theEntry : theList) {
            theFollowingTokens.add(theEntry.getKey());
        }

        return new SuggestResult(theFollowingTokens.toArray(new String[theFollowingTokens.size()]));
    }

    private SuggestResult getSuggestResult(ProcessorResult theResult, TokenDictionary theDictionary) {
        Map<String, Integer> theFollowingTokensWithOccourences = new HashMap<>();
        for (PostingsList theList : theResult.lastPostings.values()) {

            theList.forEachFollowingPosting(aEntry -> {
                PostingsList theFollowingPosting = aEntry.getKey();
                IntSet theOccourences = theFollowingPosting.getOccoursInDocuments();
                IntSet theTest = theResult.documentIDs.retainAll(theOccourences);
                if (theTest.size() > 0) {
                    String theToken = theDictionary.getTokenForID(theFollowingPosting.getTokenId());
                    theFollowingTokensWithOccourences.put(theToken, theOccourences.size());
                }
            });
        }

        List<String> theFollowingTokens = new ArrayList<>();

        List<Map.Entry<String, Integer>> theList = new ArrayList<>(theFollowingTokensWithOccourences.entrySet());
        Collections.sort(theList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Integer> theEntry : theList) {
            theFollowingTokens.add(theEntry.getKey());
        }

        return new SuggestResult(theFollowingTokens.toArray(new String[theFollowingTokens.size()]));
    }
}