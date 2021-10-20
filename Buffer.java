public class Buffer
{
	static int BUFFER_SIZE = 12; // 1024 * 1024 * 16
	static long AUTO_INC = 0 ; 
	String fileName;
	long offset;
	long size;
	long seqNo;
	Byte buffer[];
	int type; // 1 - data, 2 - new file open, 3 - close the file 

	public Buffer()
	{
		seqNo = AUTO_INC++ ;
		buffer = new Byte[BUFFER_SIZE];
	}

	public Buffer(String fname, long off, long sz) 
	{
		fileName = fname;
		offset = off;
		size = sz;
		seqNo = AUTO_INC++ ;
	}
}