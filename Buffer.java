public class Buffer{
	static int BUFFER_SIZE 12; // 1024 * 1024 * 16
	String fileName;
	long Offset;
	long size;
	long seqNo;
	Byte buffer[];
	int type; // 1 - data, 2 - new file open, 3 - close the file 

	public Buffer()
	{
		seqNo = 0;
		buffer = new Byte[BUFFER_SIZE];
	}

	public initBuffer(String fname, long off, long sz, long sno) 
	{
		fileName = fname;
		offset = off;
		size = sz;
		seqNo = sno;
	}
}