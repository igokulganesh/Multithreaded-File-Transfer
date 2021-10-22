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
		System.out.println("Reader Thread started");
		Buffer buf = readManger.getNextBuffer();
		while(true)
		{
			try
			{
				if(buf.fileIndex != fileIndex)
				{
					fileIndex = buf.fileIndex;
					if (readManger.mode.equals("SOCKET"))
					{
						if (file != null)
							file.close();
						file = readManger.CreateIO(fileIndex);
					}
					else if (file == null)
					{
						file = readManger.CreateIO(fileIndex);
					}
				}
				/*
				if(buf.type == Buffer.MsgType.FILECLOSE)
				{
					file.close();
					file = null;
				}
				else*/
				if (readManger.mode.equals("SOCKET"))
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
					if(readManger.mode.equals("SOCKET"))
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
				System.out.println("READER Error");
				e.printStackTrace();
				break ; 
			}
		}

		System.out.println("Reader Thread completed");
	}
}