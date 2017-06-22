package com.zyxist;

import com.zyxist.chuck.ChuckNorrisException;

public class Example3 {
	public static void main(String[] args) {
		try {
			throw new ChuckNorrisException();
		} catch(Throwable thr) {
			System.err.println("I've caught "+thr.getMessage());
		} finally {
			System.out.println("Finalization.");
		}
	}
}
