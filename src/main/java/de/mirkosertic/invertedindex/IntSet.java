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

import java.util.function.Consumer;

public class IntSet {

    private static final int SIZE_FACTOR = 128;

    private int[] data;
    private int maxValue;
    private int minValue;
    private int size;

    public IntSet() {
        data = new int[SIZE_FACTOR];
        maxValue = Integer.MIN_VALUE;
        minValue = Integer.MAX_VALUE;
    }

    public void add(int aValue) {

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

        maxValue = Math.max(maxValue, aValue);
        minValue = Math.min(minValue, aValue);
    }

    public void forEach(Consumer<Integer> aConsumer) {
        for (int i=0;i<size;i++) {
            aConsumer.accept(data[i]);
        }
    }

    public boolean contains(int aValue) {
        if (aValue > maxValue) {
            return false;
        }
        if (aValue < minValue) {
            return false;
        }
        for (int i=0;i<data.length;i++) {
            if (data[i] == aValue) {
                return true;
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
                theResult.add(data[i]);
            }
        }
        return theResult;
    }
}