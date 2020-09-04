package convex.core.data;

import java.util.function.Function;

/**
 * Abstract base class for lists.
 * 
 * Lists are immutable sequences of values, with efficient access and change to
 * the head of the list. Lists are most importantly used for representing code
 * as data, in the fine tradition of Lisp.
 * 
 * For general manipulation of sequential data, vectors are recommended.
 * 
 * There are multiple possible implementations for different list types, but all
 * should conform to the general AList interface. We use an abstract base class
 * in preference to an interface because we control the hierarchy and it offers
 * some mild performance advantages.
 * 
 * General design goals: - Immutability - Optimised performance for front of
 * list (cons, first etc.) - Able to share vector implementations where
 * appropriate
 * 
 * @param <T> Type of list
 */
public abstract class AList<T> extends ASequence<T> {

	@Override
	public abstract AList<T> cons(T x);

	/**
	 * Adds an element to this list, in first position.
	 * 
	 * Returns a new list.
	 */
	@Override
	public abstract <R> AList<R> conj(R x);

	@Override
	public AList<T> empty() {
		return Lists.empty();
	}

	@Override
	public abstract <R> AList<R> map(Function<? super T, ? extends R> mapper);

	@Override
	public abstract AList<T> concat(ASequence<T> vals);

	@Override
	public abstract AList<T> assoc(long i, T value);
	
	

}
