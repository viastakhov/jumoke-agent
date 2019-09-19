package jumoke;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.WORD;


/**
 * DEVMODE java class implementation for WinAPI
 *
 * @author Astakhov Vladimir [VIAstakhov@mail.ru]
 * @version 1.0
 */
public class DEVMODE extends Structure {
    public DEVMODE() {
        super();
    }

    public DEVMODE(Pointer p) {
        super(p);
    }

    public byte[] dmDeviceName = new byte[64];
    public short dmSpecVersion;
    public short dmDriverVersion;
    public short dmSize;
    public short dmDriverExtra;
    public int dmFields;
    public short dmOrientation = 0;
    public short dmPaperSize = 0;
    public short dmPaperLength = 0;
    public short dmPaperWidth = 0;
    public short dmScale = 0;
    public short dmCopies = 0;
    public short dmDefaultScore = 0;
    public short dmPrintQuality = 0;
    public short dmColor = 0;
    public short dmDuplex = 0;
    public short dmYResolution = 0;
    public short dmTTOption = 0;
    public short dmCollate = 0;
    public byte[] dmFormName = new byte[64];
    public WORD dmLogPixels;
    public int dmBitsPerPel;
    public int dmPelsWidth;
    public int dmPelsHeight;
    public int dmNup;
    public int dmDisplayFrequency;
    public int dmICMMethod;
    public int dmICMIntent;
    public int dmMediaType;
    public int dmDitherType;
    public int dmReserved1;
    public int dmReserved2;
    public int dmPanningWidth;
    public int dmPanningHeight;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"dmDeviceName", "dmSpecVersion", "dmDriverVersion", "dmSize", "dmDriverExtra", "dmFields", "dmOrientation", "dmPaperSize", "dmPaperLength", "dmPaperWidth", "dmScale", "dmCopies", "dmDefaultScore", "dmPrintQuality", "dmColor", "dmDuplex", "dmYResolution", "dmTTOption", "dmCollate", "dmFormName", "dmLogPixels", "dmBitsPerPel", "dmPelsWidth", "dmPelsHeight", "dmNup", "dmDisplayFrequency", "dmICMMethod", "dmICMIntent", "dmMediaType", "dmDitherType", "dmReserved1", "dmReserved2", "dmPanningWidth", "dmPanningHeight"});
    }
}