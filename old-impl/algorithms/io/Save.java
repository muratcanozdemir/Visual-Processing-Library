/*   1:    */ package vpt.algorithms.io;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.awt.image.DataBufferByte;
/*   5:    */ import java.awt.image.SampleModel;
/*   6:    */ import java.awt.image.WritableRaster;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import javax.imageio.ImageIO;
/*  11:    */ import javax.media.jai.RasterFactory;
/*  12:    */ import vpt.Algorithm;
/*  13:    */ import vpt.GlobalException;
/*  14:    */ import vpt.Image;
/*  15:    */ 
/*  16:    */ public class Save
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 26 */   public Image inputImage = null;
/*  20: 27 */   public String filename = null;
/*  21:    */   
/*  22:    */   public Save()
/*  23:    */   {
/*  24: 30 */     this.inputFields = "inputImage,filename";
/*  25: 31 */     this.outputFields = "";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 35 */     BufferedImage bimg = null;
/*  32: 36 */     String extension = null;
/*  33:    */     
/*  34: 38 */     int channels = this.inputImage.getCDim();
/*  35: 39 */     int width = this.inputImage.getXDim();
/*  36: 40 */     int height = this.inputImage.getYDim();
/*  37:    */     
/*  38: 42 */     int size = this.inputImage.getSize();
/*  39: 44 */     if ((channels != 1) && (channels != 3)) {
/*  40: 45 */       throw new GlobalException("Cannot save anything other than greyscale or 3-channel color images, sorry :(");
/*  41:    */     }
/*  42: 47 */     byte[] pixels = new byte[width * height * channels];
/*  43: 49 */     for (int i = 0; i < pixels.length; i++) {
/*  44: 50 */       pixels[i] = ((byte)this.inputImage.getByte(i));
/*  45:    */     }
/*  46: 52 */     DataBufferByte dbb = new DataBufferByte(pixels, size);
/*  47: 54 */     if (channels == 3)
/*  48:    */     {
/*  49: 55 */       int[] channelOffsets = { 0, 1, 2 };
/*  50:    */       
/*  51: 57 */       SampleModel smodel = RasterFactory.createPixelInterleavedSampleModel(0, width, height, channels, channels * width, channelOffsets);
/*  52:    */       
/*  53: 59 */       WritableRaster raster = RasterFactory.createWritableRaster(smodel, dbb, null);
/*  54: 60 */       bimg = new BufferedImage(width, height, 5);
/*  55: 61 */       bimg.setData(raster);
/*  56:    */     }
/*  57:    */     else
/*  58:    */     {
/*  59: 64 */       SampleModel smodel = RasterFactory.createBandedSampleModel(0, width, height, 1);
/*  60:    */       
/*  61: 66 */       WritableRaster raster = RasterFactory.createWritableRaster(smodel, dbb, null);
/*  62: 67 */       bimg = new BufferedImage(width, height, 10);
/*  63: 68 */       bimg.setData(raster);
/*  64:    */     }
/*  65: 71 */     int index = this.filename.lastIndexOf('.');
/*  66: 72 */     if (index != -1) {
/*  67: 72 */       extension = this.filename.substring(index + 1).toLowerCase();
/*  68:    */     } else {
/*  69: 73 */       extension = "tiff";
/*  70:    */     }
/*  71: 75 */     File file = new File(this.filename);
/*  72:    */     try
/*  73:    */     {
/*  74: 78 */       if (!ImageIO.write(bimg, extension, file))
/*  75:    */       {
/*  76: 79 */         System.err.println("Error encountered with the desired file format, switching to 'tiff'");
/*  77: 80 */         ImageIO.write(bimg, "tiff", file);
/*  78:    */       }
/*  79:    */     }
/*  80:    */     catch (IOException e)
/*  81:    */     {
/*  82: 83 */       throw new GlobalException("Error encountered while saving : " + this.filename);
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static void invoke(Image img, String path)
/*  87:    */   {
/*  88:    */     try
/*  89:    */     {
/*  90: 98 */       new Save().preprocess(new Object[] { img, path });
/*  91:    */     }
/*  92:    */     catch (GlobalException e)
/*  93:    */     {
/*  94:101 */       e.printStackTrace();
/*  95:    */     }
/*  96:    */   }
/*  97:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.io.Save
 * JD-Core Version:    0.7.0.1
 */