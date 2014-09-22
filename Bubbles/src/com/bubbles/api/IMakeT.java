package com.bubbles.api;

/**
 * Makes an instance of the given type 
 * @param <T>	Type to instanciate. Must be instanciable
 */
public interface IMakeT<T extends Object> {
	/**
	 * Creates a new instance of type T
	 * @return	New instance of T
	 */
	T Make();
}