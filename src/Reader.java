public class Reader extends Thread 
{
	ReadManager readManger ; 
	IO file ;
	int fileIndex;

    Reader (ReadManager rm)
	{
		file = null;
		readManger = rm ;
		fileIndex = -1;
	}
	
	@Override 
	public void run() 
	{
		Logger.Debug("Reader Thread started");
		Buffer buf = readManger.getNextBuffer();
		
		while(buf != null)
		{
			try
			{
				if(buf.fileIndex != fileIndex)
				{
					fileIndex = buf.fileIndex;
					if (file == null)
					{
						file = readManger.CreateIO(fileIndex);
					}
				}

				if(file == null)
					break ; 

				if (readManger.mode == FileTransfer.Type.SOCKET)
				{
					file.read(buf);
				}
				else
				{
					if(file != null && buf.type == Buffer.MsgType.DATA)
					{
						file.read(buf);
					}	
				}
				
				readManger.putBuffer(buf);
					
				if(buf.type == Buffer.MsgType.END)
				{
					if(readManger.mode == FileTransfer.Type.SOCKET)
					{
						// Send the Last Acknowledgement to Sender
						// file.write(buf);
						//file.flush();
						file.close();
						buf = readManger.getNextBuffer();
					}
				
					break ; 
				}
				buf = readManger.getNextBuffer();
			}
			catch (Exception e)
			{
				Logger.Print("READER Error");
				Logger.Debug(e);
				break ; 
			}
		}

		Logger.Debug("Reader Thread completed");
	}
}