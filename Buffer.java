public class Buffer implements java.io.Serializable
{
	private static final long serialVersionUID = 987654321;

	public enum MsgType { INVALID, DATA, FILEOPEN, FILECLOSE, END };   

	static int BUFFER_SIZE = 12; // 1024 * 1024 * 16 ; 
	
	int fileIndex;
	int offset ;
	int size ;
	int seqNo ;
	MsgType type; // 1 - data, 2 - new file open, 3 - close the file 
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

/*	private void writeObject(ObjectOutputStream out) throws IOException 
	{	

	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException 
	{

	}*/
}