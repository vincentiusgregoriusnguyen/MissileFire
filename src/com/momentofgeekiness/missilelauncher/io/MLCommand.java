package com.momentofgeekiness.missilelauncher.io;

public enum MLCommand {
	LEFT("a"),
	RIGHT("d"),
	UP("w"),
	DOWN("x"),
	STOP("s"),
	FIRE("f");
	
	private String strCommand;
	
	private MLCommand(String strCommand) {
		this.strCommand = strCommand;
	}
	
	public String getCommand() {
		return strCommand;
	}
}
