/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/kata/two/TestBinaryChop.java,v 1.4 2003/12/01 16:40:11 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived 
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.functor.example.kata.two;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.Function;
import org.apache.commons.functor.generator.util.IntegerRange;

/**
 * See http://pragprog.com/pragdave/Practices/Kata/KataTwo.rdoc,v
 * for more information on this Kata.
 * 
 * @version $Revision: 1.4 $ $Date: 2003/12/01 16:40:11 $
 * @author Rodney Waldhoff
 */
public class TestBinaryChop extends TestCase {
    public TestBinaryChop(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryChop.class);
    }
        
    public void setUp() throws Exception {
        super.setUp();
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    private void chopTest(BinaryChop chopper) {
        assertEquals(-1, chopper.find(3, new int[0]));
        assertEquals(-1, chopper.find(3, new int[] { 1 }));
        assertEquals(0,  chopper.find(1, new int[] { 1 }));

        assertEquals(0,  chopper.find(1, new int[] { 1, 3, 5 }));
        assertEquals(1,  chopper.find(3, new int[] { 1, 3, 5 }));
        assertEquals(2,  chopper.find(5, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(0, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(2, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(4, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(6, new int[] { 1, 3, 5 }));

        assertEquals(0,  chopper.find(1, new int[] { 1, 3, 5, 7 }));
        assertEquals(1,  chopper.find(3, new int[] { 1, 3, 5, 7 }));
        assertEquals(2,  chopper.find(5, new int[] { 1, 3, 5, 7 }));
        assertEquals(3,  chopper.find(7, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(0, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(2, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(4, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(6, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(8, new int[] { 1, 3, 5, 7 }));
        
        List largeList = (List)(new IntegerRange(0,100001).toCollection());
        assertEquals(-1, chopper.find(new Integer(-5),largeList));
        assertEquals(100000, chopper.find(new Integer(100000),largeList));
        assertEquals(0, chopper.find(new Integer(0),largeList));
        assertEquals(50000, chopper.find(new Integer(50000),largeList));
        
     }

    public void testIterative() {
        chopTest(
            new BaseBinaryChop() {
                public int find(Object seeking, List list) {
                    int high = list.size();
                    int low = 0;
                    int cur = 0;
                    while(low < high) {
                        cur = (high+low)/2;
                        int comp = ((Comparable)(list.get(cur))).compareTo(seeking);
                        if(comp == 0) { 
                            return cur;
                        } else if(comp < 0) {
                            if(low == cur) { cur++; }
                            low = cur;
                        } else {                                
                            high = cur;
                        }
                    }
                    return -1;
                }
            });    
    }

    public void testRecursive() {
        chopTest(
            new BaseBinaryChop() {
                public int find(Object seeking, List list) {                    
                    if(list.size() == 0) {
                        return -1;
                    } else {
                        int pivot = list.size()/2;
                        int offset = 0;
                        int comp = ((Comparable)(list.get(pivot))).compareTo(seeking);
                        if(comp == 0) { 
                            return pivot;
                        } else if(comp < 0) {
                            offset = find(seeking,list.subList(Math.max(pivot,1),list.size()));
                        } else {                                
                            offset = find(seeking,list.subList(0,pivot));
                        }
                        return -1 == offset ? -1 : (comp == 1) ? offset : pivot+offset;
                    }
                }
            });    
    }

    public void testRecursive2() {
        chopTest(
            new BaseBinaryChop() {
                public int find(Object seeking, List list) { 
                    return find(seeking,list,0,list.size());
                }
                
                private int find(Object seeking, List list, int low, int high) {
                    if(low >= high) {
                        return -1;
                    } else {
                        int cur = (high+low)/2;
                        int comp = ((Comparable)(list.get(cur))).compareTo(seeking);
                        if(comp == 0) { 
                            return cur;
                        } else if(comp < 0) {
                            if(low == cur) { cur++; }
                            return find(seeking,list,cur,high);
                        } else {                                
                            return find(seeking,list,low,cur);
                        }
                    }
                }
            });    
    }    
    public void testTailRecursive() {
        chopTest(
            new BaseBinaryChop() {
                public int find(final Object seeking, final List list) { 
                    return ((Number)Algorithms.recurse(
                        new Function() {
                            public Object evaluate() {
                                if(low < high) {
                                    int mid = (high+low)/2;                                
                                    int comp = ((Comparable)(list.get(mid))).compareTo(seeking);
                                    if(comp == 0) { 
                                        return new Integer(mid);
                                    } else if(comp < 0) {
                                        if(mid == low) { mid++; }
                                        low = mid;
                                        return this;
                                    } else {  
                                        high = mid;
                                        return this;                              
                                    }
                                } else {
                                    return new Integer(-1);
                                }
                            }
                            int high = list.size();
                            int low = 0;
                        })).intValue();
                }                   
            });    
    }    
}