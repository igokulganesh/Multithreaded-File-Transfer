import java.io.*;
import java.util.*; 
import java.util.concurrent.locks.*;

public class ReadManager
{
	private final Lock rmlock = new ReentrantLock(); 
	private final int READ_SIZE = FileTransfer.BUFFER_SIZE ;

	ArrayList<Reader> readers = new ArrayList<Reader>();  ; // Thread 
	ArrayList<String> fileNames = new ArrayList<String>(); // filenames 

	BufferQueue input;
	BufferQueue output;

	int currentIndex;
	int offset;
	int seqNo;
	int size;
	int threadCount;
	int remainingBytes;

	int port ;
	String mode ;
	
	long startTime ; 

	ReadManager(String mode, String port, String fname, int thc, BufferQueue inp, BufferQueue out)
	{

		this.mode = mode ; 
		this.port = Integer.parseInt(port);   

		currentIndex = -1 ;
		fileNames.add(fname); 
		offset = 0 ; 
		size = READ_SIZE ; 
		seqNo = 0;
		threadCount = thc ; 

		input = inp ; 
		output = out ;
	}

	Buffer getNextBuffer()
	{
		rmlock.lock();
		Buffer buf = input.pop();

		if (mode == "SOCKET")
		{
			rmlock.unlock();
			return buf;
		}
		if(offset == 0)
		{	
			currentIndex++ ; 
			if(currentIndex >= fileNames.size()) // All files Completed 
			{
				buf.init("", Buffer.MsgType.END, seqNo++, currentIndex);
				rmlock.unlock();
				return buf ; 
			}
			else // next files 
			{
				File file = new File(fileNames.get(currentIndex));
				size = READ_SIZE;
				remainingBytes = (int)file.length();	
				Logger.Print("File Name : " + fileNames.get(currentIndex));
				Logger.Print("File Size : " + (remainingBytes / ( 1024 * 1024)) + "MB" );
				startTime = System.nanoTime();
			}
		}

		if (size > remainingBytes)
			size = remainingBytes ;

		buf.init(offset, size, seqNo, currentIndex);
		offset += size;
		seqNo++;
		remainingBytes -= size ;
		
		if (remainingBytes == 0)
		{
			long endTime = System.nanoTime(); 
		 	double elapsedTime = (double)(endTime - startTime)/1000000000 ; 
			Logger.Print("Time Taken : " + elapsedTime + " Seconds");
			offset = 0;
		}
		
		
		Logger.Debug("Remaining : " + remainingBytes);
		rmlock.unlock();
		return buf ;
	}	

	IO CreateIO(int index) throws IOException
	{
		IO file = null ; 

		if(index >= fileNames.size())
			return file;
		
		if(mode.equals("FILE"))
		{	
			file = new FileIO();
			file.open(fileNames.get(index), "r");
		}
		else if(mode.equals("SOCKET"))
		{
			file = new SocketIO(port); 
			file.open(null, "RECIEVER"); 
		}
		else
		{
			Logger.Print("Invalid Input");
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
		Reader r ; 
		for(int i = 0 ; i < threadCount ; i++)
		{
			r = new Reader(this); 
			readers.add(r); 
			r.start();
		}
	}

	void join()
	{
		for(Reader r : readers)
		{
			try 
			{
				r.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
		}
	}
}