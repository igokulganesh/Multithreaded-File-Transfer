import java.io.*;
import java.util.*; 
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WriteManager
{
	ArrayList<Writer> writers ; 
	String[] fileNames = new String[10] ;
	BufferQueue input;
	BufferQueue output;

	final int READ_SIZE = 10 ; 
	int currentIndex;
	long offset;
	long seqNo;
	long size;
	int threadCount; 
	int transferCompleted;
	private final Lock rmlock = new ReentrantLock(); 

	WriteManager(String fname, int thc, BufferQueue inp, BufferQueue out)
	{
		currentIndex = 0 ;
		fileNames[currentIndex] = (fname); 
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
			output.push(buf);
			buf = null;
		}
		rmlock.unlock();

		return buf;
	}	

	IO createFileObj(int index) throws IOException
	{
		IO file = new FileIO();
		file.open(fileNames[index], "rw");
		return file;
	}

	void putBuffer(Buffer buf)
	{ 
		output.push(buf);
	}

	void start()
	{
		writers = new ArrayList<Writer>(); 
		for(int i = 0 ; i < threadCount ; i++)
		{
			Writer w = new Writer(this); 
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