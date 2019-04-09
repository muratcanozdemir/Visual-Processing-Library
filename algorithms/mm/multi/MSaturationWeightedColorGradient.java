/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.ByteImage;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MSaturationWeightedColorGradient
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:20 */   public Image output = null;
/* 15:21 */   public Image input = null;
/* 16:22 */   public FlatSE se = null;
/* 17:   */   private int xdim;
/* 18:   */   private int ydim;
/* 19:   */   
/* 20:   */   public MSaturationWeightedColorGradient()
/* 21:   */   {
/* 22:28 */     this.inputFields = "input,se";
/* 23:29 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:34 */     this.xdim = this.input.getXDim();
/* 30:35 */     this.ydim = this.input.getYDim();
/* 31:   */     
/* 32:37 */     this.output = new ByteImage(this.xdim, this.ydim, 1);
/* 33:39 */     for (int y = 0; y < this.ydim; y++) {
/* 34:40 */       for (int x = 0; x < this.xdim; x++) {
/* 35:41 */         this.output.setXYDouble(x, y, getGradient(x, y, this.se.getCoords()));
/* 36:   */       }
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   private double getGradient(int x, int y, Point[] coords)
/* 41:   */   {
/* 42:49 */     double maxLumDist = 0.0D;
/* 43:50 */     double maxHueDist = 0.0D;
/* 44:51 */     double maxDist = 0.0D;
/* 45:   */     
/* 46:   */ 
/* 47:54 */     double centralLuminance = this.input.getXYCDouble(x, y, 2);
/* 48:55 */     double centralSaturation = this.input.getXYCDouble(x, y, 1);
/* 49:56 */     double centralHue = this.input.getXYCDouble(x, y, 0);
/* 50:58 */     for (int i = 0; i < coords.length; i++)
/* 51:   */     {
/* 52:60 */       int _x = x - coords[i].x;
/* 53:61 */       int _y = y - coords[i].y;
/* 54:63 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 55:   */       {
/* 56:66 */         double tmp = Math.abs(centralLuminance - this.input.getXYCDouble(_x, _y, 2));
/* 57:67 */         if (tmp > maxLumDist) {
/* 58:67 */           maxLumDist = tmp;
/* 59:   */         }
/* 60:70 */         tmp = Tools.hueDistance(centralHue, this.input.getXYCDouble(_x, _y, 2)) * 2.0D;
/* 61:71 */         if (tmp > maxHueDist) {
/* 62:71 */           maxHueDist = tmp;
/* 63:   */         }
/* 64:74 */         double combined = maxLumDist * (1.0D - centralSaturation) + maxHueDist * centralSaturation;
/* 65:75 */         if (combined > maxDist) {
/* 66:75 */           maxDist = combined;
/* 67:   */         }
/* 68:   */       }
/* 69:   */     }
/* 70:78 */     return maxDist;
/* 71:   */   }
/* 72:   */   
/* 73:   */   public static Image invoke(Image image, FlatSE se)
/* 74:   */   {
/* 75:   */     try
/* 76:   */     {
/* 77:84 */       return (Image)new MSaturationWeightedColorGradient().preprocess(new Object[] { image, se });
/* 78:   */     }
/* 79:   */     catch (GlobalException e)
/* 80:   */     {
/* 81:86 */       e.printStackTrace();
/* 82:   */     }
/* 83:87 */     return null;
/* 84:   */   }
/* 85:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MSaturationWeightedColorGradient
 * JD-Core Version:    0.7.0.1
 */