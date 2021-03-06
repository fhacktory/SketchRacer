package com.fhacktory.sketchracer;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs; // imread, imwrite, etc

public class ContourUtils {

    static{ System.loadLibrary("opencv_java3"); }

    private String path;

    public ContourUtils(String path)
    {
        this.path = path;
    }

    public List<MatOfPoint> computeContours() {

        Log.d("ContourUtils", "Now scanning picture: " + this.path);

        // load opencv library
        Mat image = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Imgproc.medianBlur(image,image,9);

        double[] center = {(double)image.width()/2,(double)image.height()/2};
        Point image_center = new Point(center);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy  = new Mat();
        Mat res = image.clone();
        int lowThreshold = 10, highThreshold = 50;
        do {
            Imgproc.Canny(image, res, lowThreshold, highThreshold, 3, true);
            lowThreshold--;
            highThreshold+=5;
        }
        while(res.empty() && lowThreshold > 0 && highThreshold > 0);
        if(res.empty()) return contours;
        image = res;
        int dilation_size = 5;
        int erosion_size = 1;
        //Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
        //Imgproc.erode(image, image, element);
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
        Imgproc.dilate(image, image, element1);
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);

        Log.d("ContourUtils", contours.size() + " contours found");
        return contours;
    }
}
