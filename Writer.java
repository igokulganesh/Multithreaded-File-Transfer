class Writer extends Thread 
{
	WriteManager writeManager;
	IO file ; 
	int fileIndex;
    Writer (WriteManager wm)
	{
		file = null;
		writeManager = wm ;
		fileIndex = -1;
	}

	@Override 
	public void run() 
	{
		System.out.println("Writer Thread started");

		Buffer buf = writeManager.getNextBuffer();
		while(buf != null)
		{
			try
			{
				if(buf.fileIndex != fileIndex)
				{
					fileIndex = buf.fileIndex;
					if (file != null)
						file.close();					
					file = writeManager.createFileObj(fileIndex);
				}
				
				if (buf.type == Buffer.MsgType.DATA)
					file.write(buf);
				
				writeManager.putBuffer(buf);
				buf = writeManager.getNextBuffer();
			}
			catch(Exception e)
			{
				System.out.println("write Error");
				e.printStackTrace();
				break ; 
			}
			
			
		}

		System.out.println("Writer Thread completed");
	}
}

// Input -> FIleINput, StreamINput [open, close, read]
// Output -> FileOutput, streamOutput [open, close, write]




// 
