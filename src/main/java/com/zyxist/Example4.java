package com.zyxist;

import com.zyxist.chuck.ChuckNorrisException;

public class Example4 {
	public static void main(String[] args) {
		try {
			throw new ChuckNorrisException();
		} catch(Throwable thr) {
			Exception ex = (Exception) thr;
			System.err.println("I've caught "+ex.getMessage());
		}
	}
}
