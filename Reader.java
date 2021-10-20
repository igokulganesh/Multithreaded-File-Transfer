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
					if (file != null)
						file.close();
					file = readManger.createFileObj(fileIndex);
				}
				/*
				if(buf.type == Buffer.MsgType.FILECLOSE)
				{
					file.close();
					file = null;
				}
				else*/
				if(buf.type == Buffer.MsgType.END)
				{
					readManger.putBuffer(buf);
					break ; 
				} 
				else if(buf.type == Buffer.MsgType.DATA)
				{
					file.read(buf);
				} 
				readManger.putBuffer(buf);
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