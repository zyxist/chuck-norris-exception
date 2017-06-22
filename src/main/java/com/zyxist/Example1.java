package com.zyxist;

import com.zyxist.chuck.ChuckNorrisException;

public class Example1 {
	public static void main(String[] args) {
		try {
			throw new ChuckNorrisException();
		} catch(ChuckNorrisException thr) {
			System.err.println("I've caught "+thr.getClass().getSimpleName());
		} finally {
			System.out.println("Finalization.");
		}
	}
}
