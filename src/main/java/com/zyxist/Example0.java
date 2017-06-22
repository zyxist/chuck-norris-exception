package com.zyxist;

import com.zyxist.other.FooException;

/**
 * This is how regular exceptions work.
 */
public class Example0 {
	
	public static void main(String args[]) {
		try {
			throw new FooException("Die!");
		} catch (FooException exception) {
			System.out.println("Caught exception: " + exception.getMessage());
		}
	}
}
