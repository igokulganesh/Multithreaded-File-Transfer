public class FileTransfer
{
	public static void main(String[] args) 
	{
		int MAX_BUFFERS = 5 ; 
		BufferQueue freeQueue = new BufferQueue(100, false);
		BufferQueue writeQueue = new BufferQueue(100, true); 

		for(int i=0; i< MAX_BUFFERS; i++)
		{
			freeQueue.push(new Buffer());
		}

		ReadManager rm = new ReadManager(args[0], 2, freeQueue, writeQueue);
		WriteManager wm = new WriteManager(args[1], 2, writeQueue, freeQueue); 

		rm.start(); 
		wm.start(); 

		rm.join();
		wm.join(); 

		System.out.println("File Transfer Completed Successfully!");
	}
}