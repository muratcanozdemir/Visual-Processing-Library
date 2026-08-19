/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class LabelsToMeanHue
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image labelImage;
/* 12:   */   public Image original;
/* 13:   */   public Image output;
/* 14:   */   
/* 15:   */   public LabelsToMeanHue()
/* 16:   */   {
/* 17:24 */     this.inputFields = "labelImage,original";
/* 18:25 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:29 */     int xDim = this.original.getXDim();
/* 25:30 */     int yDim = this.original.getYDim();
/* 26:32 */     if ((this.original.getCDim() != 1) || (this.labelImage.getCDim() != 1)) {
/* 27:33 */       throw new GlobalException("The input and label images must be mono-channel!");
/* 28:   */     }
/* 29:35 */     this.output = this.original.newInstance(false);
/* 30:   */     
/* 31:   */ 
/* 32:38 */     Point p = this.labelImage.getXYCMaximum(0);
/* 33:39 */     double[][] labels = new double[this.labelImage.getXYInt(p.x, p.y) + 1][4];
/* 34:44 */     for (int x = 0; x < xDim; x++) {
/* 35:45 */       for (int y = 0; y < yDim; y++)
/* 36:   */       {
/* 37:46 */         int label = this.labelImage.getXYInt(x, y);
/* 38:   */         
/* 39:48 */         labels[label][0] += Math.sin(this.original.getXYDouble(x, y) * 3.141592653589793D * 2.0D);
/* 40:49 */         labels[label][1] += Math.cos(this.original.getXYDouble(x, y) * 3.141592653589793D * 2.0D);
/* 41:   */       }
/* 42:   */     }
/* 43:54 */     for (int i = 0; i < labels.length; i++)
/* 44:   */     {
/* 45:55 */       labels[i][2] = (Math.atan2(labels[i][0], labels[i][1]) / 6.283185307179586D);
/* 46:56 */       if (labels[i][2] < 0.0D) {
/* 47:56 */         labels[i][2] += 1.0D;
/* 48:   */       }
/* 49:   */     }
/* 50:60 */     for (int x = 0; x < xDim; x++) {
/* 51:61 */       for (int y = 0; y < yDim; y++)
/* 52:   */       {
/* 53:62 */         int label = this.labelImage.getXYInt(x, y);
/* 54:63 */         this.output.setXYDouble(x, y, labels[label][2]);
/* 55:   */       }
/* 56:   */     }
/* 57:   */   }
/* 58:   */   
/* 59:   */   public static Image invoke(Image labelImage, Image original)
/* 60:   */   {
/* 61:   */     try
/* 62:   */     {
/* 63:70 */       return (Image)new LabelsToMeanHue().preprocess(new Object[] { labelImage, original });
/* 64:   */     }
/* 65:   */     catch (GlobalException e)
/* 66:   */     {
/* 67:72 */       e.printStackTrace();
/* 68:   */     }
/* 69:73 */     return null;
/* 70:   */   }
/* 71:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.LabelsToMeanHue
 * JD-Core Version:    0.7.0.1
 */