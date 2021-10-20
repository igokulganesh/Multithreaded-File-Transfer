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
	boolean isSerialize ;

 
	private final Lock aLock = new ReentrantLock(); 
	private final Condition bufferEmpty = aLock.newCondition();

	BufferQueue(int capacity, boolean flag)
	{ 
		this.pq = new PriorityQueue<Buffer>(capacity, new BufferComparator()); 
		this.prev = 0 ;
		this.isSerialize = flag ; 
	}

	BufferQueue()
	{
		this(100, true); 
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
			if(!isSerialize)
			{
				while(pq.isEmpty())
					bufferEmpty.await(); 
				return pq.poll(); 
			}
			
			while(pq.isEmpty() || prev != pq.peek().seqNo)
				bufferEmpty.await(); 

			Buffer top = pq.poll() ;
			prev++ ; 
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