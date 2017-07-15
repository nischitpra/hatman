package facedetection;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class VideoFaceDetection {
	CascadeClassifier face_cascade=new CascadeClassifier();
	CascadeClassifier eyes_cascade=new CascadeClassifier();
	BufferedImage finalImage=null;
	public VideoFaceDetection(){
		VideoCapture camera=new VideoCapture(0);
		Mat frame=new Mat();
		//capture.open(0);
		if(camera.isOpened()){
			while(true){
				if(camera.read(frame)){
					detectAndDisplay(frame);
				}else{
					print("no frame captured");
					break;
				}
			}
		}
	}
	private void detectAndDisplay(Mat frame) {
		MatOfRect faces=new MatOfRect();
		Mat frame_gray=new Mat();
		
		Imgproc.cvtColor(frame, frame_gray,Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(frame_gray,frame_gray);
		
		face_cascade.detectMultiScale(frame_gray, faces);
		for(Rect r:faces.toArray()){
			Core.rectangle(frame, new Point(r.x,r.y), new Point(r.x+r.width,r.y+r.height), new Scalar( 255, 0, 255 ));
		}
		finalImage=convertToBuffer(frame);
	}
	private BufferedImage convertToBuffer(Mat image){
		BufferedImage img=new BufferedImage(image.width(),image.height(),BufferedImage.TYPE_3BYTE_BGR);
		image.get(0, 0,((DataBufferByte)img.getRaster().getDataBuffer()).getData());
		return img;	
	}
	public BufferedImage getImage(){
		return finalImage;
	}
	void print(String s){
		System.out.println(s);
	}
}
