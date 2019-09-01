package jumoke;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.OVERLAPPED;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;


/**
 * Win32 API native class implementation
 * 
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.4
 */
public class WinApi  {

	WinApi() {

	}

	public static interface Kernel32 extends StdCallLibrary {
		final Kernel32 instance = (Kernel32) Native.loadLibrary ("kernel32", Kernel32.class);

		boolean CloseHandle(HANDLE hObject);
		HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
		boolean ReadProcessMemory(HANDLE hProcess, Pointer lpBaseAddress, Structure lpBuffer, int  nSize, Pointer lpNumberOfBytesRead);
		Pointer VirtualAllocEx(HANDLE hProcess, Pointer lpAddress, int dwSize, int flAllocationType, int flProtect);
		boolean VirtualFreeEx(HANDLE hProcess, Pointer lpAddress, int dwSize, int dwFreeType);
		boolean WriteProcessMemory(HANDLE hProcess, Pointer lpBaseAddress, Structure lpBuffer, int nSize, Pointer lpNumberOfBytesWritten);
		int GetLastError();
		boolean DeleteFileA(String lpFileName);
		//boolean DeleteFile(String lpFileName); //doesn't exist at all
		//boolean DeleteFileW(String lpFileName); //doesn't work 
		HANDLE FindFirstFileA(String lpFileName, Structure lpFindFileData);
		boolean FindNextFileA(HANDLE hFindFile, Structure lpFindFileData);
		boolean FindClose(HANDLE hFindFile);
		HANDLE CreateFileA(String lpFileName, int dwDesiredAccess, int dwShareMode, SECURITY_ATTRIBUTES lpSecurityAttributes, int dwCreationDistribution, int dwFlagsAndAttributes, HANDLE hTemplateFile);
		boolean WriteFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToWrite, IntByReference lpNumberOfBytesWritten, OVERLAPPED lpOverlapped);  
		boolean ReadFile(HANDLE hFile, byte[] lpBuffer, int nNumberOfBytesToRead, IntByReference lpNumberOfBytesRead, OVERLAPPED lpOverlapped);
	}

	public interface User32 extends W32APIOptions {  
		User32 instance = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);  

		int ChangeDisplaySettings(Structure lpDevMode, int dwflags);
		boolean EnumDisplaySettings(String lpszDeviceName, int iModeNum, Structure lpDevMode);
		boolean OpenClipboard(HWND hWndNewOwner);
		boolean CloseClipboard();
		boolean EmptyClipboard();
		boolean ClientToScreen(HWND hWnd, Structure lpPoint);  
		int GetWindowThreadProcessId(HWND hWnd, IntByReference lpdwProcessId);
		HWND FindWindow(String lpClassName, String lpWindowName);
		LRESULT SendMessage(HWND hWnd, UINT Msg, WPARAM wParam, LPARAM lParam);
		//int WindowFromPoint(long x, long y); //- get handle only for parent window
		//int WindowFromPoint(long point); //- get handle for child control of parent window 
		//HWND WindowFromPoint(POINT Point ); //- get handle for child control of parent window
		HWND WindowFromPoint(long lPoint);
		boolean GetCursorPos(POINT lpPoint);
		boolean SetCursorPos(int X, int Y);

	}

	public static interface Gdi32 extends Library {
		final Gdi32 instance = (Gdi32) Native.loadLibrary ("gdi32", Gdi32.class);
	}

	
	public String readFile(String hFile, String lpBuffer, String nNumberOfBytesToRead, String lpNumberOfBytesRead, String lpOverlapped) {
		Jumoke.log.info(">>");
		HANDLE hFile_ = (HANDLE) Marshal.deserialize(hFile);
		byte[] lpBuffer_ = (byte[]) Marshal.deserialize(lpBuffer);
		int nNumberOfBytesToRead_ = (int) Marshal.deserialize(nNumberOfBytesToRead);
		int nNumberOfBytesRead_ = (int) Marshal.deserialize(lpNumberOfBytesRead);
		IntByReference lpNumberOfBytesRead_ = new IntByReference(nNumberOfBytesRead_);
		OVERLAPPED lpOverlapped_ = (OVERLAPPED) Marshal.deserialize(lpOverlapped);

		OVERLAPPED lpOverlapped_copy;

		if (lpOverlapped_ instanceof OVERLAPPED) {
			lpOverlapped_copy = new OVERLAPPED();
			lpOverlapped_copy.Internal = lpOverlapped_.Internal;
			lpOverlapped_copy.InternalHigh = lpOverlapped_.InternalHigh;
			lpOverlapped_copy.Offset = lpOverlapped_.Offset;
			lpOverlapped_copy.OffsetHigh = lpOverlapped_.OffsetHigh;
			lpOverlapped_copy.hEvent = lpOverlapped_.hEvent;
		} else {
			lpOverlapped_copy = lpOverlapped_;
		}

		boolean res = Kernel32.instance.ReadFile(hFile_, lpBuffer_, nNumberOfBytesToRead_, lpNumberOfBytesRead_, lpOverlapped_copy);

		@SuppressWarnings("unused")
		class ReadFile {
			boolean res;
			byte[] lpBuffer;
			int lpNumberOfBytesRead;
			OVERLAPPED lpOverlapped;
		}

		ReadFile rf = new ReadFile();
		rf.res = res;
		rf.lpBuffer = lpBuffer_;
		rf.lpNumberOfBytesRead = lpNumberOfBytesRead_.getValue();
		rf.lpOverlapped = lpOverlapped_copy;

		String result = Marshal.serialize(rf);

		Jumoke.log.info("<< readFile (" + hFile_ + ", " + lpBuffer_ + ", " + nNumberOfBytesToRead_ + ", " + rf.lpNumberOfBytesRead+ ", " + rf.lpOverlapped + ") -> " + res);
		return result;
	}

	public String writeFile(String hFile, String lpBuffer, String nNumberOfBytesToWrite, String lpNumberOfBytesWritten, String lpOverlapped) {
		Jumoke.log.info(">>");
		HANDLE hFile_ = (HANDLE) Marshal.deserialize(hFile);
		byte[] lpBuffer_ = (byte[]) Marshal.deserialize(lpBuffer);
		int nNumberOfBytesToWrite_ = (int) Marshal.deserialize(nNumberOfBytesToWrite);
		int nNumberOfBytesWritten_ = (int) Marshal.deserialize(lpNumberOfBytesWritten);
		IntByReference lpNumberOfBytesWritten_ = new IntByReference(nNumberOfBytesWritten_);
		OVERLAPPED lpOverlapped_ = (OVERLAPPED) Marshal.deserialize(lpOverlapped);

		OVERLAPPED lpOverlapped_copy;

		if (lpOverlapped_ instanceof OVERLAPPED) {
			lpOverlapped_copy = new OVERLAPPED();
			lpOverlapped_copy.Internal = lpOverlapped_.Internal;
			lpOverlapped_copy.InternalHigh = lpOverlapped_.InternalHigh;
			lpOverlapped_copy.Offset = lpOverlapped_.Offset;
			lpOverlapped_copy.OffsetHigh = lpOverlapped_.OffsetHigh;
			lpOverlapped_copy.hEvent = lpOverlapped_.hEvent;
		} else {
			lpOverlapped_copy = lpOverlapped_;
		}

		boolean res = Kernel32.instance.WriteFile(hFile_, lpBuffer_, nNumberOfBytesToWrite_, lpNumberOfBytesWritten_, lpOverlapped_copy);

		@SuppressWarnings("unused")
		class WriteFile {
			boolean res;
			int lpNumberOfBytesWritten;
			OVERLAPPED lpOverlapped;
		}

		WriteFile wf = new WriteFile();
		wf.res = res;
		wf.lpNumberOfBytesWritten = lpNumberOfBytesWritten_.getValue();
		wf.lpOverlapped = lpOverlapped_copy;

		String result = Marshal.serialize(wf);

		Jumoke.log.info("<< writeFile (" + hFile_ + ", " + lpBuffer_ + ", " + nNumberOfBytesToWrite_ + ", " + wf.lpNumberOfBytesWritten + ", " + wf.lpOverlapped + ") -> " + res);
		return result;
	}

	public String createFileA(String lpFileName, String dwDesiredAccess, String dwShareMode, String lpSecurityAttributes, String dwCreationDistribution, String dwFlagsAndAttributes, String hTemplateFile) {
		Jumoke.log.info(">>");
		String lpFileName_ = (String) Marshal.deserialize(lpFileName);
		int dwDesiredAccess_ = (int) Marshal.deserialize(dwDesiredAccess);
		int dwShareMode_ = (int) Marshal.deserialize(dwShareMode);
		SECURITY_ATTRIBUTES lpSecurityAttributes_ = (SECURITY_ATTRIBUTES) Marshal.deserialize(lpSecurityAttributes);
		int dwCreationDistribution_ = (int) Marshal.deserialize(dwCreationDistribution);
		int dwFlagsAndAttributes_ = (int) Marshal.deserialize(dwFlagsAndAttributes);
		HANDLE hTemplateFile_ = (HANDLE) Marshal.deserialize(hTemplateFile);


		SECURITY_ATTRIBUTES lpSecurityAttributes_copy;

		if (lpSecurityAttributes_ instanceof SECURITY_ATTRIBUTES) {
			lpSecurityAttributes_copy = new SECURITY_ATTRIBUTES();
			lpSecurityAttributes_copy.dwLength = lpSecurityAttributes_.dwLength;
			lpSecurityAttributes_copy.lpSecurityDescriptor = lpSecurityAttributes_.lpSecurityDescriptor;
			lpSecurityAttributes_copy.bInheritHandle = lpSecurityAttributes_.bInheritHandle;
		} else {
			lpSecurityAttributes_copy = lpSecurityAttributes_;
		}

		HANDLE res = Kernel32.instance.CreateFileA(lpFileName_, dwDesiredAccess_, dwShareMode_, lpSecurityAttributes_copy, dwCreationDistribution_, dwFlagsAndAttributes_, hTemplateFile_);
		String result = Marshal.serialize(res);
		Jumoke.log.info("<< createFileA (" + lpFileName_ + ", " + dwDesiredAccess_ + ", " + dwShareMode_ + ", " + lpSecurityAttributes_copy + ", " + dwCreationDistribution_+ ", " + dwFlagsAndAttributes_ + ", " + hTemplateFile_ + ") -> " + res);
		return result;
	}

	public String findClose(String hFindFile) {
		Jumoke.log.info(">>");
		HANDLE hFindFile_ = (HANDLE) Marshal.deserialize(hFindFile);
		boolean res = Kernel32.instance.FindClose(hFindFile_);
		String result = Marshal.serialize(res);
		Jumoke.log.info("<< findClose (" + hFindFile_+ ") -> " + res);
		return result;
	}

	public String findNextFileA(String hFindFile, String lpFindFileData) {
		Jumoke.log.info(">>");
		HANDLE hFindFile_ = (HANDLE) Marshal.deserialize(hFindFile);
		Structure lpFindFileData_ = (Structure) Marshal.deserialize(lpFindFileData);

		Structure lpFindFileData_copy;

		if (lpFindFileData_ instanceof WIN32_FIND_DATA) {
			lpFindFileData_copy = new WIN32_FIND_DATA();
			((WIN32_FIND_DATA)lpFindFileData_copy).dwFileAttributes = ((WIN32_FIND_DATA)lpFindFileData_).dwFileAttributes;
			((WIN32_FIND_DATA)lpFindFileData_copy).ftCreationTime = ((WIN32_FIND_DATA)lpFindFileData_).ftCreationTime;
			((WIN32_FIND_DATA)lpFindFileData_copy).ftLastAccessTime = ((WIN32_FIND_DATA)lpFindFileData_).ftLastAccessTime;
			((WIN32_FIND_DATA)lpFindFileData_copy).ftLastWriteTime = ((WIN32_FIND_DATA)lpFindFileData_).ftLastWriteTime;
			((WIN32_FIND_DATA)lpFindFileData_copy).nFileSizeHigh = ((WIN32_FIND_DATA)lpFindFileData_).nFileSizeHigh;
			((WIN32_FIND_DATA)lpFindFileData_copy).nFileSizeLow = ((WIN32_FIND_DATA)lpFindFileData_).nFileSizeLow;
			((WIN32_FIND_DATA)lpFindFileData_copy).dwReserved0 = ((WIN32_FIND_DATA)lpFindFileData_).dwReserved0;
			((WIN32_FIND_DATA)lpFindFileData_copy).dwReserved1 = ((WIN32_FIND_DATA)lpFindFileData_).dwReserved1;
			((WIN32_FIND_DATA)lpFindFileData_copy).cFileName = ((WIN32_FIND_DATA)lpFindFileData_).cFileName;
			((WIN32_FIND_DATA)lpFindFileData_copy).cAlternateFileName = ((WIN32_FIND_DATA)lpFindFileData_).cAlternateFileName;
		} else {
			lpFindFileData_copy = lpFindFileData_;
		}

		boolean res = Kernel32.instance.FindNextFileA(hFindFile_, lpFindFileData_copy);

		@SuppressWarnings("unused")
		class FindNextFileA {
			boolean res;
			Structure lpFindFileData;
		}

		FindNextFileA fnfa = new FindNextFileA();
		fnfa.res = res;
		fnfa.lpFindFileData = lpFindFileData_copy;

		String result = Marshal.serialize(fnfa);

		Jumoke.log.info("<< findNextFileA (" + hFindFile_ + ", " + lpFindFileData_copy + ") -> " + res);
		return result;
	}

	public String findFirstFileA(String lpFileName, String lpFindFileData) {
		Jumoke.log.info(">>");
		String lpFileName_ = (String) Marshal.deserialize(lpFileName);
		Structure lpFindFileData_ = (Structure) Marshal.deserialize(lpFindFileData);

		Structure lpFindFileData_copy;

		if (lpFindFileData_ instanceof WIN32_FIND_DATA) {
			lpFindFileData_copy = new WIN32_FIND_DATA();
			((WIN32_FIND_DATA)lpFindFileData_copy).dwFileAttributes = ((WIN32_FIND_DATA)lpFindFileData_).dwFileAttributes;
			((WIN32_FIND_DATA)lpFindFileData_copy).ftCreationTime = ((WIN32_FIND_DATA)lpFindFileData_).ftCreationTime;
			((WIN32_FIND_DATA)lpFindFileData_copy).ftLastAccessTime = ((WIN32_FIND_DATA)lpFindFileData_).ftLastAccessTime;
			((WIN32_FIND_DATA)lpFindFileData_copy).ftLastWriteTime = ((WIN32_FIND_DATA)lpFindFileData_).ftLastWriteTime;
			((WIN32_FIND_DATA)lpFindFileData_copy).nFileSizeHigh = ((WIN32_FIND_DATA)lpFindFileData_).nFileSizeHigh;
			((WIN32_FIND_DATA)lpFindFileData_copy).nFileSizeLow = ((WIN32_FIND_DATA)lpFindFileData_).nFileSizeLow;
			((WIN32_FIND_DATA)lpFindFileData_copy).dwReserved0 = ((WIN32_FIND_DATA)lpFindFileData_).dwReserved0;
			((WIN32_FIND_DATA)lpFindFileData_copy).dwReserved1 = ((WIN32_FIND_DATA)lpFindFileData_).dwReserved1;
			((WIN32_FIND_DATA)lpFindFileData_copy).cFileName = ((WIN32_FIND_DATA)lpFindFileData_).cFileName;
			((WIN32_FIND_DATA)lpFindFileData_copy).cAlternateFileName = ((WIN32_FIND_DATA)lpFindFileData_).cAlternateFileName;
		} else {
			lpFindFileData_copy = lpFindFileData_;
		}

		HANDLE res = Kernel32.instance.FindFirstFileA(lpFileName_, lpFindFileData_copy);

		@SuppressWarnings("unused")
		class FindFirstFileA {
			HANDLE res;
			Structure lpFindFileData;
		}

		FindFirstFileA fffa = new FindFirstFileA();
		fffa.res = res;
		fffa.lpFindFileData = lpFindFileData_copy;

		String result = Marshal.serialize(fffa);

		Jumoke.log.info("<< findFirstFileA (" + lpFileName_ + ", " + lpFindFileData_copy + ") -> " + res);
		return result;
	}

	public String deleteFileA(String lpFileName) {
		Jumoke.log.info(">>");
		String lpFileName_ = (String) Marshal.deserialize(lpFileName);
		boolean res = Kernel32.instance.DeleteFileA(lpFileName_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< deleteFileA (" + lpFileName_ + ") -> " + res);
		return result;
	}

	public String changeDisplaySettings(String lpDevMode, String dwflags) {
		Jumoke.log.info(">>");
		Structure lpDevMode_ = (Structure) Marshal.deserialize(lpDevMode);
		int dwflags_ = (int) Marshal.deserialize(dwflags); 

		Structure lpDevMode_copy;

		if (lpDevMode_ instanceof DEVMODE) {
			DEVMODE dm = new DEVMODE();
			lpDevMode_copy = dm;
			((DEVMODE)lpDevMode_copy).dmDeviceName = ((DEVMODE)lpDevMode_).dmDeviceName; 
			((DEVMODE)lpDevMode_copy).dmSpecVersion = ((DEVMODE)lpDevMode_).dmSpecVersion; 
			((DEVMODE)lpDevMode_copy).dmDriverVersion = ((DEVMODE)lpDevMode_).dmDriverVersion; 
			((DEVMODE)lpDevMode_copy).dmSize = ((DEVMODE)lpDevMode_).dmSize; 
			((DEVMODE)lpDevMode_copy).dmDriverExtra = ((DEVMODE)lpDevMode_).dmDriverExtra; 
			((DEVMODE)lpDevMode_copy).dmFields = ((DEVMODE)lpDevMode_).dmFields; 
			((DEVMODE)lpDevMode_copy).dmOrientation = ((DEVMODE)lpDevMode_).dmOrientation; 
			((DEVMODE)lpDevMode_copy).dmPaperSize = ((DEVMODE)lpDevMode_).dmPaperSize; 
			((DEVMODE)lpDevMode_copy).dmPaperLength = ((DEVMODE)lpDevMode_).dmPaperLength; 
			((DEVMODE)lpDevMode_copy).dmPaperWidth = ((DEVMODE)lpDevMode_).dmPaperWidth; 
			((DEVMODE)lpDevMode_copy).dmScale = ((DEVMODE)lpDevMode_).dmScale; 
			((DEVMODE)lpDevMode_copy).dmCopies = ((DEVMODE)lpDevMode_).dmCopies; 
			((DEVMODE)lpDevMode_copy).dmDefaultScore = ((DEVMODE)lpDevMode_).dmDefaultScore; 
			((DEVMODE)lpDevMode_copy).dmPrintQuality = ((DEVMODE)lpDevMode_).dmPrintQuality; 
			((DEVMODE)lpDevMode_copy).dmColor = ((DEVMODE)lpDevMode_).dmColor; 
			((DEVMODE)lpDevMode_copy).dmDuplex = ((DEVMODE)lpDevMode_).dmDuplex; 
			((DEVMODE)lpDevMode_copy).dmYResolution = ((DEVMODE)lpDevMode_).dmYResolution; 
			((DEVMODE)lpDevMode_copy).dmTTOption = ((DEVMODE)lpDevMode_).dmTTOption; 
			((DEVMODE)lpDevMode_copy).dmCollate = ((DEVMODE)lpDevMode_).dmCollate; 
			((DEVMODE)lpDevMode_copy).dmFormName = ((DEVMODE)lpDevMode_).dmFormName; 
			((DEVMODE)lpDevMode_copy).dmLogPixels = ((DEVMODE)lpDevMode_).dmLogPixels; 
			((DEVMODE)lpDevMode_copy).dmBitsPerPel = ((DEVMODE)lpDevMode_).dmBitsPerPel; 
			((DEVMODE)lpDevMode_copy).dmPelsWidth = ((DEVMODE)lpDevMode_).dmPelsWidth; 
			((DEVMODE)lpDevMode_copy).dmPelsHeight = ((DEVMODE)lpDevMode_).dmPelsHeight; 
			((DEVMODE)lpDevMode_copy).dmNup = ((DEVMODE)lpDevMode_).dmNup; 
			((DEVMODE)lpDevMode_copy).dmDisplayFrequency = ((DEVMODE)lpDevMode_).dmDisplayFrequency; 
			((DEVMODE)lpDevMode_copy).dmICMMethod = ((DEVMODE)lpDevMode_).dmICMMethod; 
			((DEVMODE)lpDevMode_copy).dmICMIntent = ((DEVMODE)lpDevMode_).dmICMIntent; 
			((DEVMODE)lpDevMode_copy).dmMediaType = ((DEVMODE)lpDevMode_).dmMediaType; 
			((DEVMODE)lpDevMode_copy).dmDitherType = ((DEVMODE)lpDevMode_).dmDitherType; 
			((DEVMODE)lpDevMode_copy).dmReserved1 = ((DEVMODE)lpDevMode_).dmReserved1; 
			((DEVMODE)lpDevMode_copy).dmReserved2 = ((DEVMODE)lpDevMode_).dmReserved2; 
			((DEVMODE)lpDevMode_copy).dmPanningWidth = ((DEVMODE)lpDevMode_).dmPanningWidth; 
			((DEVMODE)lpDevMode_copy).dmPanningHeight = ((DEVMODE)lpDevMode_).dmPanningHeight; 
		} else {
			lpDevMode_copy = lpDevMode_;
		}

		int res = User32.instance.ChangeDisplaySettings(lpDevMode_copy, dwflags_);

		String result = Marshal.serialize(res);

		Jumoke.log.info("<< changeDisplaySettings (" + lpDevMode_ + ", " + dwflags_  + ") -> " + res);
		return result;
	}


	public String enumDisplaySettings(String lpszDeviceName, String iModeNum, String lpDevMode) {
		Jumoke.log.info(">>");
		String lpszDeviceName_ = (String) Marshal.deserialize(lpszDeviceName);
		int iModeNum_ = (int) Marshal.deserialize(iModeNum); 
		Structure lpDevMode_ = (Structure) Marshal.deserialize(lpDevMode); 

		Structure lpDevMode_copy;

		if (lpDevMode_ instanceof DEVMODE) {
			DEVMODE dm = new DEVMODE();
			lpDevMode_copy = dm;
			((DEVMODE)lpDevMode_copy).dmDeviceName = ((DEVMODE)lpDevMode_).dmDeviceName; 
			((DEVMODE)lpDevMode_copy).dmSpecVersion = ((DEVMODE)lpDevMode_).dmSpecVersion; 
			((DEVMODE)lpDevMode_copy).dmDriverVersion = ((DEVMODE)lpDevMode_).dmDriverVersion; 
			((DEVMODE)lpDevMode_copy).dmSize = ((DEVMODE)lpDevMode_).dmSize; 
			((DEVMODE)lpDevMode_copy).dmDriverExtra = ((DEVMODE)lpDevMode_).dmDriverExtra; 
			((DEVMODE)lpDevMode_copy).dmFields = ((DEVMODE)lpDevMode_).dmFields; 
			((DEVMODE)lpDevMode_copy).dmOrientation = ((DEVMODE)lpDevMode_).dmOrientation; 
			((DEVMODE)lpDevMode_copy).dmPaperSize = ((DEVMODE)lpDevMode_).dmPaperSize; 
			((DEVMODE)lpDevMode_copy).dmPaperLength = ((DEVMODE)lpDevMode_).dmPaperLength; 
			((DEVMODE)lpDevMode_copy).dmPaperWidth = ((DEVMODE)lpDevMode_).dmPaperWidth; 
			((DEVMODE)lpDevMode_copy).dmScale = ((DEVMODE)lpDevMode_).dmScale; 
			((DEVMODE)lpDevMode_copy).dmCopies = ((DEVMODE)lpDevMode_).dmCopies; 
			((DEVMODE)lpDevMode_copy).dmDefaultScore = ((DEVMODE)lpDevMode_).dmDefaultScore; 
			((DEVMODE)lpDevMode_copy).dmPrintQuality = ((DEVMODE)lpDevMode_).dmPrintQuality; 
			((DEVMODE)lpDevMode_copy).dmColor = ((DEVMODE)lpDevMode_).dmColor; 
			((DEVMODE)lpDevMode_copy).dmDuplex = ((DEVMODE)lpDevMode_).dmDuplex; 
			((DEVMODE)lpDevMode_copy).dmYResolution = ((DEVMODE)lpDevMode_).dmYResolution; 
			((DEVMODE)lpDevMode_copy).dmTTOption = ((DEVMODE)lpDevMode_).dmTTOption; 
			((DEVMODE)lpDevMode_copy).dmCollate = ((DEVMODE)lpDevMode_).dmCollate; 
			((DEVMODE)lpDevMode_copy).dmFormName = ((DEVMODE)lpDevMode_).dmFormName; 
			((DEVMODE)lpDevMode_copy).dmLogPixels = ((DEVMODE)lpDevMode_).dmLogPixels; 
			((DEVMODE)lpDevMode_copy).dmBitsPerPel = ((DEVMODE)lpDevMode_).dmBitsPerPel; 
			((DEVMODE)lpDevMode_copy).dmPelsWidth = ((DEVMODE)lpDevMode_).dmPelsWidth; 
			((DEVMODE)lpDevMode_copy).dmPelsHeight = ((DEVMODE)lpDevMode_).dmPelsHeight; 
			((DEVMODE)lpDevMode_copy).dmNup = ((DEVMODE)lpDevMode_).dmNup; 
			((DEVMODE)lpDevMode_copy).dmDisplayFrequency = ((DEVMODE)lpDevMode_).dmDisplayFrequency; 
			((DEVMODE)lpDevMode_copy).dmICMMethod = ((DEVMODE)lpDevMode_).dmICMMethod; 
			((DEVMODE)lpDevMode_copy).dmICMIntent = ((DEVMODE)lpDevMode_).dmICMIntent; 
			((DEVMODE)lpDevMode_copy).dmMediaType = ((DEVMODE)lpDevMode_).dmMediaType; 
			((DEVMODE)lpDevMode_copy).dmDitherType = ((DEVMODE)lpDevMode_).dmDitherType; 
			((DEVMODE)lpDevMode_copy).dmReserved1 = ((DEVMODE)lpDevMode_).dmReserved1; 
			((DEVMODE)lpDevMode_copy).dmReserved2 = ((DEVMODE)lpDevMode_).dmReserved2; 
			((DEVMODE)lpDevMode_copy).dmPanningWidth = ((DEVMODE)lpDevMode_).dmPanningWidth; 
			((DEVMODE)lpDevMode_copy).dmPanningHeight = ((DEVMODE)lpDevMode_).dmPanningHeight; 
		} else {
			lpDevMode_copy = lpDevMode_;
		}

		boolean res = User32.instance.EnumDisplaySettings(lpszDeviceName_, iModeNum_, lpDevMode_copy);

		@SuppressWarnings("unused")
		class EnumDisplaySettings {
			boolean res;
			Structure lpDevMode;
		}

		EnumDisplaySettings eds = new EnumDisplaySettings();
		eds.res = res;
		eds.lpDevMode = lpDevMode_copy;

		String result = Marshal.serialize(eds);

		Jumoke.log.info("<< enumDisplaySettings (" + lpszDeviceName_ + ", " + iModeNum_ + ", " + lpDevMode_copy + ") -> " + res);
		return result;
	}

	public String openClipboard(String hWndNewOwner) {
		Jumoke.log.info(">>");
		HWND hWndNewOwner_ = (HWND) Marshal.deserialize(hWndNewOwner);
		boolean res = User32.instance.OpenClipboard(hWndNewOwner_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< openClipboard (" + hWndNewOwner_ + ") -> " + res);
		return result;
	}

	public String closeClipboard() {
		Jumoke.log.info(">>");
		boolean res = User32.instance.CloseClipboard();
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< closeClipboard () -> " + res);
		return result;
	}

	public String emptyClipboard() {
		Jumoke.log.info(">>");
		boolean res = User32.instance.EmptyClipboard();
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< emptyClipboard () -> " + res);
		return result;
	}

	public String closeHandle(String hObject) {
		Jumoke.log.info(">>");
		HANDLE hObject_ = (HANDLE) Marshal.deserialize(hObject);
		boolean res = Kernel32.instance.CloseHandle(hObject_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< closeHandle (" + hObject_ + ") -> " + res);
		return result;
	}

	public String openProcess(String dwDesiredAccess, String bInheritHandle, String  dwProcessId) {
		Jumoke.log.info(">>");
		int dwDesiredAccess_ = (int) Marshal.deserialize(dwDesiredAccess);
		boolean bInheritHandle_ = (boolean) Marshal.deserialize(bInheritHandle);
		int dwProcessId_ = (int) Marshal.deserialize(dwProcessId);
		HANDLE res = Kernel32.instance.OpenProcess(dwDesiredAccess_, bInheritHandle_, dwProcessId_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< openProcess (" + dwDesiredAccess_ + ", " + bInheritHandle_ + ", " + dwProcessId_ + ") -> " + res);
		return result;
	}

	public String readProcessMemory(String hProcess, String lpBaseAddress, String lpBuffer, String  nSize, String lpNumberOfBytesRead){
		Jumoke.log.info(">>");
		HANDLE hProcess_ = (HANDLE) Marshal.deserialize(hProcess);
		Pointer lpBaseAddress_ = (Pointer) Marshal.deserialize(lpBaseAddress);
		Structure lpBuffer_ = new RECT();//(Structure) Marshal.deserialize(lpBuffer);
		int nSize_ = 16;// (int) Marshal.deserialize(nSize);
		Pointer lpNumberOfBytesRead_ = new Pointer(0);// (Pointer) Marshal.deserialize(lpNumberOfBytesRead);
		boolean res = Kernel32.instance.ReadProcessMemory(hProcess_, lpBaseAddress_, lpBuffer_, nSize_, lpNumberOfBytesRead_);

		@SuppressWarnings("unused")
		class ReadProcessMemory {
			boolean res;
			Structure lpBuffer;
		}

		ReadProcessMemory rv = new ReadProcessMemory();
		rv.res = res;
		rv.lpBuffer = lpBuffer_;

		String result = Marshal.serialize(rv); 
		Jumoke.log.info("<< readProcessMemory (" + hProcess_ + ", " + lpBaseAddress_ + ", " + "<?>" + ", " + nSize_ + ", " + lpNumberOfBytesRead_ + ") -> " + res + ", " + "lpBuffer = " + lpBuffer_);
		return result;
	}

	public String virtualAllocEx(String hProcess, String lAddress, String dwSize, String flAllocationType, String flProtect){
		Jumoke.log.info(">>");
		HANDLE hProcess_ = (HANDLE) Marshal.deserialize(hProcess);
		long lAddress_ = (long) Marshal.deserialize(lAddress);		
		Pointer lpAddress_ = new Pointer(lAddress_);	
		int dwSize_ = (int) Marshal.deserialize(dwSize);
		int flAllocationType_ = (int) Marshal.deserialize(flAllocationType);
		int flProtect_ = (int) Marshal.deserialize(flProtect);
		Pointer res = Kernel32.instance.VirtualAllocEx(hProcess_, lpAddress_, dwSize_, flAllocationType_, flProtect_);
		String result = Marshal.serialize(res); //try return native long
		Jumoke.log.info("<< virtualAllocEx (" + hProcess_ + ", " + lpAddress_ + ", " + dwSize_ + ", " + flAllocationType_ + ", " + flProtect_ + ") -> " + res);
		return result;
	}

	public String virtualFreeEx(String hProcess, String lpAddress, String dwSize, String dwFreeType) {
		Jumoke.log.info(">>");
		HANDLE hProcess_ = (HANDLE) Marshal.deserialize(hProcess);
		Pointer lpAddress_ = (Pointer) Marshal.deserialize(lpAddress);
		int dwSize_ = (int) Marshal.deserialize(dwSize);
		int dwFreeType_ = (int) Marshal.deserialize(dwFreeType);
		boolean res = Kernel32.instance.VirtualFreeEx(hProcess_, lpAddress_, dwSize_, dwFreeType_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< virtualFreeEx (" + hProcess_ + ", " + lpAddress_ + ", " + dwSize_ + ", " + dwFreeType_ + ") -> " + res);
		return result;
	}

	public String writeProcessMemory(String hProcess, String lpBaseAddress, String lpBuffer, String nSize, String lNumberOfBytesWritten) {
		Jumoke.log.info(">>");
		HANDLE hWnd_ = (HANDLE) Marshal.deserialize(hProcess); //probably need receive int
		Pointer lpBaseAddress_ = (Pointer) Marshal.deserialize(lpBaseAddress); //probably need receive native long
		Structure lpBuffer_ = (Structure) Marshal.deserialize(lpBuffer); 
		int nSize_ = (int) Marshal.deserialize(nSize);
		long lNumberOfBytesWritten_ = (long) Marshal.deserialize(lNumberOfBytesWritten);
		Pointer lpNumberOfBytesWritten_ = new Pointer(lNumberOfBytesWritten_);

		Structure lpBuffer_copy;

		if (lpBuffer_ instanceof RECT) {
			RECT rect = new RECT();
			lpBuffer_copy = rect;
			((RECT)lpBuffer_copy).left = ((RECT)lpBuffer_).left; 
			((RECT)lpBuffer_copy).right = ((RECT)lpBuffer_).right; 
			((RECT)lpBuffer_copy).bottom = ((RECT)lpBuffer_).bottom; 
			((RECT)lpBuffer_copy).top = ((RECT)lpBuffer_).top; 
		} else if (lpBuffer_ instanceof POINT) {
			POINT point = new POINT();
			lpBuffer_copy = point;
			((POINT)lpBuffer_copy).x = ((POINT)lpBuffer_).x; 
			((POINT)lpBuffer_copy).y = ((POINT)lpBuffer_).y; 
		} else {
			lpBuffer_copy = lpBuffer_;
		}

		boolean res = Kernel32.instance.WriteProcessMemory(hWnd_, lpBaseAddress_, lpBuffer_copy, nSize_, lpNumberOfBytesWritten_);

		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< writeProcessMemory (" + hWnd_ + ", " + lpBaseAddress_ + ", " + lpBuffer_copy + ", " + nSize_ + ", " + lpNumberOfBytesWritten_ + ") -> " + res);
		return result;
	}

	public String getWindowThreadProcessId(String hWnd, String ldwProcessId) {
		Jumoke.log.info(">>");
		HWND hWnd_ = (HWND) Marshal.deserialize(hWnd);
		//IntByReference lpdwProcessId_ = (IntByReference) Marshal.deserialize(lpdwProcessId);
		int ldwProcessId_ = (int) Marshal.deserialize(ldwProcessId);
		IntByReference lpdwProcessId = new IntByReference(ldwProcessId_);
		int res = User32.instance.GetWindowThreadProcessId(hWnd_, lpdwProcessId);

		class WindowThreadProcessId {
			int res;
			int ldwProcessId;
		}

		WindowThreadProcessId rv = new WindowThreadProcessId();
		rv.res = res;
		rv.ldwProcessId = lpdwProcessId.getValue();

		String result = Marshal.serialize(rv); 
		Jumoke.log.info("<< getWindowThreadProcessId (" + hWnd_ + ", " + ldwProcessId_ + ") -> " + rv.res + ", " + "lpdwProcessId = " + rv.ldwProcessId);
		return result;
	}

	public String findWindow(String lpClassName, String lpWindowName) {
		Jumoke.log.info(">>");
		String lpClassName_ = (String) Marshal.deserialize(lpClassName);
		String lpWindowName_ = (String) Marshal.deserialize(lpWindowName);
		HWND res = User32.instance.FindWindow(lpClassName_, lpWindowName_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< findWindow (" + lpClassName_ + ", " + lpWindowName_ + ") -> " + res);
		return result;
	}

	public String clientToScreen(String hWnd, String lpPoint){
		Jumoke.log.info(">>");
		HWND hWnd_ = (HWND) Marshal.deserialize(hWnd);
		POINT lpPoint_ = (POINT) Marshal.deserialize(lpPoint);

		POINT lpPoint_copy = new POINT();
		lpPoint_copy.x = lpPoint_.x;
		lpPoint_copy.y = lpPoint_.y;

		boolean res = User32.instance.ClientToScreen(hWnd_, lpPoint_copy);

		class ClientToScreen {
			boolean res;
			POINT lpPoint;
		}

		ClientToScreen rv = new ClientToScreen();
		rv.res = res;
		rv.lpPoint = lpPoint_copy;

		String result = Marshal.serialize(rv); 
		Jumoke.log.info("<< clientToScreen (" + hWnd_ + ", <?>" + ") -> " + rv.res + ", " + "lpPoint = " + rv.lpPoint);
		return result;
	}


	//LRESULT SendMessage(HWND hWnd, UINT Msg, WPARAM wParam, LPARAM lParam);
	public String sendMessage(String hWnd, String Msg, String wParam, String lParam) {
		Jumoke.log.info(">>");
		HWND hWnd_ = (HWND) Marshal.deserialize(hWnd);
		UINT Msg_ = (UINT) Marshal.deserialize(Msg);

		long lwParam = (long) Marshal.deserialize(wParam);
		long llParam = (long) Marshal.deserialize(lParam);

		WPARAM wParam_ = new WPARAM(lwParam); //(WPARAM) Marshal.deserialize(wParam);
		LPARAM lParam_ = new LPARAM(llParam); //(LPARAM) Marshal.deserialize(lParam); 

		LRESULT lresult = new LRESULT();

		lresult = User32.instance.SendMessage(hWnd_, Msg_, wParam_, lParam_);

		String result = Marshal.serialize(lresult); 

		Jumoke.log.info("<< sendMessage (" + hWnd_ + ", " +  Msg_ + ", " + wParam_ + ", " + lParam_ + ") -> " + lresult);
		return result;
	}


	public String setCursorPos(String X, String Y) {
		Jumoke.log.info(">>");
		int X_ = (int) Marshal.deserialize(X);
		int Y_ = (int) Marshal.deserialize(Y);
		boolean res = User32.instance.SetCursorPos(X_, Y_);
		String result = Marshal.serialize(res); 
		Jumoke.log.info("<< setCursorPos (" + X_ + ", " + Y_ + ") -> " + res);
		return result;
	}


	public String getCursorPos() {
		Jumoke.log.info(">>");
		POINT lpPoint = new POINT();
		boolean res = User32.instance.GetCursorPos(lpPoint);

		@SuppressWarnings("unused")
		class CursorPos {
			boolean res;
			POINT lpPoint;
			long lPoint;
		}

		CursorPos rv = new CursorPos();
		rv.res = res;
		rv.lpPoint = lpPoint;
		rv.lPoint = lpPoint.getPointer().getLong(0);

		String result = Marshal.serialize(rv); 
		Jumoke.log.info("<< getCursorPos (" + "<lpPoint>" + ") -> " + res + ", " + "lpPoint = " + lpPoint);
		return result;
	}

	public String windowFromPoint(String lPoint) {
		Jumoke.log.info(">>");
		long lPoint_ = (long) Marshal.deserialize(lPoint);
		HWND hWnd = User32.instance.WindowFromPoint(lPoint_);
		String result = Marshal.serialize(hWnd); 
		Jumoke.log.info("<< windowFromPoint (" + lPoint_ + ") -> " + hWnd);
		return result;
	}

	//	public String windowFromPoint(String x, String y) {
	//		Jumoke.log.info(">>");
	//		int x_ = (int) Marshal.deserialize(x);
	//		int y_ = (int) Marshal.deserialize(y);
	//		User32.instance.SetCursorPos(x_, y_);
	//		long[] getPos = new long[1];
	//		User32.instance.GetCursorPos(getPos);
	//		int hwnd = User32.instance.WindowFromPoint(getPos[0]);
	//		String result = Marshal.serialize(hwnd); 
	//		Jumoke.log.info("<< windowFromPoint (" + x_ + ", " +  y_ + ") -> " + hwnd);
	//		return result;
	//	}

	/*	POINT p = new POINT();

	p.x= 1000;
	p.y = 500;

	while (true) {

	System.out.println("hTV = " + User32.inst.GetCursorPos(p));
	System.out.println("hTV = " + p);
	System.out.println("hTV = " + User32.inst.WindowFromPoint(p.getPointer().getLong(0)));
	}*/


}
