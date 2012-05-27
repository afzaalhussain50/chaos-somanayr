package vacuum.npcs.hooks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.server.Packet;

public class BogusSocket extends Socket {

	public byte[] currentPacket = null;
	public int off = 0;

	private Queue<byte[]> packets = new LinkedList<byte[]>();

	public Object lock = new Object();

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return new InputStream() {

			@Override
			public int read() throws IOException {
				//TODO
				/*if(currentPacket == null){
					synchronized (lock) {
						try {
							lock.wait();
						} catch (Exception e) {
							if(!Thread.interrupted())
								e.printStackTrace();
						}
					}
				}*/
				while(currentPacket == null);
				System.out.println("Thread awoken Current packet = " + currentPacket);
				int ret = (int)currentPacket[off++];
				if(off >= currentPacket.length){
					off = 0;
					currentPacket = packets.poll();
				}
				return ret;
			}
		};
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return new OutputStream() {
			
			StringBuilder buf = new StringBuilder();
			
			@Override
			public void write(int arg0) throws IOException {
				char c = (char)arg0;
				if(c == '\n'){
					System.out.println(buf.toString());
					buf.setLength(0);
				} else
					buf.append(c);
			}
		};
	}

	@Override
	public SocketAddress getRemoteSocketAddress() {
		return null;
	}
	
	public void queuePacket(Packet p){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Packet.a(p, new DataOutputStream(out));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.queuePacket(out.toByteArray());
	}
	
	public void queuePacket(int[] packet){
		byte[] buf = new byte[packet.length];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte)(packet[i] & 0xFF);
		}
		queuePacket(buf);
	}
	
	public void queuePacket(byte[] packet){
		if(packet == null || packet.length == 0)
			throw new IllegalArgumentException();
		synchronized (lock) {
			if(currentPacket == null){
				currentPacket = packet;
//				System.out.println("Unlocking thread, current packet = " + currentPacket);
//				lock.notify();
//				System.out.println("Thread unlocked, current packet = " + currentPacket);
			} else {
				packets.add(packet);
			}
		}
	}

}
