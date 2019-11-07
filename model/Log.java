package model;

public class Log {
	// 1 = quiet
	// 2 = errors
	// 3 = debug
	static int logLevel = 3; 
	
	
	public static void l(int level, String process, String message) {
		if (level <= logLevel) {
			System.out.println(process + ": " + message);
		}
		
	}
}
