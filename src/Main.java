import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class Main extends MouseAdapter{
	private static Point topLeft;
	private static Point bottomRight;
	private static Point nextPage;
	private static int index = 0;
	
	private static void getPoints(){
		JPanel panel = new JPanel();
		panel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

		panel.setBackground(new Color(224,224,224,60));
		panel.addMouseListener(new Main());
		JFrame frame = new JFrame("Location Window");
		
		frame.setLocation(0,0);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0,0,0,0));
		
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		while(index < 3){
			try{
				Thread.sleep(10);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		frame.setVisible(false);
	}
	
	public void mouseClicked(MouseEvent me) {
	    int screenX = me.getXOnScreen();
	    int screenY = me.getYOnScreen();
	    //System.out.println(index);
	    switch(index){
	    	case 0:
	    		topLeft = new Point(screenX, screenY);
	    		System.out.println("Top Left: " + screenX + "," + screenY);
	    		index++;
	    		break;
	    	case 1:
	    		bottomRight = new Point(screenX, screenY);
	    		System.out.println("Bottom Right: " + screenX + "," + screenY);
	    		index++;
	    		break;
	    	case 2:
	    		nextPage = new Point(screenX, screenY);
	    		System.out.println("Next Page: " + screenX + "," + screenY);
	    		index++;
	    		break;
	    	default:
	    		break;
	    }
	  }
	
	private static void goToNextPage(){
		Robot robot;
		try{
			robot = new Robot();
			robot.mouseMove((int)nextPage.getX(),(int)nextPage.getY());
			robot.mousePress(InputEvent.BUTTON1_MASK);
		    robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}catch (AWTException e){
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws IOException, AWTException, DocumentException, InterruptedException {
		getPoints();
	
		int width = (int)(bottomRight.getX() - topLeft.getX());
		int height = (int)(bottomRight.getY() - topLeft.getY());
		
		Robot robot = new Robot();

		Dimension screenSize = new Dimension(width, height);
		Rectangle captureRect = new Rectangle(topLeft, screenSize);

		com.itextpdf.text.Rectangle pdfRect = new com.itextpdf.text.Rectangle((float)width, (float)height);

		Document doc = new Document(pdfRect);
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("C:/Users/Nyarlathotep/Workspace/KindleScreenshotToPDF/MATH2410.pdf"));
		doc.open(); 
	    PdfContentByte cb = writer.getDirectContent(); 
	    
		for(int i=0; i<835+24-1; i++){
			if(i==1)
				Thread.sleep(1000);
			
			PdfTemplate pdfTemplate = cb.createTemplate(width, height); 
			BufferedImage image = robot.createScreenCapture(captureRect);
			Image img = Image.getInstance(image, null);
			img.setAbsolutePosition(0f, 0f);
			
			pdfTemplate.addImage(img); 
			cb.addTemplate(pdfTemplate, 0,20);
			doc.newPage();
			goToNextPage();
			Thread.sleep(150);
		}
		
		doc.close();
		
		System.out.println("Finished");
	}
}
