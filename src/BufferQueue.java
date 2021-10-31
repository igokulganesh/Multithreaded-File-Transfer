import java.util.*; 
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferQueue
{
	private	Queue<Buffer> que = new LinkedList<>();
	private final Lock aLock = new ReentrantLock(); 
	private final Condition bufferEmpty = aLock.newCondition();


	void push(Buffer val) 
	{
		aLock.lock();
		que.add(val);
		bufferEmpty.signalAll(); 
		aLock.unlock(); 
	}

	Buffer pop() 
	{
		aLock.lock(); 
		try
		{
			while(que.isEmpty())
				bufferEmpty.await(); 
			return que.poll(); 
		} 
		catch(InterruptedException e)
		{
			Logger.Debug(e);
			return null ;
		}
		finally 
		{ 
			aLock.unlock(); 
		}
	}

	Buffer top()
	{
		return que.peek();
	}

	boolean isEmpty()
	{
		return que.isEmpty();
	}
}