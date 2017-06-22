package com.zyxist;

import com.zyxist.chuck.ChuckNorrisException;

public class Example5 {
	public static void main(String[] args) throws InterruptedException {
		try {
			throw new ChuckNorrisException();
		} catch(Throwable thr) {
		}
		System.out.println("Buahahaha!");
		Thread.sleep(10000);
		System.out.println("Still alive!");
		Thread.sleep(10000);
		System.out.println("Still alive! #2");
	}
}
