package com.zyxist;

import com.zyxist.chuck.ChuckNorrisException;

public class Extra {
	public static void main(String[] args) {
		try {
			throw new ChuckNorrisException();
		} catch(Throwable thr) {
			ChuckNorrisException ex = (ChuckNorrisException) thr;
			System.err.println("I've caught "+ex.getMessage());
		}
	}
}
