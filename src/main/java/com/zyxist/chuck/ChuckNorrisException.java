package com.zyxist.chuck;

import static com.zyxist.chuck.RoundhouseKick.throwAsUnchecked;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * Uncatchable exception.
 */
public class ChuckNorrisException extends Exception {
	public ChuckNorrisException() {
		Throwable uncatchable = null;
		try {
			Class<?> hackedChuck = rewriteWithDifferentClassLoader();
			uncatchable = instantiateHackedClass(hackedChuck);
			uncatchable.setStackTrace(this.getStackTrace());
			callInstallShutdownHook(hackedChuck, uncatchable);
		} catch(Exception exception) {
			throw new RuntimeException("Chucking didn't work: " + exception.getMessage(), exception);
		}
		dontLetItLiveTooMuch(uncatchable);
		throwAsUnchecked(uncatchable);
	}
	
	private Class<?> rewriteWithDifferentClassLoader() throws Exception {
		ClassLoader cl = new ClassLoader(){};

		ClassPool pool = ClassPool.getDefault();
		CtClass chckCl = pool.get(RoundhouseKick.class.getCanonicalName());
		// change the class name to ChuckNorrisException ;)
		chckCl.setName(this.getClass().getCanonicalName()); 
		return pool.toClass(chckCl, cl, Class.class.getProtectionDomain());
	}
	
	private Throwable instantiateHackedClass(Class<?> hackedChuck) throws Exception {
		Constructor ctor = hackedChuck.getDeclaredConstructor(String.class);
		ctor.setAccessible(true);
		return (Throwable) ctor.newInstance("I'm uncatchable!");
	}
	
	private void callInstallShutdownHook(Class<?> hackedChuck, Throwable uncatchable) throws Exception {
		Method shutdownHookInstaller = hackedChuck.getDeclaredMethod("installShutdownHook", Class.class);
		shutdownHookInstaller.setAccessible(true);
		shutdownHookInstaller.invoke(uncatchable, RoundhouseKick.ShutdownThread.class);
	}
	
	private void dontLetItLiveTooMuch(final Throwable uncatchable) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
					try {
						Field roundhouseKickedField = uncatchable.getClass().getDeclaredField("roundhouseKicked");
						roundhouseKickedField.setAccessible(true);
						boolean value = (Boolean) roundhouseKickedField.get(uncatchable);
						if (!value) {
							System.exit(0);
						}
					} catch(NoSuchFieldException | SecurityException | IllegalAccessException ex) {
						System.err.println("Oh sheet");
					}
				} catch(InterruptedException exception) {
					System.exit(0);
				}
			}
		}.start();
	}
}
