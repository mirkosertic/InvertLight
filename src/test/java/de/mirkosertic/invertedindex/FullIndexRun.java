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

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FullIndexRun {

    public static void main(String[] args) throws IOException {
        InvertedIndex theIndex = new InvertedIndex();
        UpdateIndexHandler theIndexHandler = new UpdateIndexHandler(theIndex);
        Tokenizer theTokenizer = new Tokenizer(new ToLowercaseTokenHandler(theIndexHandler));

        File theOrigin = new File("/home/sertic/ownCloud/Textcontent");
        for (File theFile : theOrigin.listFiles()) {
            System.out.println("Indexing " + theFile);
            String theFileContent = IOUtils.toString(new FileReader(theFile));
            theTokenizer.process(new Document(theFile.getName(), theFileContent));
        }

        System.out.println(theIndex.getTokenCount() + " unique tokens");
        System.out.println(theIndex.getDocumentCount() + " documents");

        theIndex.tokens.entrySet().stream().sorted(
                (o1, o2) -> ((Integer)o1.getValue().getOccoursInDocuments().size()).compareTo(o2.getValue().getOccoursInDocuments().size())).forEach(t -> {
            System.out.println(t.getKey() + " -> " + t.getValue().getOccoursInDocuments().size());
        });

        System.out.println("Query");

        Result theResult = theIndex.query(new TokenSequenceQuery(new String[] {"introduction","to", "aop"}));
        System.out.println(theResult.getSize());
        for (int i=0;i<theResult.getSize();i++) {
            System.out.println(theResult.getDoc(i).getName());
        }

        while(true) {
            theResult = theIndex.query(new TokenSequenceQuery(new String[] {"introduction","to", "aop"}));
        }
    }
}