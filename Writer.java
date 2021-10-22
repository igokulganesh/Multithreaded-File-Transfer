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
					if (!writeManager.mode.equals("SOCKET"))
					{
						if (file != null)
							file.close();					
						file = writeManager.CreateIO(fileIndex);
					}
					else if (file == null)
					{
						file = writeManager.CreateIO(fileIndex);
					}
				}
				
				if (buf.type == Buffer.MsgType.DATA)
					file.write(buf);
				
				if (buf.type == Buffer.MsgType.END)
				{
					
					if (writeManager.mode.equals("SOCKET"))
					{
						// file.flush();
						file.write(buf);
						file.close();
					}
					buf.seqNo++;
					writeManager.input.push(buf);
					break ;
				}
				
				buf = null;
				writeManager.putBuffer(new Buffer());
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

