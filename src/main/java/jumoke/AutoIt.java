package jumoke;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.logging.Level;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HBRUSH;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HRGN;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;


/**
 * AutoITX native class implementation
 *
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.5
 */
public class AutoIt implements IAutoIt {
    private IAutoIt au3;
    private boolean highlightMode;
    private int highlightDelay;

    private final static byte HIGHLIGHT_FLASH_SEVERE = 0;
    private final static byte HIGHLIGHT_FLASH_WARNING = 1;
    private final static byte HIGHLIGHT_FLASH_INFO = 2;

    public static interface User32 extends StdCallLibrary {
        final User32 instance = (User32) Native.loadLibrary("user32", User32.class);
        HWND FindWindowExA(HWND hwndParent, HWND childAfter, String className, String windowName);
        HWND FindWindowA(String className, String windowName);
        HDC GetDC(HWND hWnd);
        int ReleaseDC(HWND hWnd, HDC hDC);
        boolean GetClientRect(HWND hWnd, RECT lpRect);
        boolean InvalidateRect(HWND hWnd, RECT lpRect, boolean bErase);
        boolean InvalidateRgn(HWND hWnd, HRGN hrgn, boolean bErase);
        boolean UpdateWindow(HWND hWnd);
        HWND GetParent(HWND hWnd);
        boolean ShowWindow(HWND hWnd, int nCmdShow);
        boolean GetCursorPos(POINT lpPoint);
        boolean SetCursorPos(int X, int Y);
        HWND WindowFromPoint(long lPoint);
    }

    public static interface Gdi32 extends Library {
        final Gdi32 instance = (Gdi32) Native.loadLibrary("gdi32", Gdi32.class);
        HRGN CreateRectRgn(int nLeftRect, int nTopRect, int nRightRect, int nBottomRect);
        HBRUSH CreateSolidBrush(long crColor);
        boolean FrameRgn(HDC hdc, HRGN hrgn, HBRUSH hbr, int nWidth, int nHeight);
        boolean DeleteObject(HANDLE hObject);
    }

    /**
     * Initializes a new instance of the AutoIT
     *
     * @throws IOException
     */
    public AutoIt(boolean highlightMode, int highlightDelay) throws IOException {
        this.highlightMode = highlightMode;
        this.highlightDelay = highlightDelay;
        String libPath = null;

        if (Platform.isWindows()) {
            libPath = Platform.is64Bit() ? "/libs/autoitx/AutoItX3_x64.dll" : "/library/autoitx/AutoItX3.dll";
        }

        au3 = (IAutoIt) NativeUtils.loadLibraryFromJar(libPath, IAutoIt.class);
    }

    public AutoIt() throws IOException {
        this(false, 1000);
    }

    public int getHighlightDelay() {
        return this.highlightDelay;
    }

    public void setHighlightDelay(int highlightDelay) {
        this.highlightDelay = highlightDelay;
    }

    public void setHighlightMode(boolean highlightMode) {
        this.highlightMode = highlightMode;
    }

    public boolean getHighlightMode() {
        return this.highlightMode;
    }

    public void highlightControl(String szTitle, String szText, String szControl, byte flashStyle) {
        if (this.highlightMode) {
            WString szTitle_ = new WString(szTitle);
            WString szText_ = new WString(szText);
            WString szControl_ = new WString(szControl);
            this.highlightControl(szTitle_, szText_, szControl_, flashStyle);
        }
    }

    public void highlightControl(WString szTitle, WString szText, WString szControl, byte flashStyle) {
        if (this.highlightMode) {
            Jumoke.log.info(">> szTitle = " + szTitle + ", szText = " + szText + ", szControl = " + szControl);
            int handle = 0;

            try {
                int nBufSize = 200;
                CharBuffer szRetText = CharBuffer.allocate(nBufSize);
                au3.AU3_ControlGetHandle(szTitle, szText, szControl, szRetText, nBufSize);
                String szRetTextTrimmed = szRetText.toString().trim();

                if ((szRetText != null) && (szRetTextTrimmed.length() != 0)) {
                    handle = Integer.parseInt(szRetTextTrimmed, 16);
                    this.highlightControl(handle, flashStyle);
                }

                Jumoke.log.info("<< " + handle);
            } catch (Exception e) {
                Jumoke.log.log(Level.SEVERE, e.toString(), e);
            }
        }
    }

    public void highlightControl(int handle, byte flashStyle) {
        if (this.highlightMode) {
            Jumoke.log.info(">> handle = " + handle);
            HWND hWnd = new HWND(Pointer.createConstant(handle));
            Jumoke.log.info("<< hWnd = " + hWnd);
            this.highlightControl(hWnd, flashStyle);
        }
    }

    public void highlightControl(HWND hWnd, byte flashStyle) {
        if (this.highlightMode) {
            Jumoke.log.info(">> hWnd = " + hWnd);
            HDC hDC = User32.instance.GetDC(hWnd);
            boolean bCR = false;
            boolean bFR = false;
            boolean bIR = false;
            int rDC = 0;

            if (hDC != null) {
                WinDef.RECT rect = new WinDef.RECT();
                bCR = User32.instance.GetClientRect(hWnd, rect);

                if (bCR) {
                    HRGN hRgn = Gdi32.instance.CreateRectRgn(rect.left, rect.top, rect.right, rect.bottom);

                    if (hRgn != null) {
                        HBRUSH hBrush = null;

                        switch (flashStyle) {
                            case HIGHLIGHT_FLASH_SEVERE:
                                hBrush = Gdi32.instance.CreateSolidBrush(0x0000FF);
                                break;
                            case HIGHLIGHT_FLASH_WARNING:
                                hBrush = Gdi32.instance.CreateSolidBrush(0x00FFFF);
                                break;
                            case HIGHLIGHT_FLASH_INFO:
                                hBrush = Gdi32.instance.CreateSolidBrush(0x00FF00);
                                break;
                        }

                        //HBRUSH hBrush = Gdi32.instance.CreateSolidBrush(0x0000FF);

                        if (hBrush != null) {
                            bFR = Gdi32.instance.FrameRgn(hDC, hRgn, hBrush, 2, 2);

                            // Clean up
                            Gdi32.instance.DeleteObject(hRgn);
                            Gdi32.instance.DeleteObject(hBrush);

                            try {
                                Thread.sleep(this.highlightDelay);
                            } catch (InterruptedException e) {
                                Jumoke.log.log(Level.SEVERE, e.toString(), e);
                            }
                            rDC = User32.instance.ReleaseDC(hWnd, hDC);

                            //Declare an invalid rectangle and then we request OS to repaint the rectangle.
                            bIR = User32.instance.InvalidateRect(hWnd, rect, true);
                            User32.instance.UpdateWindow(hWnd);
                        }
                    }
                }
            }
            Jumoke.log.info("<< [bCR:" + bCR + ", bFR:" + bFR + ", bIR:" + bIR + ", rDC:" + rDC + "]");
        }
    }


    public String autoItSetOption(String szOption, String nValue) {
        Jumoke.log.info(">>");
        WString szOption_ = new WString((String) Marshal.deserialize(szOption));
        int nValue_ = (int) Marshal.deserialize(nValue);
        int res = this.AU3_AutoItSetOption(szOption_, nValue_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< autoItSetOption (" + szOption_ + ", " + nValue_ + ") -> " + res);
        return result;
    }

    public String blockInput(String nFlag) {
        Jumoke.log.info(">>");
        long nFlag_ = (long) Marshal.deserialize(nFlag);
        this.AU3_BlockInput(nFlag_);
        String result = Marshal.serialize(1L);
        Jumoke.log.info("<< blockInput (" + nFlag_ + ")");
        return result;
    }

    public String clipGet(String nBufSize) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");
        //User32.instance.OpenClipboard(null);
        int nBufSize_ = (int) Marshal.deserialize(nBufSize);
        CharBuffer szClip = CharBuffer.allocate(nBufSize_);
        this.AU3_ClipGet(szClip, nBufSize_);
        String sRes = szClip.toString();
        //User32.instance.CloseClipboard();
        String result = Marshal.serialize(sRes.getBytes());
        Jumoke.log.info("<< clipGet (" + nBufSize_ + ") -> " + sRes);
        return result;
    }

    public String controlGetHandle(String szTitle, String szText, String szControl) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_INFO);
        int handle = 0;
        int nBufSize = 200;

        try {
            CharBuffer szRetText = CharBuffer.allocate(nBufSize);
            this.AU3_ControlGetHandle(szTitle_, szText_, szControl_, szRetText, nBufSize);
            handle = Integer.parseInt(szRetText.toString().trim(), 16);
        } catch (Exception e) {
            Jumoke.log.log(Level.SEVERE, e.toString(), e);
        }

        String result = Marshal.serialize(handle);
        Jumoke.log.info("<< controlGetHandle (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + handle);
        return result;
    }

    public HWND controlGetHandle(int x, int y) {
        WinDef.POINT lpPoint = new WinDef.POINT();
        User32.instance.SetCursorPos(x, y);
        User32.instance.GetCursorPos(lpPoint);
        HWND hWnd = User32.instance.WindowFromPoint(lpPoint.getPointer().getLong(0));
        return hWnd;
    }

    public String controlClick(String szTitle, String szText, String szControl, String szButton, String nNumClicks, String nX, String nY) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        WString szButton_ = new WString((String) Marshal.deserialize(szButton));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_SEVERE);
        int nNumClicks_ = (int) Marshal.deserialize(nNumClicks);
        int nX_ = (int) Marshal.deserialize(nX);
        int nY_ = (int) Marshal.deserialize(nY);

        int res = this.AU3_ControlClick(szTitle_, szText_, szControl_, szButton_, nNumClicks_, nX_, nY_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlClick (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ", " + szButton_ + ", " + nNumClicks_ + ", " + nX_ + ", " + nY_ + ") -> " + res);
        return result;
    }

    public String controlCommand(String szTitle, String szText, String szControl, String szCommand, String szExtra) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");

        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        WString szCommand_ = new WString((String) Marshal.deserialize(szCommand));
        WString szExtra_ = new WString((String) Marshal.deserialize(szExtra));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_SEVERE);
        String res = "";
        int nBufSize = 256;

        try {
            CharBuffer szResult = CharBuffer.allocate(nBufSize);
            this.AU3_ControlCommand(szTitle_, szText_, szControl_, szCommand_, szExtra_, szResult, nBufSize);
            res = szResult.toString().trim();
        } catch (Exception e) {
            Jumoke.log.log(Level.SEVERE, e.toString(), e);
        }

        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlCommand (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ", " + szCommand_ + ", " + szExtra_ + ") -> " + res);
        return result;
    }

    public String controlFocus(String szTitle, String szText, String szControl) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_WARNING);
        int res = this.AU3_ControlFocus(szTitle_, szText_, szControl_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlSetText (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + res);
        return result;
    }

    public String controlGetPosX(String szTitle, String szText, String szControl) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_ControlGetPosX(szTitle_, szText_, szControl_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlGetPosX (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + res);
        return result;
    }

    public String controlGetPosY(String szTitle, String szText, String szControl) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_ControlGetPosY(szTitle_, szText_, szControl_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlGetPosY (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + res);
        return result;
    }

    public String controlGetPosHeight(String szTitle, String szText, String szControl) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_ControlGetPosHeight(szTitle_, szText_, szControl_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlGetPosHeight (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + res);
        return result;
    }

    public String controlGetPosWidth(String szTitle, String szText, String szControl) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_ControlGetPosWidth(szTitle_, szText_, szControl_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlGetPosWidth (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + res);
        return result;
    }

    public String controlGetText(String szTitle, String szText, String szControl) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");

        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_INFO);
        String res = "";
        int nBufSize = 131072;

        try {
            CharBuffer szControlText = CharBuffer.allocate(nBufSize);
            this.AU3_ControlGetText(szTitle_, szText_, szControl_, szControlText, nBufSize);
            res = szControlText.toString().trim();
        } catch (Exception e) {
            Jumoke.log.log(Level.SEVERE, e.toString(), e);
        }

        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlGetText (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ") -> " + res);
        return result;
    }

    public String controlSend(String szTitle, String szText, String szControl, String szSendText, String nMode) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        WString szSendText_ = new WString((String) Marshal.deserialize(szSendText));
        long nMode_ = (long) Marshal.deserialize(nMode);
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_SEVERE);
        int res = this.AU3_ControlSend(szTitle_, szText_, szControl_, szSendText_, nMode_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlSend (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ", " + szSendText_ + ", " + nMode_ + ") -> " + res);
        return result;
    }

    public String controlSetText(String szTitle, String szText, String szControl, String szControlText) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        WString szControlText_ = new WString((String) Marshal.deserialize(szControlText));
        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_SEVERE);
        int res = this.AU3_ControlSetText(szTitle_, szText_, szControl_, szControlText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlSetText (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ", " + szControlText_ + ") -> " + res);
        return result;
    }

    public String controlTreeView(String szTitle, String szText, String szControl, String szCommand, String szExtra1, String szExtra2) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");

        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        WString szControl_ = new WString((String) Marshal.deserialize(szControl));
        WString szCommand_ = new WString((String) Marshal.deserialize(szCommand));
        WString szExtra1_ = new WString((String) Marshal.deserialize(szExtra1));
        WString szExtra2_ = new WString((String) Marshal.deserialize(szExtra1));

        this.highlightControl(szTitle_, szText_, szControl_, HIGHLIGHT_FLASH_SEVERE);
        String res = "";
        int nBufSize = 256;

        try {
            CharBuffer szResult = CharBuffer.allocate(nBufSize);
            this.AU3_ControlTreeView(szTitle_, szText_, szControl_, szCommand_, szExtra1_, szExtra2_, szResult, nBufSize);
            res = szResult.toString().trim();
        } catch (Exception e) {
            Jumoke.log.log(Level.SEVERE, e.toString(), e);
        }

        String result = Marshal.serialize(res);
        Jumoke.log.info("<< controlTreeView (" + szTitle_ + ", " + szText_ + ", " + szControl_ + ", " + szCommand_ + ", " + szExtra1_ + ", " + szExtra2_ + ") -> " + res);
        return result;
    }

    public String mouseClick(String szButton, String nX, String nY, String nClicks, String nSpeed) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szButton_ = new WString((String) Marshal.deserialize(szButton));
        int nX_ = (int) Marshal.deserialize(nX);
        int nY_ = (int) Marshal.deserialize(nY);
        int nClicks_ = (int) Marshal.deserialize(nClicks);
        int nSpeed_ = (int) Marshal.deserialize(nSpeed);

        if (this.highlightMode) {
            HWND hWnd = controlGetHandle(nX_, nX_);
            highlightControl(hWnd, HIGHLIGHT_FLASH_SEVERE);
        }

        int res = this.AU3_MouseClick(szButton_, nX_, nY_, nClicks_, nSpeed_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< mouseClick (" + szButton_ + ", " + nX_ + ", " + nY_ + ", " + nClicks_ + ", " + nSpeed_ + ") -> " + res);
        return result;
    }

    public String mouseClickDrag(String szButton, String nX1, String nY1, String nX2, String nY2, String nSpeed) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szButton_ = new WString((String) Marshal.deserialize(szButton));
        int nX1_ = (int) Marshal.deserialize(nX1);
        int nY1_ = (int) Marshal.deserialize(nY1);
        int nX2_ = (int) Marshal.deserialize(nX2);
        int nY2_ = (int) Marshal.deserialize(nY2);
        int nSpeed_ = (int) Marshal.deserialize(nSpeed);

        if (this.highlightMode) {
            HWND hWnd = controlGetHandle(nX1_, nY1_);
            highlightControl(hWnd, HIGHLIGHT_FLASH_SEVERE);
            hWnd = controlGetHandle(nX2_, nY2_);
            highlightControl(hWnd, HIGHLIGHT_FLASH_SEVERE);
        }

        int res = this.AU3_MouseClickDrag(szButton_, nX1_, nY1_, nX2_, nY2_, nSpeed_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< mouseClick (" + szButton_ + ", " + nX1_ + ", " + nY1_ + ", " + nX2_ + ", " + nY2_ + ", " + nSpeed_ + ") -> " + res);
        return result;
    }

    public String mouseGetPosX() throws IOException {
        Jumoke.log.info(">>");
        long x = this.AU3_MouseGetPosX();
        Jumoke.log.info("<< mouseGetPosX () -> " + x);
        return Marshal.serialize(x);
    }

    public String mouseGetPosY() throws IOException {
        Jumoke.log.info(">>");
        long y = this.AU3_MouseGetPosY();
        Jumoke.log.info("<< mouseGetPosY () -> " + y);
        return Marshal.serialize(y);
    }

    public String run(String szRun, String szDir, String nShowFlags) throws IOException {
        Jumoke.log.info(">>");
        WString szRun_ = new WString((String) Marshal.deserialize(szRun));
        WString szDir_ = new WString((String) Marshal.deserialize(szDir));
        long nShowFlags_ = (long) Marshal.deserialize(nShowFlags);
        int res = this.AU3_Run(szRun_, szDir_, nShowFlags_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< run (" + szRun_ + ", " + szDir_ + ", " + nShowFlags_ + ") -> " + res);
        return result;
    }

    public String send(String szSendText, String nMode) {
        Jumoke.log.info(">>");
        WString szSendText_ = new WString((String) Marshal.deserialize(szSendText));
        long nMode_ = (long) Marshal.deserialize(nMode);
        this.AU3_Send(szSendText_, nMode_);
        String result = Marshal.serialize(1L);
        Jumoke.log.info("<< send (" + szSendText_ + ", " + nMode_ + ")");
        return result;
    }

    public String sleep(String nMilliseconds) {
        Jumoke.log.info(">>");
        long nMilliseconds_ = (long) Marshal.deserialize(nMilliseconds);
        this.AU3_Sleep(nMilliseconds_);
        String result = Marshal.serialize(1L);
        Jumoke.log.info("<< sleep (" + nMilliseconds_ + ")");
        return result;
    }

    public String winActivate(String szTitle, String szText) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);
        this.AU3_WinActivate(szTitle_, szText_);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);
        String result = Marshal.serialize(1L);
        Jumoke.log.info("<< winActivate (" + szTitle_ + ", " + szText_ + ")");
        return result;
    }

    public String winClose(String szTitle, String szText) throws IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_SEVERE);
        int res = this.AU3_WinClose(szTitle_, szText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winClose (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winExists(String szTitle, String szText) {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_WinExists(szTitle_, szText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winExists (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winGetPosX(String szTitle, String szText) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_WinGetPosX(szTitle_, szText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winGetPosX (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winGetPosY(String szTitle, String szText) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_WinGetPosY(szTitle_, szText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winGetPosY (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winGetPosHeight(String szTitle, String szText) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_WinGetPosHeight(szTitle_, szText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winGetPosHeight (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winGetPosWidth(String szTitle, String szText) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);
        int res = this.AU3_WinGetPosWidth(szTitle_, szText_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winGetPosWidth (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winGetText(String szTitle, String szText) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");

        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));

        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);

        String res = "";
        int nBufSize = 131072;

        try {
            CharBuffer szRetText = CharBuffer.allocate(nBufSize);
            this.AU3_WinGetText(szTitle_, szText_, szRetText, nBufSize);
            res = szRetText.toString().trim();
        } catch (Exception e) {
            Jumoke.log.log(Level.SEVERE, e.toString(), e);
        }

        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winGetText (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }


    public String winGetTitle(String szTitle, String szText) throws IOException, ClassNotFoundException {
        Jumoke.log.info(">>");

        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));

        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_INFO);

        String res = "";
        int nBufSize = 131072;

        try {
            CharBuffer szRetText = CharBuffer.allocate(nBufSize);
            this.AU3_WinGetTitle(szTitle_, szText_, szRetText, nBufSize);
            res = szRetText.toString().trim();
        } catch (Exception e) {
            Jumoke.log.log(Level.SEVERE, e.toString(), e);
        }

        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winGetTitle (" + szTitle_ + ", " + szText_ + ") -> " + res);
        return result;
    }

    public String winSetState(String szTitle, String szText, String nFlags) {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        long nFlags_ = (long) Marshal.deserialize(nFlags);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);
        int res = this.AU3_WinSetState(szTitle_, szText_, nFlags_);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winSetState (" + szTitle_ + ", " + szText_ + ", " + nFlags_ + ") -> " + res);
        return result;
    }

    public String winWait(String szTitle, String szText, String nTimeout) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        long nTimeout_ = (long) Marshal.deserialize(nTimeout);

        int res = this.AU3_WinWait(szTitle_, szText_, nTimeout_);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);

        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winWait (" + szTitle_ + ", " + szText_ + ", " + nTimeout_ + ") -> " + res);
        return result;
    }

    public String winWaitActive(String szTitle, String szText, String nTimeout) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        long nTimeout_ = (long) Marshal.deserialize(nTimeout);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);
        int res = this.AU3_WinWaitActive(szTitle_, szText_, nTimeout_);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_WARNING);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winWaitActive (" + szTitle_ + ", " + szText_ + ", " + nTimeout_ + ") -> " + res);
        return result;
    }

    public String winWaitClose(String szTitle, String szText, String nTimeout) throws ClassNotFoundException, IOException {
        Jumoke.log.info(">>");
        WString szTitle_ = new WString((String) Marshal.deserialize(szTitle));
        WString szText_ = new WString((String) Marshal.deserialize(szText));
        long nTimeout_ = (long) Marshal.deserialize(nTimeout);
        this.highlightControl(szTitle_, szText_, new WString(""), HIGHLIGHT_FLASH_SEVERE);
        int res = this.AU3_WinWaitClose(szTitle_, szText_, nTimeout_);
        String result = Marshal.serialize(res);
        Jumoke.log.info("<< winWaitClose (" + szTitle_ + ", " + szText_ + ", " + nTimeout_ + ") -> " + res);
        return result;
    }


    @Override
    public int AU3_AutoItSetOption(WString szOption, int nValue) {
        return au3.AU3_AutoItSetOption(szOption, nValue);
    }

    @Override
    public int AU3_ControlClick(WString szTitle, WString szText, WString szControl, WString szButton, int nNumClicks, int nX, int nY) {
        return au3.AU3_ControlClick(szTitle, szText, szControl, szButton, nNumClicks, nX, nY);
    }

    @Override
    public void AU3_ClipGet(CharBuffer szClip, int nBufSize) {
        au3.AU3_ClipGet(szClip, nBufSize);
    }

    @Override
    public int AU3_MouseClick(WString szButton, int nX, int nY, int nClicks, int nSpeed) {
        return (int) au3.AU3_MouseClick(szButton, nX, nY, nClicks, nSpeed);
    }

    @Override
    public long AU3_MouseGetPosX() {
        return au3.AU3_MouseGetPosX();
    }

    @Override
    public long AU3_MouseGetPosY() {
        return au3.AU3_MouseGetPosY();
    }

    @Override
    public void AU3_ControlGetHandle(WString szTitle, WString szText, WString szControl, CharBuffer szRetText, int nBufSize) {
        au3.AU3_ControlGetHandle(szTitle, szText, szControl, szRetText, nBufSize);
    }

    @Override
    public void AU3_WinActivate(WString szTitle, WString szText) {
        au3.AU3_WinActivate(szTitle, szText);

    }

    @Override
    public int AU3_WinWaitActive(WString szTitle, WString szText, long nTimeout) {
        int ret = (int) au3.AU3_WinWaitActive(szTitle, szText, nTimeout);
        return ret;
    }

    @Override
    public int AU3_WinWaitClose(WString szTitle, WString szText, long nTimeout) {
        int ret = (int) au3.AU3_WinWaitClose(szTitle, szText, nTimeout);
        return ret;
    }

    @Override
    public void AU3_WinGetText(WString szTitle, WString szText, CharBuffer szRetText, int nBufSize) {
        au3.AU3_WinGetText(szTitle, szText, szRetText, nBufSize);
    }

    @Override
    public void AU3_WinGetTitle(WString szTitle, WString szText, CharBuffer szRetText, int nBufSize) {
        au3.AU3_WinGetTitle(szTitle, szText, szRetText, nBufSize);
    }

    @Override
    public void AU3_BlockInput(long nFlag) {
        au3.AU3_BlockInput(nFlag);
    }

    @Override
    public int AU3_Run(WString szRun, WString szDir, long nShowFlags) {
        return (int) au3.AU3_Run(szRun, szDir, nShowFlags);
    }

    @Override
    public int AU3_WinClose(WString szTitle, WString szText) {
        return (int) au3.AU3_WinClose(szTitle, szText);
    }

    @Override
    public void AU3_ControlCommand(WString szTitle, WString szText, WString szControl, WString szCommand, WString szExtra, CharBuffer szResult, int nBufSize) {
        au3.AU3_ControlCommand(szTitle, szText, szControl, szCommand, szExtra, szResult, nBufSize);
    }

    @Override
    public void AU3_ControlGetText(WString szTitle, WString szText, WString szControl, CharBuffer szControlText, int nBufSize) {
        au3.AU3_ControlGetText(szTitle, szText, szControl, szControlText, nBufSize);
    }

    @Override
    public int AU3_ControlSend(WString szTitle, WString szText, WString szControl, WString szSendText, long nMode) {
        return (int) au3.AU3_ControlSend(szTitle, szText, szControl, szSendText, nMode);
    }

    @Override
    public void AU3_Send(WString szSendText, long nMode) {
        au3.AU3_Send(szSendText, nMode);
    }

    @Override
    public void AU3_Sleep(long nMilliseconds) {
        au3.AU3_Sleep(nMilliseconds);
    }

    @Override
    public int AU3_ControlSetText(WString szTitle, WString szText, WString szControl, WString szControlText) {
        return (int) au3.AU3_ControlSetText(szTitle, szText, szControl, szControlText);
    }

    @Override
    public int AU3_ControlFocus(WString szTitle, WString szText, WString szControl) {
        return (int) au3.AU3_ControlFocus(szTitle, szText, szControl);
    }

    @Override
    public int AU3_WinSetState(WString szTitle, WString szText, long nFlags) {
        return (int) au3.AU3_WinSetState(szTitle, szText, nFlags);
    }

    @Override
    public int AU3_WinExists(WString szTitle, WString szText) {
        return (int) au3.AU3_WinExists(szTitle, szText);
    }

    @Override
    public int AU3_ControlGetPosX(WString szTitle, WString szText, WString szControl) {
        return (int) au3.AU3_ControlGetPosX(szTitle, szText, szControl);
    }

    @Override
    public int AU3_ControlGetPosY(WString szTitle, WString szText, WString szControl) {
        return (int) au3.AU3_ControlGetPosY(szTitle, szText, szControl);
    }

    @Override
    public int AU3_ControlGetPosHeight(WString szTitle, WString szText, WString szControl) {
        return (int) au3.AU3_ControlGetPosHeight(szTitle, szText, szControl);
    }

    @Override
    public int AU3_ControlGetPosWidth(WString szTitle, WString szText, WString szControl) {
        return (int) au3.AU3_ControlGetPosWidth(szTitle, szText, szControl);
    }

    @Override
    public void AU3_ControlTreeView(WString szTitle, WString szText, WString szControl, WString szCommand, WString szExtra1, WString szExtra2, CharBuffer szResult, int nBufSize) {
        au3.AU3_ControlTreeView(szTitle, szText, szControl, szCommand, szExtra1, szExtra2, szResult, nBufSize);

    }

    @Override
    public int AU3_WinWait(WString szTitle, WString szText, long nTimeout) {
        return au3.AU3_WinWait(szTitle, szText, nTimeout);
    }

    @Override
    public int AU3_WinGetPosX(WString szTitle, WString szText) {
        return (int) au3.AU3_WinGetPosX(szTitle, szText);
    }

    @Override
    public int AU3_WinGetPosY(WString szTitle, WString szText) {
        return (int) au3.AU3_WinGetPosY(szTitle, szText);
    }

    @Override
    public int AU3_WinGetPosHeight(WString szTitle, WString szText) {
        return (int) au3.AU3_WinGetPosHeight(szTitle, szText);
    }

    @Override
    public int AU3_WinGetPosWidth(WString szTitle, WString szText) {
        return (int) au3.AU3_WinGetPosWidth(szTitle, szText);
    }

    @Override
    public int AU3_MouseClickDrag(WString szButton, int nX1, int nY1, int nX2, int nY2, int nSpeed) {
        return (int) au3.AU3_MouseClickDrag(szButton, nX1, nY1, nX2, nY2, nSpeed);
    }
}
