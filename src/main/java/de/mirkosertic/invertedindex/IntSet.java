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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;

public class IntSet {

    private static final int SIZE_FACTOR = 16;

    private int[] data;
    private int size;

    public IntSet() {
        data = new int[SIZE_FACTOR];
    }

    public IntSet(Collection<Integer> aCollection) {
        data = new int[aCollection.size()];
        int counter = 0;
        for (int theValue : aCollection) {
            data[counter++] = theValue;
        }
        size = aCollection.size();
        internalSort();
    }

    private void internalAdd(int aValue) {
        if (contains(aValue)) {
            return;
        }

        if (size < data.length - 1) {
            data[size++] = aValue;
        } else {
            int[] theNewData = new int[data.length + SIZE_FACTOR];
            for (int i=0;i<data.length;i++) {
                theNewData[i] = data[i];
            }
            data = theNewData;
            data[size++] = aValue;
        }
    }

    private void internalSort() {
        Arrays.sort(data, 0 ,size);
    }

    public void add(int aValue) {
        internalAdd(aValue);
        internalSort();
    }

    public void forEach(BiConsumer<Integer, Integer> aConsumer) {
        for (int i=0;i<size;i++) {
            aConsumer.accept(i, data[i]);
        }
    }

    public boolean contains(int aValue) {

        if (size == 0) {
            return false;
        }

        int theStart = size / 2;
        int theCurrentValue = data[theStart];
        if (theCurrentValue == aValue) {
            return true;
        }
        if (theCurrentValue > aValue) {
            while(--theStart>=0) {
                theCurrentValue = data[theStart];
                if (theCurrentValue == aValue) {
                    return true;
                }
                if (theCurrentValue < aValue) {
                    return false;
                }
            }
            return false;
        }
        while(++theStart < size) {
            theCurrentValue = data[theStart];
            if (theCurrentValue == aValue) {
                return true;
            }
            if (theCurrentValue > aValue) {
                return false;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public IntSet retainAll(IntSet aOtherIntSet) {
        IntSet theResult = new IntSet();
        for (int i=0;i<size;i++) {
            if (aOtherIntSet.contains(data[i])) {
                theResult.internalAdd(data[i]);
            }
        }
        theResult.internalSort();
        return theResult;
    }
}