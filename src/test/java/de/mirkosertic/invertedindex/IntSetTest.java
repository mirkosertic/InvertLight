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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntSetTest {

    @Test
    public void testMany() {
        IntSet theSet = new IntSet();
        for (int i=0;i<400;i++) {
            theSet.add(i);
        }
        assertEquals(400, theSet.size());
    }

    @Test
    public void testSet() {
        IntSet theSet = new IntSet();
        theSet.add(10);
        theSet.add(10);
        assertEquals(1, theSet.size());
    }

    @Test
    public void testContains() {
        IntSet theSet = new IntSet();
        theSet.add(10);
        theSet.add(20);
        theSet.add(30);
        assertFalse(theSet.contains(999));
        assertFalse(theSet.contains(9));
        assertTrue(theSet.contains(10));
        assertTrue(theSet.contains(20));
        assertTrue(theSet.contains(30));
    }

    @Test
    public void testRetains() {
        IntSet theSet1 = new IntSet();
        theSet1.add(1);
        theSet1.add(10);
        theSet1.add(12);
        theSet1.add(20);

        IntSet theSet2 = new IntSet();
        theSet2.add(10);
        theSet2.add(20);
        theSet2.add(99);

        IntSet theResult = theSet1.retainAll(theSet2);
        assertEquals(2, theResult.size());
        assertTrue(theResult.contains(10));
        assertTrue(theResult.contains(20));
    }
}