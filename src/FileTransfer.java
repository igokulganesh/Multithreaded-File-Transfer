import java.util.* ; 

public class FileTransfer
{
	// Receiver = -R PORT TargetFiles
	// Sender = -S IP PORT SourceFiles
	public enum Type { INVALID, SOCKET, FILE, };  

	static final int MAX_BUFFERS = 20 ; 
	static final int BUFFER_SIZE = 1024 * 1024 * 32 ; 
	static int THREAD_COUNT = 3 ; 

	public static void main(String[] args)
	{
		Logger.DEBUG = true ; 
		Logger.Debug("Threads : " + THREAD_COUNT);
		
		BufferQueue freeQueue = new BufferQueue();
		BufferQueue writeQueue = new BufferQueue();

		ReadManager rm = null ; // 1 = mode , 2 = port, 3 = filename, 4 = thread count  
		WriteManager wm = null ; // 1 = mode, 2 = ReceiverIPaddress, 3 = port, 4 = thread count   

		for(int i = 0 ; i < MAX_BUFFERS ; i++)
		{
			freeQueue.push(new Buffer());
		}

		if(args[0].equals("-R") && args.length >= 3)
		{
			rm = new ReadManager(Type.SOCKET, args[1], Arrays.copyOfRange(args, 2, args.length), 1, freeQueue, writeQueue); // reads from the Socket = RS
			wm = new WriteManager(Type.FILE, null, args[1], Arrays.copyOfRange(args, 2, args.length), THREAD_COUNT, writeQueue, freeQueue); // writes to the File = WF
		}
		else if(args[0].equals("-S") && args.length >= 4)
		{	
			rm = new ReadManager(Type.FILE, args[2], Arrays.copyOfRange(args, 3, args.length), THREAD_COUNT, freeQueue, writeQueue); // reads from the file = RF
			wm = new WriteManager(Type.SOCKET, args[1], args[2], Arrays.copyOfRange(args, 3, args.length), 1, writeQueue, freeQueue); // writes to the Socket = WS
		}
		else if(args[0].equals("-F"))
		{	
			rm = new ReadManager(Type.FILE, "-1", Arrays.copyOfRange(args, 1, args.length), THREAD_COUNT, freeQueue, writeQueue);
			wm = new WriteManager(Type.FILE, null, "-1", Arrays.copyOfRange(args, 1, args.length), THREAD_COUNT, writeQueue, freeQueue); 
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