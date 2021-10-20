import java.util.*; 
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BufferComparator implements Comparator<Buffer>
{
	public int compare(Buffer b1, Buffer b2) 
	{
		if (b1.seqNo > b2.seqNo)
    		return 1;
		else if ( b1.seqNo < b2.seqNo )
    		return -1;
		return 0;
	}
}

public class BufferQueue
{
	static int MAX_BUFFERS = 20; // 1024 * 1024 * 16
	PriorityQueue<Buffer> pq ;
	long prev ;
 
	private final Lock aLock = new ReentrantLock(); 
	private final Condition bufferEmpty = aLock.newCondition();

	BufferQueue(int capacity)
	{ 
		this.pq = new PriorityQueue<Buffer>(capacity, new BufferComparator()); 
		this.prev = 0 ;
	}

	BufferQueue()
	{
		this(100);	
	}

	void push(Buffer val) 
	{
		aLock.lock();
		try
		{
			if(pq.add(val))
				bufferEmpty.signalAll(); 
		}
		finally 
		{ 
			aLock.unlock(); 
		}
	}

	Buffer pop() 
	{
		aLock.lock(); 
		try 
		{ 
			while(pq.isEmpty() || prev != pq.peek().seqNo-1)
				bufferEmpty.await(); 

			Buffer top = pq.poll() ;
			prev = top.seqNo ; 
			return top ;  
		} 
		catch(InterruptedException e)
		{
			System.out.println("UnknownError");
			return new Buffer() ;
		}
		finally 
		{ 
			aLock.unlock(); 
		}
	}

	Buffer top()
	{
		return pq.peek();
	}

	boolean isEmpty()
	{
		return pq.isEmpty();
	}
}