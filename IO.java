import java.io.* ; 

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

