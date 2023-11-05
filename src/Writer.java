class Writer extends Thread 
{
	WriteManager writeManager;
	IO file;
	int fileIndex;

    Writer(WriteManager wm)
	{
		file = null;
		writeManager = wm ;
		fileIndex = -1;
	}

	@Override 
	public void run() 
	{
		Logger.Debug("Writer Thread started");
		Buffer buf = writeManager.getNextBuffer();
		
		while(buf != null)
		{
			try
			{
				if(buf.fileIndex != fileIndex)
				{
					fileIndex = buf.fileIndex;
					if(file == null)
					{
						file = writeManager.CreateIO(fileIndex);
					}
				}

				if(file == null)
					break ; 
				
				if(buf.type == Buffer.MsgType.DATA)
					file.write(buf);
				
				if(buf.type == Buffer.MsgType.END)
				{	
					if (writeManager.mode.equals("SOCKET"))
					{
						file.write(buf);
						//file.close();
					}
					buf.seqNo++;
					writeManager.input.push(buf);
					break ;
				}
				
				buf = null;
				writeManager.putBuffer(new Buffer());
				//writeManager.putBuffer(buf);
				buf = writeManager.getNextBuffer();
			}
			catch(Exception e)
			{
				Logger.Print("write Error");
				e.printStackTrace();
				break ; 
			}		
		}

		Logger.Debug("Writer Thread completed");
	}
}

// Input -> FIleINput, StreamINput [open, close, read]
// Output -> FileOutput, streamOutput [open, close, write]
