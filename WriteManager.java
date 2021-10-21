import java.io.*;
import java.util.*; 
import java.util.concurrent.locks.*;

public class WriteManager
{
	private final Lock rmlock = new ReentrantLock(); 
	private final int READ_SIZE = 10 ;

	ArrayList<Writer> writers = new ArrayList<Writer>(); // Thread 
	ArrayList<String> fileNames  = new ArrayList<String>(); // filenames 

	BufferQueue input;
	BufferQueue output;

	int currentIndex;
	long offset;
	long seqNo;
	long size;
	int threadCount; 
	int transferCompleted;

	String serverName = null; 
	int port ;
	String mode = null ;

	WriteManager(String mode, String ip, String port, String fname, int thc, BufferQueue inp, BufferQueue out)
	{

		this.mode = mode ; 
		this.serverName = ip ; 
		this.port = Integer.parseInt(port);

		currentIndex = 0 ;
		fileNames.add(fname); 
		offset = -1 ; 
		size = READ_SIZE ; 
		seqNo = 0;
		threadCount = thc ; 
		input = inp ; 
		output = out ;
		transferCompleted = 0;
	}

	Buffer getNextBuffer()
	{
		rmlock.lock();
		Buffer buf = input.pop();
/*
		if (buf.type == Buffer.MsgType.FILEOPEN)
		{
			buf.init(fileNames[currentIndex], Buffer.MsgType.FILEOPEN, buf.seqNo, currentIndex);
			currentIndex++;
		}
*/	
		if(buf.type == Buffer.MsgType.END)
		{
			transferCompleted = 0;
			//output.push(buf);
			//buf = null;
		}
		rmlock.unlock();

		return buf;
	}	

	IO CreateIO(int index) throws IOException
	{
		IO file = null ; 

		if(mode.equals("FILE"))
		{	
			file = new FileIO();
			file.open(fileNames.get(currentIndex), "rw");
		}
		else if(mode.equals("SOCKET"))
		{
			file = new SocketIO(port); 
			file.open(serverName, "SENDER");
		}
		else
		{
			System.out.println("Invalid Input");
			System.exit(0);
		}
		return file;
	}	

	void putBuffer(Buffer buf)
	{ 
		output.push(buf);
	}

	void start()
	{
		Writer w ; 
		for(int i = 0 ; i < threadCount ; i++)
		{
			w = new Writer(this); 
			writers.add(w);
			w.start();
		}
	}

	void join()
	{
		for(Writer w : writers)
		{
			try 
			{
				w.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
		}
	}
}