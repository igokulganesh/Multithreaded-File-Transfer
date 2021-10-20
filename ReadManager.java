import java.io.*;
import java.util.*; 
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadManager
{
	ArrayList<Reader> readers ; 
	String[] fileNames = new String[10] ;
	BufferQueue input;
	BufferQueue output;

	final int READ_SIZE = 10 ; 
	int currentIndex;
	int offset;
	int seqNo;
	int size;
	int threadCount; 
	int remainingBytes ;
	
	private final Lock rmlock = new ReentrantLock(); 

	ReadManager(String fname, int thc, BufferQueue inp, BufferQueue out)
	{
		currentIndex = 0 ;
		fileNames[currentIndex] = (fname); 
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

		if(offset == 0)
		{
			//currentIndex++;
			try
			{
				File file = new File(fileNames[currentIndex]);
				remainingBytes = (int)file.length();		
			}
			catch(Exception e)
			{
				currentIndex-- ;   
				buf.init(fileNames[currentIndex], Buffer.MsgType.END, seqNo++, currentIndex);
				offset = 0 ;
				rmlock.unlock();
				return buf ; 
			}	

		//	buf.init(fileNames[currentIndex], Buffer.MsgType.FILEOPEN, seqNo++);
		//	offset = 0 ;  
		//	rmlock.unlock();
		//	return buf ; 
		}
		/*
		if(remainingBytes == 0)
		{
			buf.init(fileNames[currentIndex], Buffer.MsgType.FILECLOSE, seqNo++, currentIndex);
			offset = -1 ;
			currentIndex++ ;   
			rmlock.unlock();
			return buf ; 
		} 
		*/

		if (size > remainingBytes)
			size = remainingBytes ;

		buf.init(offset, size, seqNo, currentIndex);
		offset += size;
		seqNo++;
		remainingBytes -= size ;
		if (remainingBytes == 0)
		{
			currentIndex++;
			offset = 0;
		}
		rmlock.unlock();
		return buf ;
	}	

	IO createFileObj(int index) throws IOException
	{
		IO file = new FileIO();
		file.open(fileNames[index], "r");
		return file;
	}
	
	void putBuffer(Buffer buf)
	{ 
		output.push(buf);
	}

	void start()
	{
		readers = new ArrayList<Reader>(); 
		
		for(int i = 0 ; i < threadCount ; i++)
		{
			Reader r = new Reader(this); 
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