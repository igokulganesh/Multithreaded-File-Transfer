public class FileTransfer
{
	// Receiver = -R PORT TargetFile
	// Sender = -S IP PORT SourceFile

	static final int MAX_BUFFERS = 20 ; 
	static final int BUFFER_SIZE = 1024 * 16; 
	static int THREAD_COUNT = 3 ; 

	public static void main(String[] args)
	{	
		Logger.DEBUG = false ; 

		BufferQueue freeQueue = new BufferQueue(100, false);
		BufferQueue writeQueue = new BufferQueue(100, false);

		ReadManager rm = null ; // 1 = mode , 2 = port, 3 = filename, 4 = thread count  
		WriteManager wm = null ; // 1 = mode, 2 = ReceiverIPaddress, 3 = port, 4 = thread count   

		for(int i = 0 ; i < MAX_BUFFERS ; i++)
		{
			freeQueue.push(new Buffer());
		}

		if(args[0].equals("-R") && args.length == 3)
		{
			rm = new ReadManager("SOCKET", args[1], args[2] , 1, freeQueue, writeQueue); // reads from the Socket = RS
			wm = new WriteManager("FILE", null, args[1], args[2], THREAD_COUNT, writeQueue, freeQueue); // writes to the File = WF
		}
		else if(args[0].equals("-S") && args.length == 4)
		{	
			rm = new ReadManager("FILE", args[2], args[3], THREAD_COUNT, freeQueue, writeQueue); // reads from the file = RF
			wm = new WriteManager("SOCKET", args[1], args[2], args[3], 1, writeQueue, freeQueue); // writes to the Socket = WS
		}
		else if(args[0].equals("-F"))
		{	
			rm = new ReadManager("FILE", "-1", args[1], THREAD_COUNT, freeQueue, writeQueue);
			wm = new WriteManager("FILE", null, "-1", args[1], THREAD_COUNT, writeQueue, freeQueue); 
		}
		else
		{
			Logger.Print("Invalid Input Arguments!");
			Logger.Print("Expected Arguments list:");
			Logger.Print("For Receiver = -R PORT TargetFile");
			Logger.Print("For Sender = -S IP PORT SourceFile");
			return ;
		}

		rm.start(); 
		wm.start(); 

		rm.join();
		wm.join(); 

		Logger.Print("File Transfer Completed Successfully!");
	}
}