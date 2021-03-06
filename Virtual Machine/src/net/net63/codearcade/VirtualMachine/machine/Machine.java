package net.net63.codearcade.VirtualMachine.machine;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

public class Machine {
	
	private Memory memory;
	private CPU cpu;
	
	private BufferedImage videoBuffer;
	
	private final int mask = 0b11;
	
	private int clockDeltaTime;
	private boolean justUpdated;
	
	public boolean[] keys;
	private boolean keyChanged;
	
	private JTextArea logText;
	
	private float clockTime;
	
	public Machine(JTextArea logText){
		this.logText = logText;
		
		videoBuffer = new BufferedImage(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		
		memory = new Memory(Constants.MEMORY_SIZE, logText);
		cpu = new CPU(memory, logText);
		
		clockDeltaTime = 0;
		
		keys = new boolean[300];
		
		clockTime = Constants.CLOCK_TIME;
	}
	
	public void update(int deltaTime){
		justUpdated = false;
		
		if(clockDeltaTime > clockTime){
			stepInstruction();
		}
		
		clockDeltaTime += deltaTime;
	}
	
	public void stepInstruction(){
		justUpdated = true;
		
		updateInputBuffers();
		cpu.stepInstruction();
		updateOutputBuffers();
		
		logText.setText(logText.getText() + "\n");
		
		clockDeltaTime = 0;
	}
	
	private void updateInputBuffers(){
		if(keyChanged){
			keyChanged = false;
			
			for(int i = 0; i < keys.length; i++){
				memory.setByte(Constants.SEGMENTS.KEYBOARD.getAddress() + i, (keys[i]) ? 0xFF: 0x00);
			}
		}
	}
	
	private void updateOutputBuffers(){
		int[] pixels = memory.getLength(Constants.SEGMENTS.VIDEO.getAddress(), Constants.SEGMENTS.VIDEO.getLength());
		
		for(int i = 0; i < pixels.length; i++){
			int pixel = pixels[i];			
			int r,g,b,a;
			
			r = pixel & mask;
			g = (pixel >> 2) & mask;
			b = (pixel >> 4) & mask;
			a = (pixel >> 6) & mask;
			
			r = Constants.COLOR_VALUES[r];
			g = Constants.COLOR_VALUES[g];
			b = Constants.COLOR_VALUES[b];
			a = Constants.COLOR_VALUES[a];
			
			videoBuffer.setRGB((i % Constants.VIDEO_WIDTH), (int) (i / Constants.VIDEO_HEIGHT), (new Color(r, g, b, a)).getRGB());
		}
		
		videoBuffer.flush();
	}
	
	public void loadCode(int[] code) {
		memory.setLength(Constants.SEGMENTS.CODE.getAddress(), code);
	}
	
	public BufferedImage getVideoBuffer(){
		return videoBuffer;
	}
	
	public boolean isUpdated(){
		return justUpdated;
	}
	
	public void keyPressed(int keycode){
		keys[keycode] = true;
		keyChanged = true;
	}
	
	public void keyReleased(int keycode){
		keys[keycode] = false;
		keyChanged = false;
	}

	public void setTableData(AbstractTableModel table) {
		
		for(int i = 0; i < table.getRowCount(); i++){
			int data = memory.getByte(i) & 0xFF;
			
			table.setValueAt(new Integer(i), i, 0);
			table.setValueAt(new Integer(data), i, 1);
			table.setValueAt("0x" + IntegerUtils.paddedHexString(data), i, 2);
			table.setValueAt("0b" + IntegerUtils.paddedBinaryString(data).substring(8, 16), i, 3);
		}
	}

	public float getClockTime() {
		return clockTime;
	}

	public void setClockTime(float clockTime) {
		this.clockTime = clockTime;
		
		clockDeltaTime = 0;
	}
	
	public int[] getRegisterValues(){
		return new int[]{cpu.getProgramCounter(), cpu.getAddressRegister(), cpu.getDataRegister()};
	}
}
