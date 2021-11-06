import java.io.*;
import java.util.*; 
import java.util.concurrent.locks.*;

public class ReadManager
{
	private final Lock rmlock = new ReentrantLock(); 
	private final int BUFFER_SIZE = FileTransfer.BUFFER_SIZE ;

	ArrayList<Reader> readers = new ArrayList<Reader>();  // Thread 
	String [] fileNames  ; //= new ArrayList<String>(); // filenames 

	BufferQueue input;
	BufferQueue output;

	int currentIndex;
	int offset;
	int seqNo;
	int size;
	int fileSize; 
	int threadCount;
	int remainingBytes;

	int port ;
	FileTransfer.Type mode ;
	
	long startTime = 0; 

	ReadManager(FileTransfer.Type mode, String port, String [] files, int thc, BufferQueue inp, BufferQueue out)
	{

		this.mode = mode ; 
		this.port = Integer.parseInt(port);   

		currentIndex = -1 ;
		fileNames = files ; 
		offset = 0 ; 
		size = BUFFER_SIZE ; 
		seqNo = 0;
		threadCount = thc ; 

		input = inp ; 
		output = out ;
	}

	Buffer getNextBuffer()
	{
		rmlock.lock();
		Buffer buf = input.pop();

		if(mode == FileTransfer.Type.SOCKET) // Receiver 
		{
			rmlock.unlock();
			return buf;
		}
		
		// Sender
		if(offset == 0)
		{	
			if(startTime != 0)
			{
				long endTime = System.nanoTime(); 
		 		double elapsedTime = (double)(endTime - startTime)/1000000000 ; 
				Logger.Print("Time Taken to Upload: " + elapsedTime + " Seconds");
				double speed = fileSize/elapsedTime ; 
		 		Logger.Print("Upload Speed : " + speed  + " MBPS");
		 		startTime = 0 ; 
			}

			currentIndex++ ;
			if(currentIndex >= fileNames.length) // All files Completed 
			{
				buf.init("", Buffer.MsgType.END, seqNo++, currentIndex);
				rmlock.unlock();
				return buf ; 
			}
			else // next files 
			{
				File file = new File(fileNames[currentIndex]);
				size = BUFFER_SIZE;
				remainingBytes = (int)file.length();	

				// Send FileInfo here 



				// Calculate the time 
				fileSize = (remainingBytes / ( 1024 * 1024)) ; 
				Logger.Print("File " + currentIndex+1 + " : " + fileNames[currentIndex]);
				Logger.Print("File Size : " + fileSize + " MB" );
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
			offset = 0;
		
		// Logger.Debug("Remaining : " + remainingBytes);
		
		rmlock.unlock();
		return buf ;
	}	

	IO CreateIO(int index) throws IOException
	{
		IO file = null ; 

		if(index >= fileNames.length)
			return file;
		
		if(mode == FileTransfer.Type.FILE)
		{	
			file = new FileIO();
			file.open(fileNames[index], "r");
		}
		else if(mode == FileTransfer.Type.SOCKET)
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
			r.start();
			readers.add(r); 
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
				Logger.Debug(e);
			} 
		}
	}
}