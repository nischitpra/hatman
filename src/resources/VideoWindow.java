package resources;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import facedetection.VideoFaceDetection;
import javax.swing.JButton;

public class VideoWindow {

	private JFrame frame;
	VideoFaceDetection faceDetection;
	JLabel imageView;
	protected VideoCapture camera;
	Mat prev_frame=null;
	int count=0;
	boolean take_picture=false;
	
	public static void main(String[] args) {
		//initOpenCv();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					VideoWindow window = new VideoWindow();
					window.camera =new VideoCapture(0);
					window.frame.setVisible(true);
					Mat frame=new Mat();
					Mat prev_frame=new Mat();
					//CascadeClassifier face_cascade = new CascadeClassifier("haarcascade_frontalface_alt2.xml");
					CascadeClassifier face_cascade = new CascadeClassifier("C:/openCV/sources/data/haarcascades/haarcascade_frontalface_alt2.xml");
					
					
					Thread videoPlayer=new Thread(new Runnable(){

						@Override
						public void run() {
							while(true){
								if(window.camera.read(frame)){
									if(!frame.equals(prev_frame)){
										Mat small=new Mat();
										int pixel=128;
										Imgproc.resize(frame,small,new Size(pixel,frame.height()*pixel/frame.width()));
										double scale_x=frame.width()/small.width();
										double scale_y=frame.height()/small.height();
										
										URL img_url = getClass().getResource("/drawable/hat.png");
										String img_path = img_url.getPath();

									    if (img_path.startsWith("/")) {
									        img_path = img_path.substring(1);
									    }
										Mat goku=Highgui.imread(img_path);
										
										//Mat goku=Highgui.imread("drawable/hat.png");
										
										//---------------------
										MatOfRect faces=new MatOfRect();
										
										Mat frame_gray=new Mat();
										Imgproc.cvtColor(small, frame_gray,Imgproc.COLOR_BGR2GRAY);
										Imgproc.equalizeHist(frame_gray,frame_gray);
										face_cascade.detectMultiScale( frame_gray, faces);
										/*if(window.prev_frame!=null){
											Mat difference=new Mat();
											Core.absdiff(window.prev_frame, frame, difference);
											Highgui.imwrite("C:/Users/Nischit/Desktop/diff.jpg", difference);
										}
										window.prev_frame=frame.clone();
										*/
										for(Rect r:faces.toArray()){
											int y_min=(int) ((r.y)*scale_y);
											int y_max=(int) ((r.y+r.height)*scale_y);
											int x_min=(int) (r.x*scale_x);
											int x_max=(int) ((r.x+r.width)*scale_x);
											if(y_min<0)
												y_min=0;
											if(x_max>frame.width())
												x_max=frame.width();
											
											Mat overlay=createMaskMat(frame,goku,r.x*scale_x,r.y*scale_x,r.width*scale_x,r.height*scale_y);
											overlay.copyTo(frame,overlay);
											
											//Core.rectangle(frame, new Point(r.x*scale_x,r.y*scale_y), new Point((r.x+r.width)*scale_x,(r.y+r.height)*scale_y), new Scalar( 0, 125, 0 ),2);
											//Core.ellipse(frame, new Point((r.x+r.width/2)*scale_x,(r.y+r.height/2)*scale_y), new Size (r.width*scale_x/2.8,r.height*scale_y/1.8),0, 0, 360, new Scalar( 0, 0, 125 ),2);
											//face
											//Core.ellipse(frame, new Point((r.x+r.width/2)*scale_x,(r.y+r.height/2.8)*scale_y), new Size (r.width*scale_x/3,r.height*scale_y/10),0, 0, 360, new Scalar( 0, 125, 0 ),2);
											//Core.ellipse(frame, new Point((r.x+2*r.width/2.9)*scale_x,(r.y+r.height/2.6)*scale_y), new Size (r.width*scale_x/10,r.height*scale_y/15),0, 0, 360, new Scalar( 125, 125, 0 ),3);
											//left eye
											//Core.ellipse(frame, new Point((r.x+r.width/3.2)*scale_x,(r.y+r.height/2.6)*scale_y), new Size (r.width*scale_x/10,r.height*scale_y/15),0, 0, 360, new Scalar( 125, 125, 0 ),3);
											//right eye
											//Core.ellipse(frame, new Point((r.x+r.width/2)*scale_x,(r.y+2*r.height/2.45)*scale_y), new Size (r.width*scale_x/5,r.height*scale_y/12),0, 0, 360, new Scalar( 0, 125, 125 ),2);
											//lips
											Core.ellipse(frame, new Point((r.x+r.width/2)*scale_x,(r.y+r.height/2.3)*scale_y), new Size (r.width*scale_x/12,r.height*scale_y/12),180, 0, 180, new Scalar( 125, 0, 125 ),3);
											
											Core.ellipse(frame, new Point((r.x+2*r.width/2.9)*scale_x,(r.y+r.height/2.6)*scale_y), new Size (r.width*scale_x/8,r.height*scale_y/8),0, 0, 360, new Scalar( 125, 0, 125 ),3);
											//left eye
											Core.ellipse(frame, new Point((r.x+r.width/3.2)*scale_x,(r.y+r.height/2.6)*scale_y), new Size (r.width*scale_x/8,r.height*scale_y/8),0, 0, 360, new Scalar( 125, 0, 125 ),3);
											 
										}
										/*MatOfRect eyes=new MatOfRect();
										Mat h=frame.clone();
										Imgproc.cvtColor(frame, h,Imgproc.COLOR_BGR2GRAY);
										Imgproc.equalizeHist(h,h);
										eye_cascade.detectMultiScale(h, eyes);
										for(Rect r:eyes.toArray()){
											//Core.rectangle(frame, new Point(r.x,r.y), new Point((r.x+r.width),(r.y+r.height)), new Scalar( 0, 125, 0 ),2);
											Core.ellipse(frame, new Point((r.x+r.width/2),(r.y+r.height/2)), new Size (r.width/2.2,r.height/4.8),0, 0, 360, new Scalar( 125, 0, 125 ),2);
										}
										*/
										
										//----------------------------
										if(window.take_picture){
											//Highgui.imwrite("test"+window.count++ +".jpg", window.flip(frame));
											Highgui.imwrite("C:/Users/Nischit/Desktop/test"+window.count++ +".jpg", window.flip(frame));
											window.take_picture=false;
										}
										BufferedImage image= window.MatToBufferedImage(frame);
										window.setFrame(image);
									}
								}else{
									window.print("no frame to read");
								}
							}
						}

						private Mat createMaskMat(Mat frame,Mat pic, double x, double y, double width, double height) {
							Point head=new Point(x-width/5,y+height/6);
							width*=1.3;
							Imgproc.resize(pic, pic, new Size(width,pic.height()*width/pic.width()));
							Mat overlay=Mat.zeros(frame.size(), CvType.CV_8UC3);
							for(int i=pic.height()-1;i>0;i--){
								double ypos=head.y-(pic.height()-i);
								if(ypos<0)
									break;
								for(int j=0;j<pic.width();j++){
									double xpos=head.x+j;
										if(xpos>frame.width())
											continue;
									double[] data=pic.get(i,j);
									if(data[0]!=0||data[1]!=0||data[2]!=0){
										overlay.put((int)ypos, (int)xpos, data);
									}
								}
							}
							
							
							
							//width+=width/3;
							//if((pic.width()/3)<width){
							//	Imgproc.resize(pic, pic, new Size(1.8*width,pic.height()*1.8*width/pic.width()));
							//}
							/*Mat overlay=Mat.zeros(frame.size(), CvType.CV_8UC3);
							y=y-height*1.3;
							x=x-width*0.4;
							for(int i=pic.height()-1;i>0;i--){
								if((i+y)<0)
									continue;
								
								for(int j=0;j<pic.width()-1;j++){
									if((j+x)<0 ||(j+x)>frame.width())
										continue;
									
									double[] data=pic.get(i,j);
									if(data[0]!=0||data[1]!=0||data[2]!=0){
										overlay.put((int)(i+y), (int)(j+x), data);
									}
								}
							}
							*/
							return overlay;
							
						}
					});
			        window.camera.read(frame); 
					if(window.camera.isOpened()){
						videoPlayer.start();
					}else{
						window.print("cannot open camera");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public JFrame getFrame(){
		return frame;
	}

	public VideoWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("FaceDetection");
		frame.setBounds(100, 100, 750, 680);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
			    System.exit(0);
			}
		});
		
		imageView = new JLabel();
		imageView.setBounds(10, 11, 714, 619);
		frame.getContentPane().add(imageView);	
		
		JButton close_camera = new JButton("close camera");
		close_camera.setBounds(596, 0, 138, 23);
		close_camera.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				camera.release();
				
			}
			
		});
		frame.getContentPane().add(close_camera);
		
		JButton btnTakePhoto = new JButton("take photo");
		btnTakePhoto.setBounds(357, 0, 240, 23);
		btnTakePhoto.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				take_picture=true;
				
			}
			
		});
		frame.getContentPane().add(btnTakePhoto);
	}
	public void setFrame(BufferedImage img){
		if(img!=null){
			ImageIcon ii=new ImageIcon(img);
			imageView.setIcon(ii);
			frame.revalidate();
			frame.repaint();
		}
	}
	private BufferedImage MatToBufferedImage(Mat frame) {
		frame=flip(frame);
		int type=0;
		if(frame.channels()==1){
			type=BufferedImage.TYPE_BYTE_GRAY;
		}else if(frame.channels()==3){
			type=BufferedImage.TYPE_3BYTE_BGR;
		}
		
		BufferedImage image=new BufferedImage(frame.width(),frame.height(),type);
		frame.get(0, 0,((DataBufferByte)image.getRaster().getDataBuffer()).getData());
		return image;
	}
	private Mat flip(Mat frame){
		for(int i=0;i<frame.height();i++){
			for(int j=0;j<frame.width()/2;j++){
				double[] left=frame.get(i, j);	
				double[] right=frame.get(i,frame.width()-j-1);
				
				double[] data={right[0],right[1],right[2]};
				frame.put(i, j, data);
				
				double[] data1={left[0],left[1],left[2]};
				frame.put(i, frame.width()-j-1, data1);
			}
		}
		return frame;
	}
	private void print(String string) {
		System.out.println(string);
	}
	//required to lauch openCV in extranal file--------------------------------------
		public static void initOpenCv() {

		    setLibraryPath();

		    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		    System.out.println("OpenCV loaded. Version: " + Core.VERSION);

		}

		private static void setLibraryPath() { //this is for setting system path of opencv for exported jar. 

		    try {

		    	String systemType=System.getenv("ProgramFiles(x86)");
		    	if(systemType!=null){
		    		System.out.println("64bit");
		    		System.setProperty("java.library.path", "HatMan_lib/x64/");	// loads this dll to line number 66
		    	}else{
		    		System.out.println("32bit");
		    		System.setProperty("java.library.path", "HatMan_lib/x86/");	//loads this dll to line number 66
			    		
		    	}
		        
		        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		        fieldSysPath.setAccessible(true);
		        fieldSysPath.set(null, null);

		    } catch (Exception ex) {
		        ex.printStackTrace();
		        throw new RuntimeException(ex);
		    }

		}
}
