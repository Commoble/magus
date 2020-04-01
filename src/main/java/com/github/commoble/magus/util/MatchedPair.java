package com.github.commoble.magus.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** like a Pair, but both objects are the same type **/
public class MatchedPair<T>
{
	private final T left;
	private final T right;
	
	private MatchedPair(T left, T right)
	{
		this.left = left;
		this.right = right;
	}
	
	/** Returns a new MatchedPair containing the given objects **/
	public static <T> MatchedPair<T> of(T left, T right)
	{
		return new MatchedPair<>(left, right);
	}
	
	/** Returns a new MatchedPair whose two objects are the same object **/
	public static <T> MatchedPair<T> of(T both)
	{
		return MatchedPair.of(both, both);
	}
	
	/**
	 * Returns a new MatchedPair, calling the supplier to create both objects.
	 * The supplier will be called twice, allowing for seperate-but-equivalent objects
	 */
	public static <T> MatchedPair<T> of(Supplier<T> bothGetter)
	{
		return MatchedPair.of(bothGetter.get(), bothGetter.get());
	}
	
	/** Returns the first object this was constructed with **/
	public T getLeft()
	{
		return this.left;
	}
	
	/** Returns the second object this was constructed with **/
	public T getRight()
	{
		return this.right;
	}
	
	/** Returns a new matched pair by feeding the current values into the given function **/
	public <Result> MatchedPair<Result> map(Function<T, Result> mapper)
	{
		return MatchedPair.of(mapper.apply(this.getLeft()), mapper.apply(this.getRight()));
	}
	
	/** Returns a new matched pair by applying separate functions to this pair's values **/
	public <Result> MatchedPair<Result> map(Function<T, Result> leftMapper, Function<T, Result> rightMapper)
	{
		return MatchedPair.of(leftMapper.apply(this.getLeft()), rightMapper.apply(this.getRight()));
	}
	
	/** Applies the given consumer to this pair's values (first the left, then the right) **/
	public void forEach(Consumer<T> consumer)
	{
		consumer.accept(this.getLeft());
		consumer.accept(this.getRight());
	}
	
	/** Applies the given consumers to this pair's values (first the left, then the right) **/
	public void forEach(Consumer<T> leftConsumer, Consumer<T> rightConsumer)
	{
		leftConsumer.accept(this.getLeft());
		rightConsumer.accept(this.getRight());
	}
	
	/** Returns right if true, left if false **/
	public T decide(boolean isRight)
	{
		return isRight ? this.getRight() : this.getLeft();
	}
	
	/** Applies the consumer to the left values of this pair and the other pair, then applies the consumer to the right values **/
	public <Other> void consume(MatchedPair<Other> other, BiConsumer<T,Other> consumer)
	{
		consumer.accept(this.getLeft(), other.getLeft());
		consumer.accept(this.getRight(), other.getRight());
	}
	
	/** Applies the given function to both objects and returns the result **/
	public <Result> Result reduce(BiFunction<T,T, Result> reducer)
	{
		return reducer.apply(this.getLeft(), this.getRight());
	}
}
