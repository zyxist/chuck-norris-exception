package com.zyxist.chuck;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Why needed: I keep this logic in a separate class to avoid problems
 * with instantiating ShutdownThread, after messing with the class loaders.
 */
public class RoundhouseKick extends Throwable { // note that after conversion, we no longer extend 'Exception', avoiding 'catch(Exception)' clauses.
	private boolean roundhouseKicked = false;
	
	public RoundhouseKick(String msg) {
		super(msg);
	}
	
	// The methods below work like the original, but only in the uncaught exception
	// handler. In any other context, they rethrow our exception back. This is useful
	// for escaping the 'catch(Throwable)' construct, which we can't avoid.
	
	@Override
	public String getMessage() {
		if (!isWithinUndeclaredThrowableHandler()) {
			throwAsUnchecked(this);
		}
		roundhouseKicked = true;
		return super.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		if (!isWithinUndeclaredThrowableHandler()) {
			throwAsUnchecked(this);
		}
		roundhouseKicked = true;
		return super.getStackTrace();
	}

	@Override
	public void printStackTrace() {
		if (!isWithinUndeclaredThrowableHandler()) {
			throwAsUnchecked(this);
		}
		roundhouseKicked = true;
		super.printStackTrace();
	}
	
	// To check if we are in the uncaught exception handler, we create a fake exception just
	// to extract the stacktrace from it and examine it.
	
	private boolean isWithinUndeclaredThrowableHandler() {
		RuntimeException inspector = new RuntimeException("Stacktrace inspector");
		for (StackTraceElement element: inspector.getStackTrace()) {
			if (element.getClassName().equals("java.lang.Thread") && element.getMethodName().equals("dispatchUncaughtException")) {
				return true;
			}
		}
		return false;
	}
	
	// Java Virtual Machine does not know the concept of 'checked' exceptions. They are just a compile-time
	// check. With a little help of generics, we can trick the Java compiler and force it to generate a code
	// that throws checked exception as an unchecked exception thanks to the type erasure.
	
	static void throwAsUnchecked(Throwable exception) {
		RoundhouseKick.<RuntimeException>throwChuckedException(exception);
	}
	
	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void throwChuckedException(Throwable e) throws E {
		throw (E) e;
	}
	
	// Last line of defense - the programmer used 'catch(Throwable)' and did nothing inside it.
	// To escape from this situation, we do two things:
	//  1. start a thread that kills JVM after 100 ms (penalty for ignoring exceptions)
	//  2. install a shutdown hook that re-throws our original exception (part of this code is in ChuckNorrisException).
	// Because of the classloader tricks, we must do everything through the reflection to avoid
	// compilation problems.
	//
	// The 'roundhouseKicked' flag is a signal for us that our exception has been handled and we don't have to
	// kill JVM.
	
	private void installShutdownHook(Class<?> shutdownHookClass) {
		try {
			Thread thr = (Thread) shutdownHookClass.getConstructor(String.class, Throwable.class).newInstance(Thread.currentThread().getName(), this);
			Runtime.getRuntime().addShutdownHook(thr);
		} catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			System.err.println("Oh cr44p");
		}
	}
	
	public static class ShutdownThread extends Thread {
		private final Throwable hackedException;
		
		public ShutdownThread(String originalThreadName, Throwable hackedException) {
			super(originalThreadName);
			this.hackedException = hackedException;
		}
		
		@Override
		public void run() {
			try {
				Field roundhouseKickedField = hackedException.getClass().getDeclaredField("roundhouseKicked");
				roundhouseKickedField.setAccessible(true);
				boolean value = (Boolean) roundhouseKickedField.get(hackedException);
				if (!value) {
					throwAsUnchecked(hackedException);
				}
			} catch(NoSuchFieldException | SecurityException | IllegalAccessException ex) {
				System.err.println("Oh sheet");
			}
		}
	}
}
