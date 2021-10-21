import java.io.* ; 
import java.net.*;

/* 
IO -> input and output classes
FileIO -> disk file, offset and size could be used . multiple writer threads 
StreamIO -> single Reader or writer thread. 
*/

public abstract class IO
{
	abstract void open(String fname, String mode) throws IOException;
	abstract void close() throws IOException;
	abstract int read(Buffer buf) throws IOException;
	abstract void write(Buffer buf) throws IOException;
}

class FileIO extends IO 
{ 
	RandomAccessFile file = null ; 

	FileIO()
	{}

	// mode would be "r" or "rw" 
	void open(String fname, String mode) throws IOException
	{
		file = new RandomAccessFile(fname, mode);
	}

	void close() throws IOException
	{
		file.close() ;
		file = null ;
	}

	int read(Buffer buf) throws IOException
	{
		file.seek(buf.offset);
		return file.read(buf.buffer, 0, buf.size); 
	}

	
	void write(Buffer buf) throws IOException
	{
		file.seek(buf.offset);
		file.write(buf.buffer, 0, buf.size); 
	}
}

class SocketIO extends IO
{
	private ServerSocket serverSocket = null ; // create a server
	private Socket socket = null ; 
	private DataOutputStream dataOutput = null ;  
	private DataInputStream dataInput = null ;
	private ObjectInputStream in = null ; 
	private ObjectOutputStream out = null ; 


	private int port ;  

	public SocketIO(int port)
	{
		this.port = port ; 
	}

	public void OpenSender(String serverName) 
	{
		try
		{
			socket = new Socket(serverName, port);
			System.out.println("Sender started on port "+ socket.getLocalPort()+".....");
			System.out.println("Connected to server "+ socket.getRemoteSocketAddress());
			
			dataOutput = new DataOutputStream(socket.getOutputStream());
			dataInput = new DataInputStream(socket.getInputStream());  

			out = new ObjectOutputStream(dataOutput);
			in = new ObjectInputStream(dataInput);

		}
		catch(IOException e)
		{
			System.out.println("Error : " + e.getMessage() );
			e.printStackTrace();
		}
	}

	public void OpenReceiver() 
	{
		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("Receiver started on port " + serverSocket.getLocalPort()+".....");
	 		System.out.println("Waiting for Sender.....");
	 		socket = serverSocket.accept();
	 		System.out.println(""+ socket.getRemoteSocketAddress() + " connected.....");

	 		dataOutput = new DataOutputStream(socket.getOutputStream());
			dataInput = new DataInputStream(socket.getInputStream());  

			out = new ObjectOutputStream(dataOutput);
			in = new ObjectInputStream(dataInput);
		}
		catch(IOException e)
		{
			System.out.println("Error : " + e.getMessage() );
			e.printStackTrace();
		}
	}

	void open(String ip, String mode) throws IOException
	{
		if(mode.equals("SENDER"))
		{
			OpenSender(ip);
		}
		else if(mode.equals("RECIEVER"))
		{
			OpenReceiver();
		}
		return ; 
	}

	void close() throws IOException
	{
		try{
			dataOutput.close();
			dataInput.close(); 
			socket.close();

			if(serverSocket != null)
			{
				serverSocket.close(); 
				System.out.println("Server Closed...");  
			}
			System.out.println("Server Disconnected...");  
		}
		catch(IOException e)
		{
			System.out.println("Server not closed properly"); 
			System.out.println("Error : " + e.getMessage() );
			e.printStackTrace();
		}  
	}

	// receiver
	int read(Buffer buf) throws IOException
	{
		try 
		{
			Buffer data = (Buffer)in.readObject();
			if (data.type == Buffer.MsgType.DATA)
				buf.init(data.offset, data.size, data.seqNo, data.fileIndex);
			else
				buf.init("", data.type, data.seqNo, data.fileIndex);
			System.arraycopy(data.buffer, 0, buf.buffer, 0, data.buffer.length);
			//System.out.println("Read the Seq from stream " + data.seqNo + " " + data.offset + " " +data.size); 
		//	out.write(data.seqNo);
		//	out.flush();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		//System.out.println("Read From the stream");
		return buf.size ; // dataInput.read(buf.buffer, 0, buf.size); 
	}

	// sender
	void write(Buffer buf) throws IOException
	{
		out.writeObject(buf);
		out.flush();
		//int ack = in.readInt();
		//System.out.println("Write into the stream - " + buf.seqNo + " " + buf.offset + " " + buf.size);
		// out.writeObject(buf.buffer);
		//dataOutput.write(buf.buffer, 0, buf.size);
	}
}

