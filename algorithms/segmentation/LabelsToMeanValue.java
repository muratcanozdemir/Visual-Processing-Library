/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ 
/*  8:   */ public class LabelsToMeanValue
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image labelImage;
/* 12:   */   public Image baseImage;
/* 13:   */   public Image outputImage;
/* 14:   */   
/* 15:   */   public LabelsToMeanValue()
/* 16:   */   {
/* 17:24 */     this.inputFields = "labelImage,baseImage";
/* 18:25 */     this.outputFields = "outputImage";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:29 */     this.outputImage = this.baseImage.newInstance(false);
/* 25:   */     
/* 26:31 */     int xDim = this.outputImage.getXDim();
/* 27:32 */     int yDim = this.outputImage.getYDim();
/* 28:33 */     int cDim = this.outputImage.getCDim();
/* 29:   */     
/* 30:   */ 
/* 31:36 */     Point p = this.labelImage.getXYCMaximum(0);
/* 32:37 */     double[][] labels = new double[this.labelImage.getXYInt(p.x, p.y) + 1][cDim + 1];
/* 33:40 */     for (int x = 0; x < xDim; x++) {
/* 34:41 */       for (int y = 0; y < yDim; y++)
/* 35:   */       {
/* 36:42 */         int label = this.labelImage.getXYInt(x, y);
/* 37:   */         
/* 38:   */ 
/* 39:   */ 
/* 40:   */ 
/* 41:47 */         labels[label][0] += 1.0D;
/* 42:49 */         for (int c = 0; c < cDim; c++) {
/* 43:50 */           labels[label][(c + 1)] += this.baseImage.getXYCDouble(x, y, c);
/* 44:   */         }
/* 45:   */       }
/* 46:   */     }
/* 47:56 */     for (int i = 0; i < labels.length; i++) {
/* 48:57 */       for (int c = 0; c < cDim; c++) {
/* 49:58 */         labels[i][(c + 1)] /= labels[i][0];
/* 50:   */       }
/* 51:   */     }
/* 52:63 */     for (int x = 0; x < xDim; x++) {
/* 53:64 */       for (int y = 0; y < yDim; y++)
/* 54:   */       {
/* 55:65 */         int label = this.labelImage.getXYInt(x, y);
/* 56:67 */         for (int c = 0; c < cDim; c++) {
/* 57:68 */           this.outputImage.setXYCDouble(x, y, c, labels[label][(c + 1)]);
/* 58:   */         }
/* 59:   */       }
/* 60:   */     }
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static Image invoke(Image labels, Image original)
/* 64:   */   {
/* 65:   */     try
/* 66:   */     {
/* 67:76 */       return (Image)new LabelsToMeanValue().preprocess(new Object[] { labels, original });
/* 68:   */     }
/* 69:   */     catch (GlobalException e)
/* 70:   */     {
/* 71:78 */       e.printStackTrace();
/* 72:   */     }
/* 73:79 */     return null;
/* 74:   */   }
/* 75:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.LabelsToMeanValue
 * JD-Core Version:    0.7.0.1
 */