/*  1:   */ package vpt.algorithms.io;
/*  2:   */ 
/*  3:   */ import java.awt.image.BufferedImage;
/*  4:   */ import java.awt.image.ColorModel;
/*  5:   */ import java.awt.image.WritableRaster;
/*  6:   */ import java.io.File;
/*  7:   */ import java.io.IOException;
/*  8:   */ import javax.imageio.ImageIO;
/*  9:   */ import vpt.Algorithm;
/* 10:   */ import vpt.BooleanImage;
/* 11:   */ import vpt.ByteImage;
/* 12:   */ import vpt.GlobalException;
/* 13:   */ import vpt.Image;
/* 14:   */ 
/* 15:   */ public class Load
/* 16:   */   extends Algorithm
/* 17:   */ {
/* 18:26 */   public Image output = null;
/* 19:27 */   public String path = null;
/* 20:   */   
/* 21:   */   public Load()
/* 22:   */   {
/* 23:30 */     this.inputFields = "path";
/* 24:31 */     this.outputFields = "output";
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void execute()
/* 28:   */     throws GlobalException
/* 29:   */   {
/* 30:36 */     File f = new File(this.path);
/* 31:38 */     if (!f.canRead()) {
/* 32:38 */       throw new GlobalException("Cannot read file at " + this.path);
/* 33:   */     }
/* 34:40 */     BufferedImage bimage = null;
/* 35:   */     try
/* 36:   */     {
/* 37:43 */       bimage = ImageIO.read(f);
/* 38:   */     }
/* 39:   */     catch (IOException e)
/* 40:   */     {
/* 41:45 */       throw new GlobalException("Failed to load image from " + this.path);
/* 42:   */     }
/* 43:48 */     WritableRaster raster = bimage.getRaster();
/* 44:   */     
/* 45:50 */     int xdim = bimage.getWidth();
/* 46:51 */     int ydim = bimage.getHeight();
/* 47:52 */     int cdim = raster.getNumBands();
/* 48:   */     
/* 49:54 */     int pixelsize = bimage.getColorModel().getPixelSize();
/* 50:56 */     if (pixelsize / cdim == 8)
/* 51:   */     {
/* 52:57 */       this.output = new ByteImage(xdim, ydim, cdim);
/* 53:59 */       for (int x = 0; x < xdim; x++) {
/* 54:60 */         for (int y = 0; y < ydim; y++) {
/* 55:61 */           for (int c = 0; c < cdim; c++) {
/* 56:62 */             this.output.setXYCByte(x, y, c, raster.getSample(x, y, c));
/* 57:   */           }
/* 58:   */         }
/* 59:   */       }
/* 60:   */     }
/* 61:63 */     else if (pixelsize / cdim == 1)
/* 62:   */     {
/* 63:65 */       this.output = new BooleanImage(xdim, ydim, cdim);
/* 64:67 */       for (int x = 0; x < xdim; x++) {
/* 65:68 */         for (int y = 0; y < ydim; y++) {
/* 66:69 */           for (int c = 0; c < cdim; c++) {
/* 67:70 */             this.output.setXYCBoolean(x, y, c, raster.getSample(x, y, c) != 0);
/* 68:   */           }
/* 69:   */         }
/* 70:   */       }
/* 71:   */     }
/* 72:   */     else
/* 73:   */     {
/* 74:72 */       throw new GlobalException("Unsupported image type");
/* 75:   */     }
/* 76:   */   }
/* 77:   */   
/* 78:   */   public static Image invoke(String path)
/* 79:   */   {
/* 80:   */     try
/* 81:   */     {
/* 82:86 */       return (Image)new Load().preprocess(new Object[] { path });
/* 83:   */     }
/* 84:   */     catch (GlobalException e)
/* 85:   */     {
/* 86:89 */       e.printStackTrace();
/* 87:   */     }
/* 88:90 */     return null;
/* 89:   */   }
/* 90:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.io.Load
 * JD-Core Version:    0.7.0.1
 */