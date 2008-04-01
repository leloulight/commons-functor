/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.functor.core.comparator;

import java.io.Serializable;
import org.apache.commons.functor.UnaryPredicate;

/**
 * A {@link UnaryPredicate} that tests whether a {@link Comparable} object is
 * within a range. The range is defined in the constructor.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author  Jason Horman (jason@jhorman.org)
 */

public class IsWithinRange implements UnaryPredicate, Serializable {

    /***************************************************
     *  Instance variables
     ***************************************************/

    /** The minimum value of the range. */
    private Comparable min = null;
    /** The maximum value of the range. */
    private Comparable max = null;
    /** Hashcode of the name of this Predicate. */
    private static final int nameHashCode = "IsWithinRange".hashCode();

    /***************************************************
     *  Constructors
     ***************************************************/

    /**
     * Constructor the object by passing in the range that will
     * be used in the {@link #test}.
     */
    public IsWithinRange(Comparable min, Comparable max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("min and max must not be null");
        }

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must be <= max");
        }

        this.min = min;
        this.max = max;
    }

    /***************************************************
     *  Instance methods
     ***************************************************/

    /**
     * Test if the passed in object is within the specified range.
     */
    public boolean test(Object o) {
        Comparable c = (Comparable)o;
        return c.compareTo(min) >= 0 && c.compareTo(max) <= 0;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IsWithinRange)) return false;
        final IsWithinRange isWithinRange = (IsWithinRange) o;
        if (!max.equals(isWithinRange.max)) return false;
        if (!min.equals(isWithinRange.min)) return false;
        return true;
    }

    public int hashCode() {
        return 29 * min.hashCode() + max.hashCode() + nameHashCode;
    }

    public String toString() {
        return "IsBetween(" + min + ", " + max + ")";
    }
}