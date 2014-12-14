package net.net63.codearcade.VirtualMachine;

public class Constants {
	
	public static final int WORD_SIZE = 16;
	
	public static final int MEMORY_SIZE = (int) Math.pow(2, WORD_SIZE - 1);
	
	public static class SEGMENTS{
		public static final Segment CODE = new Segment(0, 2000);
		public static final Segment VIDEO = new Segment(CODE.getEndPoint(), 2000);
		public static final Segment DATA = new Segment(VIDEO.getEndPoint(), MEMORY_SIZE - VIDEO.getEndPoint() );
	
	}
	
	public static final float CLOCK_SPEED = 1f;
	public static final float CLOCK_TIME = 1000 / CLOCK_SPEED;
}
