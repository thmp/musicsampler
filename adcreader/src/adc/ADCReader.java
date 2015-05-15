package adc;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.util.Arrays;

import static spark.Spark.get;

/**
 * Read an Analog to Digital Converter
 */
public class ADCReader
{

    public ADCReader() {
        /*this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Test Sound Clip");
        this.setSize(300, 200);
        this.setVisible(true);*/
    }

    private final static boolean DISPLAY_DIGIT = false;
    private final static boolean DEBUG         = false;
    // Note: "Mismatch" 23-24. The wiring says DOUT->#23, DIN->#24
    // 23: DOUT on the ADC is IN on the GPIO. ADC:Slave, GPIO:Master
    // 24: DIN on the ADC, OUT on the GPIO. Same reason as above.
    // SPI: Serial Peripheral Interface
    private static Pin spiClk  = RaspiPin.GPIO_01; // Pin #18, clock
    private static Pin spiMiso = RaspiPin.GPIO_04; // Pin #23, data in.  MISO: Master In Slave Out
    private static Pin spiMosi = RaspiPin.GPIO_05; // Pin #24, data out. MOSI: Master Out Slave In
    private static Pin spiCs   = RaspiPin.GPIO_06; // Pin #25, Chip Select

    private enum MCP3008_input_channels
    {
        CH0(0),
        CH1(1),
        CH2(2),
        CH3(3),
        CH4(4),
        CH5(5),
        CH6(6),
        CH7(7);

        private int ch;

        MCP3008_input_channels(int chNum)
        {
            this.ch = chNum;
        }

        public int ch() { return this.ch; }
    }

    private static int ADC_CHANNEL = MCP3008_input_channels.CH0.ch(); // Between 0 and 7, 8 channels on the MCP3008

    private static GpioPinDigitalInput  misoInput        = null;
    private static GpioPinDigitalOutput mosiOutput       = null;
    private static GpioPinDigitalOutput clockOutput      = null;
    private static GpioPinDigitalOutput chipSelectOutput = null;

    private static int[] volume = new int[] {0, 0, 0, 0, 0, 0, 0, 0};

    private static boolean go = true;

    public void start()
    {

    }

    public static void main(String[] args)
    {

        get("/sensors", (req, res) -> Arrays.toString(volume) );

        ADCReader reader = new ADCReader();
        reader.start();

        GpioController gpio = GpioFactory.getInstance();
        mosiOutput       = gpio.provisionDigitalOutputPin(spiMosi, "MOSI", PinState.LOW);
        clockOutput      = gpio.provisionDigitalOutputPin(spiClk,  "CLK",  PinState.LOW);
        chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs,   "CS",   PinState.LOW);

        misoInput        = gpio.provisionDigitalInputPin(spiMiso, "MISO");

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("Shutting down.");
                go = false;
            }
        });
        int[] lastRead  = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
        int tolerance = 5;
        while (go)
        {
            for (int i = 0; i < lastRead.length; i++) {
                int adc = 0;
                switch(i) {
                    case 0:
                        adc = readAdc(MCP3008_input_channels.CH0.ch());
                        break;
                    case 1:
                        adc = readAdc(MCP3008_input_channels.CH1.ch());
                        break;
                    case 2:
                        adc = readAdc(MCP3008_input_channels.CH2.ch());
                        break;
                    case 3:
                        adc = readAdc(MCP3008_input_channels.CH3.ch());
                        break;
                    case 4:
                        adc = readAdc(MCP3008_input_channels.CH4.ch());
                        break;
                    case 5:
                        adc = readAdc(MCP3008_input_channels.CH5.ch());
                        break;
                    case 6:
                        adc = readAdc(MCP3008_input_channels.CH6.ch());
                        break;
                    case 7:
                        adc = readAdc(MCP3008_input_channels.CH7.ch());
                        break;
                }
                int postAdjust = Math.abs(adc - lastRead[i]);
                if (postAdjust > tolerance) {
                    volume[i] = 100 - (int) (adc / 10.23); // [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
                    if (DEBUG)
                        System.out.println("readAdc:" + Integer.toString(adc) +
                                " (0x" + lpad(Integer.toString(adc, 16).toUpperCase(), "0", 2) +
                                ", 0&" + lpad(Integer.toString(adc, 2), "0", 8) + ")");
                    System.out.println("Channel "+i+":" + volume[i] + "% (" + adc + ")");
                    lastRead[i] = adc;
                }
            }
            try { Thread.sleep(10L); } catch (InterruptedException ie) { ie.printStackTrace(); }
        }
        System.out.println("Bye...");
        gpio.shutdown();
    }

    private static int readAdc(int channel)
    {
        chipSelectOutput.high();

        clockOutput.low();
        chipSelectOutput.low();

        //int adccommand = ADC_CHANNEL;
        int adccommand = channel;
        adccommand |= 0x18; // 0x18: 00011000
        adccommand <<= 3;
        // Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
        for (int i=0; i<5; i++) //
        {
            if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
                mosiOutput.high();
            else
                mosiOutput.low();
            adccommand <<= 1;
            clockOutput.high();
            clockOutput.low();
        }

        int adcOut = 0;
        for (int i=0; i<12; i++) // Read in one empty bit, one null bit and 10 ADC bits
        {
            clockOutput.high();
            clockOutput.low();
            adcOut <<= 1;

            if (misoInput.isHigh())
            {
//      System.out.println("    " + misoInput.getName() + " is high (i:" + i + ")");
                // Shift one bit on the adcOut
                adcOut |= 0x1;
            }
            if (DISPLAY_DIGIT)
                System.out.println("ADCOUT: 0x" + Integer.toString(adcOut, 16).toUpperCase() +
                        ", 0&" + Integer.toString(adcOut, 2).toUpperCase());
        }
        chipSelectOutput.high();

        adcOut >>= 1; // Drop first bit
        return adcOut;
    }

    private static String lpad(String str, String with, int len)
    {
        String s = str;
        while (s.length() < len)
            s = with + s;
        return s;
    }
}