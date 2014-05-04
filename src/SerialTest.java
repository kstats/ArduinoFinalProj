import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Enumeration;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;   //Not used in this program//
import java.io.PrintStream;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Enumeration;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;




public class SerialTest implements SerialPortEventListener {
	SerialPort serialPort;
	JFreeChart objChart;
	int counter = 0;
	int gps = -1;
	String gps2 = "";
	String temp = "";
	String pressure = "";
	String humidity = "";
	String light = "";
	static int test = 1;
	Connection con;
	final XYSeriesCollection dataset2 = new XYSeriesCollection();
	XYDataset dataset = dataset2;
	private static final String PORT_NAMES[] = 
		{"COM1",                        // Windows Port
		"COM2",                        // Windows Port
		"COM3",                        // Windows Port
		"COM4",                        // Windows Port
		"COM5",                        // Windows Port
		"COM6",                        // Windows Port
		"COM7",                        // Windows Port
		"COM8",                        // Windows Port
		"COM9",                        // Windows Port
		"/dev/tty.usbserial-A9007UX1", // Mac OS X
		"/dev/ttyUSB0",                // Linux
		"/dev/ttyACM0",                // Ubuntu linux
		}; 
	//****************************************************//
	// A BufferedReader fed by a InputStreamReader        //
	// converting the bytes into characters               //
	// making the displayed results codepage independent  //
	//****************************************************//
	private BufferedReader input;
	private OutputStream output; //The output port stream //
	private static final int TIME_OUT = 2000; //Port open wait
	private static final int DATA_RATE = 9600; //Port bit rate
	DefaultCategoryDataset objDataset = new DefaultCategoryDataset();


	public void initialize() {

	    // Accessing Driver From Jar File
	    try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	    //DB Connection
	    try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","");
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  

		PrintStream out;
		try {
			
			out = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		//********************************************************//
		//First, Find an instance of serial port from PORT_NAMES. //
		//If used with the Arduino open hardware platform, the    //
		//port name used for serial communications between the    //
		//board and the host computer must be present within the  //
		//PORT_NAMES String array.                                //
		//********************************************************//
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = 
					(CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			//******************************************************//
			//Open serial port, and use class name for the appName. //
			//******************************************************//
			serialPort = (SerialPort)         
					portId.open(this.getClass().getName(),
							TIME_OUT);
			//******************************************************//
			// Set port parameters                                  //
			//******************************************************//
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			//******************************************************//
			// Open the streams                                     //
			//******************************************************//
			input = new BufferedReader(new 
					InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
			//******************************************************//
			// Add event listeners                                  //
			//******************************************************//
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
		}

	}

	//*********************************************************//
	// This should be called when you stop using the port.     //
	// This will prevent port locking on platforms like Linux. //
	//*********************************************************//
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	//*********************************************************//
	// Handle event on the serial port. Read and print data.   //
	//*********************************************************//
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {

				String inputLine=input.readLine();
				if(gps % 5 != 0 && gps != -1){
				   counter++;
				   if(gps%5 == 1)
				   {
					   temp = inputLine;
					   System.out.println(temp);
				   }
				   else if(gps%5 == 2)
				   {
					   humidity = inputLine;
					   System.out.println(humidity);
				   }
				   else if(gps%5 == 3)
				   {
					   pressure = inputLine;
					   System.out.println(pressure);
				   }
				   else if(gps%5 == 4)
				   {
					   light = inputLine;
					   System.out.println(light);
				   }

				  // double data = Double.parseDouble(inputLine);
					
					
					
					//temp,hum,bar

					gps++;

					//dataset = dataset2;
					/*objChart = ChartFactory.createXYLineChart(
							"Data",     //Chart title
							"Category",     //Domain axis label
							"Value",         //Range axis label
							dataset,
							PlotOrientation.VERTICAL, // orientation
							true,             // include legend?
							true,             // include tooltips?
							false             // include URLs?
							);
					XYPlot plot = objChart.getXYPlot();
					plot.setBackgroundPaint(Color.lightGray);
					//    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
					plot.setDomainGridlinePaint(Color.white);
					plot.setRangeGridlinePaint(Color.white);
					
					plot.configureDomainAxes();
					plot.configureRangeAxes();
					
					

					final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
					renderer.setSeriesLinesVisible(0, false);
					renderer.setSeriesShapesVisible(1, false);
					plot.setRenderer(renderer);

					// change the auto tick unit selection to integer units only...
					final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
					rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
					// OPTIONAL CUSTOMISATION COMPLETED.
					ChartFrame frame = new ChartFrame("Demo", objChart);

					frame.pack();
					frame.setVisible(true);*/
					
				}
				else if (gps == -1){
					gps++;
					gps2 = inputLine;
					System.out.println(gps2);
					
				}
				else {
					if(test != 1){
					PreparedStatement statement= con.prepareStatement("INSERT INTO `systems`.`tempmoi` (`id`, `temp`, `pressure`, `humidity`, `gps`, `Light`) "
				    		+ "VALUES ('"+test+"', '"+gps2+"', '"+humidity+"', '"+temp+"', '"+light+"', '"+pressure+"');");

				    //Creating Variable to execute query
				    int result=statement.executeUpdate(); }
				    gps2 = inputLine;
					System.out.println(gps2);
				    test++;
				    gps++;
				}
				
			} catch (Exception e) {
				
			}
		}
	}

	public static void main(String[] args) throws Exception {
		SerialTest st = new SerialTest();
		st.initialize();
		Thread t=new Thread() {
			public void run() {
				//******************************************************//
				// The following line will keep this app alive for 1000 //
				// seconds, waiting for events to occur and responding  //
				// to them (printing incoming messages to console).     //
				//******************************************************//
				try {
					Thread.sleep(1000000);
				} 
				catch (InterruptedException ie) {
				}
			}
		};
		t.start();
		System.out.println("Started");
	}
}