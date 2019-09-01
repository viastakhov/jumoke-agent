package jumoke;

import java.util.Arrays;
import java.util.List;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;


/**
 * WIN32_FIND_DATA java class implementation for WinAPI
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.0
 */
public class WIN32_FIND_DATA extends Structure {
	public WIN32_FIND_DATA()
    {
		super();
    }
	
	public WIN32_FIND_DATA(Pointer p)
    {
		super(p);
    }

	public int    dwFileAttributes;
	public FILETIME ftCreationTime;
	public FILETIME ftLastAccessTime;
	public FILETIME ftLastWriteTime;
	public int    nFileSizeHigh;
	public int    nFileSizeLow;
	public int    dwReserved0;
	public int    dwReserved1;
	public byte[] cFileName = new byte[com.sun.jna.platform.win32.WinDef.MAX_PATH];
	public byte[] cAlternateFileName = new byte[14];

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] { 
											"dwFileAttributes", 
											"ftCreationTime", 
											"ftLastAccessTime", 
											"ftLastWriteTime",
											"nFileSizeHigh", 
											"nFileSizeLow", 
											"dwReserved0", 
											"dwReserved1", 
											"cFileName", 
											"cAlternateFileName"
											}); 
	}
}