/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/Algorithms.java,v 1.11 2003/11/25 19:39:44 rwaldhoff Exp $
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

package org.apache.commons.functor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.functor.core.collection.FilteredIterator;
import org.apache.commons.functor.core.collection.TransformedIterator;
import org.apache.commons.functor.core.composite.UnaryNot;
import org.apache.commons.functor.generator.BaseGenerator;
import org.apache.commons.functor.generator.Generator;
import org.apache.commons.functor.generator.IteratorToGeneratorAdapter;

/**
 * Utility methods and algorithms for applying functors to {@link Generator}s.
 * {@link Generator}s also define these utility methods as instance methods. The
 * {@link #apply apply}, {@link #select select}, and {@link #reject reject} methods 
 * return new Generators. This becomes useful for constructing nested expressions. 
 * For example:
 *
 * <pre>
 *      Algorithms.apply(new EachElement(list), func1)
 *          .filter(pred)
 *              .apply(func2)
 *                  .reject(func2)
 *                      .to();
 * </pre>
 *
 * @since 1.0
 * @version $Revision: 1.11 $ $Date: 2003/11/25 19:39:44 $
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public final class Algorithms {

    /** Public constructor for bean-ish APIs */
    public Algorithms() {        
    }

    public static Collection collect(Iterator iter) {
        return collect(iter,new ArrayList());
    }
    
    public static Collection collect(Iterator iter, Collection col) {
        while(iter.hasNext()) {
            col.add(iter.next());
        }
        return col;
    }
    
    /**
     * {@link ListIterator#set Set} each element of the
     * given {@link ListIterator ListIterator} to
     * the result of applying the 
     * given {@link UnaryFunction UnaryFunction} to
     * its original value.
     */
    public static void transform(ListIterator iter, UnaryFunction func) {
        while(iter.hasNext()) {
            iter.set(func.evaluate(iter.next()));
        }
    }

    /**
     * {@link Iterator#remove Remove} from the
     * given {@link Iterator Iterator} all elements 
     * that fail to match the
     * given {@link UnaryPredicate UnaryPredicate}.
     * 
     * @see #remove(Iterator,UnaryPredicate)
     */
    public static void retain(Iterator iter, UnaryPredicate pred) {
        while(iter.hasNext()) {
            if(!(pred.test(iter.next()))) {
                iter.remove();
            }
        }
    }

    /**
     * {@link Iterator#remove Renmove} from the
     * given {@link Iterator Iterator} all elements 
     * that match the
     * given {@link UnaryPredicate UnaryPredicate}.
     * 
     * @see #retain(Iterator,UnaryPredicate)
     */
    public static void remove(Iterator iter, UnaryPredicate pred) {
        while(iter.hasNext()) {
            if(pred.test(iter.next())) {
                iter.remove();
            }
        }        
    }
    
    /**
     * Returns an {@link Iterator} that will apply the given {@link UnaryFunction} to each
     * element when accessed.
     */
    public static final Iterator apply(Iterator iter, UnaryFunction func) {
        return TransformedIterator.transform(iter,func);
    }

    /**
     * Returns a {@link Generator} that will apply the given {@link UnaryFunction} to each
     * generated element.
     */
    public static final Generator apply(final Generator gen, final UnaryFunction func) {
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new UnaryProcedure() {
                    public void run(Object obj) {
                        proc.run(func.evaluate(obj));
                    }
                });
            }
        };
    }

    /**
     * Equivalent to 
     * <code>{@link #contains(Generator,UnaryPredicate) contains}(new {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter),pred)</code>.
     */
    public static final boolean contains(Iterator iter, UnaryPredicate pred) {
        return contains(new IteratorToGeneratorAdapter(iter),pred);
    }

    /**
     * Return <code>true</code> if some element in the given {@link Generator}
     * that matches the given {@link UnaryPredicate UnaryPredicate}.
     *
     * @see #detect(Generator,UnaryPredicate)
     */
    public static final boolean contains(final Generator gen, final UnaryPredicate pred) {
        // javas' inner classes suck, i should do this a different way i guess
        final boolean[] returnCode = new boolean[1];
        returnCode[0] = false;

        gen.run(new UnaryProcedure() {
            public void run(Object obj) {
                if (pred.test(obj)) {
                    returnCode[0] = true;
                    gen.stop();
                }
            }
        });

        return returnCode[0];
    }

    /**
     * Equivalent to <code>{@link #detect(Generator,UnaryPredicate) detect}(new {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter),pred)</code>.
     */
    public static final Object detect(Iterator iter, UnaryPredicate pred) {
        return detect(new IteratorToGeneratorAdapter(iter), pred);
    }

    /**
     * Equivalent to <code>{@link #detect(Generator,UnaryPredicate,Object) detect}(new {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter),pred,ifNone)</code>.
     */
    public static final Object detect(Iterator iter, UnaryPredicate pred, Object ifNone) {
        return detect(new IteratorToGeneratorAdapter(iter), pred, ifNone);
    }

    /**
     * Return the first element within the given {@link Generator} that matches
     * the given {@link UnaryPredicate UnaryPredicate}, or throw {@link
     * java.util.NoSuchElementException NoSuchElementException} if no
     * matching element can be found.
     *
     * @see #detect(Generator,UnaryPredicate,Object)
     * @throws NoSuchElementException If no element could be found.
     */
    public static final Object detect(final Generator gen, final UnaryPredicate pred) {
        final Object[] foundObj = new Object[1];
        gen.run(new UnaryProcedure() {
            public void run(Object obj) {
                if(pred.test(obj)) {
                    foundObj[0] = obj;
                    gen.stop();
                }
            }
        });

        if (foundObj[0] != null) {
            return foundObj[0];
        } else {
            throw new NoSuchElementException("No element matching " + pred + " was found.");
        }
    }

    /**
     * Return the first element within the given {@link Generator} that matches
     * the given {@link UnaryPredicate UnaryPredicate}, or return the given
     * (possibly <code>null</code> <code>Object</code> if no matching element
     * can be found.
     *
     * @see #detect(Generator,UnaryPredicate)
     */
    public static final Object detect(final Generator gen, final UnaryPredicate pred, Object ifNone) {
        final Object[] foundObj = new Object[1];
        gen.run(new UnaryProcedure() {
            public void run(Object obj) {
                if(pred.test(obj)) {
                    foundObj[0] = obj;
                    gen.stop();
                }
            }
        });

        if (foundObj[0] != null) {
            return foundObj[0];
        } else {
            return ifNone;
        }
    }

    /**
     * Equivalent to <code>{@link #foreach(Generator,UnaryProcedure) foreach}(new {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter),proc)</code>.
     */
    public static final void foreach(Iterator iter, UnaryProcedure proc) {
        foreach(new IteratorToGeneratorAdapter(iter), proc);
    }

    /**
     * {@link UnaryProcedure#run Apply} the given {@link UnaryProcedure
     * UnaryProcedure} to each element in the given {@link Generator}.
     */
    public static final void foreach(Generator gen, UnaryProcedure proc) {
        gen.run(proc);
    }

    /**
     * Equivalent to <code>{@link #inject(Generator,Object,BinaryFunction) inject}(new {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter),seed,func)</code>.
     */
    public static final Object inject(Iterator iter, Object seed, BinaryFunction func) {
        return inject(new IteratorToGeneratorAdapter(iter), seed, func);
    }

    /**
     * {@link BinaryFunction#evaluate Evaluate} the pair <i>( previousResult,
     * element )</i> for each element in the given {@link Generator} where
     * previousResult is initially <i>seed</i>, and thereafter the result of the
     * evaluation of the previous element in the iterator. Returns the result
     * of the final evaluation.
     *
     * <p>
     * In code:
     * <pre>
     * while(iter.hasNext()) {
     *   seed = func.evaluate(seed,iter.next());
     * }
     * return seed;
     * </pre>
     */
    public static final Object inject(Generator gen, final Object seed, final BinaryFunction func) {
        final Object[] result = new Object[1];
        result[0] = seed;

        gen.run(new UnaryProcedure() {
            public void run(Object obj) {
                result[0] = func.evaluate(result[0], obj);
            }
        });

        return result[0];
    }

    /**
     * Returns an {@link Iterator} that will only return elements that DO
     * NOT match the given predicate.
     */
    public static Iterator reject(Iterator iter, UnaryPredicate pred) {
        return FilteredIterator.filter(iter,UnaryNot.not(pred));
    }

    /**
     * Returns a {@link Generator} that will only "generate" elements that DO
     * NOT match the given predicate.
     */
    public static Generator reject(final Generator gen, final UnaryPredicate pred) {
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new UnaryProcedure() {
                    public void run(Object obj) {
                        if (!pred.test(obj)) {
                            proc.run(obj);
                        }
                    }
                });
            }
        };
    }

    /**
     * Returns an {@link Iterator} that will only return elements that DO
     * match the given predicate.
     */
    public static final Iterator select(Iterator iter, UnaryPredicate pred) {
        return FilteredIterator.filter(iter,pred);
    }

    /**
     * Returns a {@link Generator} that will only "generate" elements that DO
     * match the given predicate.
     */
    public static final Generator select(final Generator gen, final UnaryPredicate pred) {
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new UnaryProcedure() {
                    public void run(Object obj) {
                        if (pred.test(obj)) {
                            proc.run(obj);
                        }
                    }
                });
            }
        };
    }

    /**
     * Equivalent to 
     * <code>{@link #reject(Iterator,UnaryPredicate) reject}(iter,pred)</code>.
     */
    public static final Iterator until(final Iterator iter, final UnaryPredicate pred) {
        return reject(iter,pred);
    }

    /**
     * Equivalent to 
     * <code>{@link #reject(Generator,UnaryPredicate) reject}(gen,pred)</code>.
     */
    public static final Generator until(final Generator gen, final UnaryPredicate pred) {
        return reject(gen,pred);
        // here's the old version of this code
        // reject(gen,pred) doesn't call stop()
        // like this version does.
        // should reject call stop?  
        // is the stop call signficant here?
        /*
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new UnaryProcedure() {
                    public void run(Object obj) {
                        if (pred.test(obj)) {
                            stop();
                        } else {
                            proc.run(obj);
                        }
                    }
                });
            }
        };
        */
    }

    /**
     * Tail recursion for {@link Function functions}. If the {@link Function}
     * returns another function of the same type as the original, that function
     * is executed. Functions are executed until a non function value or a
     * function of a different type is returned. See {@link
     * org.apache.commons.functor.util.BinarySearch} for an example of how might
     * be used.
     */
    public static final Object recurse(Function function) {
        Object result = null;
        Class recursiveFunctionClass = function.getClass();

        // if the function returns another function, execute it. stop executing
        // when the function doesn't return another function of the same type.
        while(true) {
            result = function.evaluate();
            if(recursiveFunctionClass.isInstance(result)) {
                function = (Function)result;
                continue;
            } else {
                break;
            }
        }

        return result;
    }
}