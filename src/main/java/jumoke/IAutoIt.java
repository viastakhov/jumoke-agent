package jumoke;

import java.nio.CharBuffer;

import com.sun.jna.Library;
import com.sun.jna.WString;

/**
 * AutoITX native interface
 * 
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.2
 */
public interface IAutoIt extends Library {
    
	int   AU3_INTDEFAULT = -2147483647;
	

    //void AU3_Init();
    //long AU3_error();

    int AU3_AutoItSetOption(WString szOption, int nValue);

    void AU3_BlockInput(long nFlag);

    //long AU3_CDTray( CharBuffer szDrive,  CharBuffer szAction);
    void AU3_ClipGet(CharBuffer szClip, int nBufSize);
    //void AU3_ClipPut( CharBuffer szClip);
    int AU3_ControlClick(WString szTitle,  WString szText,  WString szControl,  WString szButton, int nNumClicks, int nX, int nY);
    void AU3_ControlCommand(WString szTitle, WString szText, WString szControl, WString szCommand, WString szExtra, CharBuffer szResult, int nBufSize);
//    void AU3_ControlListView( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szControl,  CharBuffer szCommand,  CharBuffer szExtra1,  CharBuffer szExtra2, WString szResult, int nBufSize);
//    long AU3_ControlDisable( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szControl);
//    long AU3_ControlEnable( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szControl);
    int AU3_ControlFocus(WString szTitle, WString szText, WString szControl);
//    void AU3_ControlGetFocus( CharBuffer szTitle,  CharBuffer szText, WString szControlWithFocus, int nBufSize);
    void AU3_ControlGetHandle(WString szTitle, WString szText, WString szControl, CharBuffer szRetText, int nBufSize);
    int AU3_ControlGetPosX(WString szTitle,  WString szText, WString szControl);
    int AU3_ControlGetPosY(WString szTitle,  WString szText, WString szControl);
    int AU3_ControlGetPosHeight(WString szTitle, WString szText, WString szControl);
    int AU3_ControlGetPosWidth(WString szTitle, WString szText, WString szControl);
    void AU3_ControlGetText(WString szTitle, WString szText, WString szControl, CharBuffer szControlText, int nBufSize);
//    long AU3_ControlHide( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szControl);
//    long AU3_ControlMove( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szControl, long nX, long nY, long nWidth, long nHeight);
    int AU3_ControlSend(WString szTitle, WString szText, WString szControl, WString szSendText, long nMode);
    int AU3_ControlSetText(WString szTitle, WString szText, WString szControl, WString szControlText);
//    long AU3_ControlShow( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szControl);
    void AU3_ControlTreeView(WString szTitle, WString szText, WString szControl, WString szCommand, WString szExtra1,  WString szExtra2, CharBuffer szResult, int nBufSize);
//
//    void AU3_DriveMapAdd( CharBuffer szDevice,  CharBuffer szShare, long nFlags, CharBuffer szUser, CharBuffer szPwd, WString szResult, int nBufSize);
//    long AU3_DriveMapDel( CharBuffer szDevice);
//    void AU3_DriveMapGet( CharBuffer szDevice, WString szMapping, int nBufSize);
//
//    long AU3_IniDelete( CharBuffer szFilename,  CharBuffer szSection,  CharBuffer szKey);
//    void AU3_IniRead( CharBuffer szFilename,  CharBuffer szSection,  CharBuffer szKey,  CharBuffer szDefault, WString szValue, int nBufSize);
//    long AU3_IniWrite( CharBuffer szFilename,  CharBuffer szSection,  CharBuffer szKey,  CharBuffer szValue);
//    long AU3_IsAdmin();
//
    int AU3_MouseClick(WString szButton, int nX, int nY, int nClicks, int nSpeed);
    int AU3_MouseClickDrag(WString szButton, int nX1, int nY1, int nX2, int nY2, int nSpeed);
//    void AU3_MouseDown(  CharBuffer szButton);
//    long AU3_MouseGetCursor();
    long AU3_MouseGetPosX();
    long AU3_MouseGetPosY();
//    long AU3_MouseMove(long nX, long nY, long nSpeed);
//    void AU3_MouseUp(  CharBuffer szButton);
//    void AU3_MouseWheel( CharBuffer szDirection, long nClicks);
//
//    long AU3_Opt( CharBuffer szOption, long nValue);
//
//    long AU3_PixelChecksum(long nLeft, long nTop, long nRight, long nBottom, long nStep);
//    long AU3_PixelGetColor(long nX, long nY);
//    //void AU3_PixelSearch(long nLeft, long nTop, long nRight, long nBottom, long nCol, /*default 0*/long nVar, /*default 1*/long nStep, LPPOINT pPointResult);
//    long AU3_ProcessClose( CharBuffer szProcess);
//    long AU3_ProcessExists( CharBuffer szProcess);
//    long AU3_ProcessSetPriority( CharBuffer szProcess, long nPriority);
//    long AU3_ProcessWait( CharBuffer szProcess,  long nTimeout);
//    long AU3_ProcessWaitClose( CharBuffer szProcess,  long nTimeout);
//    long AU3_RegDeleteKey( CharBuffer szKeyname);
//    long AU3_RegDeleteVal( CharBuffer szKeyname,  CharBuffer szValuename);
//    void AU3_RegEnumKey( CharBuffer szKeyname, long nInstance, WString szResult, int nBufSize);
//    void AU3_RegEnumVal( CharBuffer szKeyname, long nInstance, WString szResult, int nBufSize);
//    void AU3_RegRead( CharBuffer szKeyname,  CharBuffer szValuename, WString szRetText, int nBufSize);
//    long AU3_RegWrite( CharBuffer szKeyname,  CharBuffer szValuename,  CharBuffer szType,  CharBuffer szValue);
    int AU3_Run(WString szRun, WString szDir, long nShowFlags);
//    long AU3_RunAsSet( CharBuffer szUser,  CharBuffer szDomain,  CharBuffer szPassword, int nOptions);
//    long AU3_RunWait( CharBuffer szRun, CharBuffer szDir, long nShowFlags);
//
    void AU3_Send(WString szSendText, long nMode);
//
//    long AU3_Shutdown(long nFlags);
    void AU3_Sleep(long nMilliseconds);
//    void AU3_StatusbarGetText( CharBuffer szTitle, CharBuffer szText, long nPart, WString szStatusText, int nBufSize);
//
//    void AU3_ToolTip( CharBuffer szTip, long nX, long nY);
//
    void AU3_WinActivate(WString szTitle, WString szText);
//    long AU3_WinActive( CharBuffer szTitle, CharBuffer szText);
    int AU3_WinClose(WString szTitle, WString szText);
    int AU3_WinExists(WString szTitle, WString szText);
//    long AU3_WinGetCaretPosX();
//    long AU3_WinGetCaretPosY();
//    void AU3_WinGetClassList( CharBuffer szTitle, CharBuffer szText, WString szRetText, int nBufSize);
//    long AU3_WinGetClientSizeHeight( CharBuffer szTitle, CharBuffer szText);
//    long AU3_WinGetClientSizeWidth( CharBuffer szTitle, CharBuffer szText);
//    void AU3_WinGetHandle( CharBuffer szTitle, CharBuffer szText, WString szRetText, int nBufSize);
    int AU3_WinGetPosX(WString szTitle, WString szText);
    int AU3_WinGetPosY(WString szTitle, WString szText);
    int AU3_WinGetPosHeight(WString szTitle, WString szText);
    int AU3_WinGetPosWidth(WString szTitle, WString szText);
//    void AU3_WinGetProcess( CharBuffer szTitle, CharBuffer szText, WString szRetText, int nBufSize);
//    long AU3_WinGetState( CharBuffer szTitle, CharBuffer szText);
    void AU3_WinGetText(WString szTitle, WString szText, CharBuffer szRetText, int nBufSize);
    void AU3_WinGetTitle(WString szTitle, WString szText, CharBuffer szRetText, int nBufSize);
//    long AU3_WinKill( CharBuffer szTitle, CharBuffer szText);
//    long AU3_WinMenuSelectItem( CharBuffer szTitle, CharBuffer szText,  CharBuffer szItem1,  CharBuffer szItem2,  CharBuffer szItem3,  CharBuffer szItem4,  CharBuffer szItem5,  CharBuffer szItem6,  CharBuffer szItem7,  CharBuffer szItem8);
//    void AU3_WinMinimizeAll();
//    void AU3_WinMinimizeAllUndo();
//    long AU3_WinMove( CharBuffer szTitle, CharBuffer szText, long nX, long nY, long nWidth, long nHeight);
//    long AU3_WinSetOnTop( CharBuffer szTitle, CharBuffer szText, long nFlag);
    int AU3_WinSetState(WString szTitle, WString szText, long nFlags);
//    long AU3_WinSetTitle( CharBuffer szTitle,  CharBuffer szText,  CharBuffer szNewTitle);
//    long AU3_WinSetTrans( CharBuffer szTitle, CharBuffer szText, long nTrans);
    int AU3_WinWait(WString szTitle, WString szText,  long nTimeout);    
    int AU3_WinWaitActive(WString szTitle, WString szText,  long nTimeout);
    int AU3_WinWaitClose(WString szTitle, WString szText,  long nTimeout);
//    long AU3_WinWaitNotActive( CharBuffer szTitle, CharBuffer szText, long nTimeout);
       
}


