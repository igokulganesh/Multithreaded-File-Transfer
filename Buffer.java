public class Buffer implements java.io.Serializable
{
	private static final long serialVersionUID = 987654321;
	static final int BUFFER_SIZE = FileTransfer.BUFFER_SIZE ; 

	public enum MsgType { INVALID, DATA, FILEOPEN, FILECLOSE, END };   

	int fileIndex ;
	int offset ;
	int size ;
	int seqNo ;
	MsgType type; 
	byte buffer[] ;	
		
	public Buffer()
	{
		seqNo = 0 ; 
		buffer = new byte[BUFFER_SIZE] ;
	}

	public void init(int off, int sz, int sno, int ind) 
	{
		offset = off;
		size = sz;
		seqNo = sno ;
		type = MsgType.DATA ; 
		fileIndex = ind;
	}

	public void init(String data, MsgType msg, int sno, int ind)
	{
		type = msg ; 
		seqNo = sno ;
		fileIndex = ind;
		if(type == MsgType.FILEOPEN)
		{
			System.arraycopy(data.getBytes(), 0, buffer, 0, data.length()); 
		}
	}

	protected void finalize() throws Throwable  
	{
		this.buffer = null ; 
		//System.out.println("GC : " + this.seqNo);
	}

/*	private void writeObject(ObjectOutputStream out) throws IOException 
	{	

	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException 
	{
		offset = out.readInt() ;
		size = out.readInt() ;
		seqNo = out.readInt() ;
		type = out. ; 
		fileIndex = ind;
	}*/
}