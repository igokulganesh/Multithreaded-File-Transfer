public class Logger
{	
	static boolean DEBUG = false ; 
	
	static void Print(String val)
	{
		System.out.println(val);
	}

	static void Debug(String val)
	{
		if(DEBUG == true)
			System.out.println(val); 
	}
	
	static void Debug(Exception e)
	{
		if(DEBUG == true)
			e.printStackTrace();
		else
			Print("Unknown Error");
	}
}