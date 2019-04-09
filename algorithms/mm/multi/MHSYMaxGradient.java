/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.ByteImage;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.mm.gray.GInternGradient;
/*  9:   */ import vpt.util.Tools;
/* 10:   */ import vpt.util.se.FlatSE;
/* 11:   */ 
/* 12:   */ public class MHSYMaxGradient
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:21 */   public Image output = null;
/* 16:22 */   public Image input = null;
/* 17:23 */   public FlatSE se = null;
/* 18:   */   private int xdim;
/* 19:   */   private int ydim;
/* 20:   */   private Image lumGradient;
/* 21:   */   private Image satGradient;
/* 22:   */   
/* 23:   */   public MHSYMaxGradient()
/* 24:   */   {
/* 25:32 */     this.inputFields = "input,se";
/* 26:33 */     this.outputFields = "output";
/* 27:   */   }
/* 28:   */   
/* 29:   */   public void execute()
/* 30:   */     throws GlobalException
/* 31:   */   {
/* 32:38 */     this.xdim = this.input.getXDim();
/* 33:39 */     this.ydim = this.input.getYDim();
/* 34:   */     
/* 35:41 */     this.output = new ByteImage(this.xdim, this.ydim, 1);
/* 36:   */     
/* 37:43 */     this.lumGradient = GInternGradient.invoke(this.input.getChannel(2), this.se);
/* 38:44 */     this.satGradient = GInternGradient.invoke(this.input.getChannel(1), this.se);
/* 39:46 */     for (int y = 0; y < this.ydim; y++) {
/* 40:47 */       for (int x = 0; x < this.xdim; x++) {
/* 41:48 */         this.output.setXYDouble(x, y, getGradient(x, y, this.se.getCoords()));
/* 42:   */       }
/* 43:   */     }
/* 44:   */   }
/* 45:   */   
/* 46:   */   private double getGradient(int x, int y, Point[] coords)
/* 47:   */   {
/* 48:56 */     double maxDist = 0.0D;
/* 49:   */     
/* 50:   */ 
/* 51:59 */     double centralSaturation = this.input.getXYCDouble(x, y, 1);
/* 52:60 */     double centralHue = this.input.getXYCDouble(x, y, 0);
/* 53:62 */     for (int i = 0; i < coords.length; i++)
/* 54:   */     {
/* 55:64 */       int _x = x - coords[i].x;
/* 56:65 */       int _y = y - coords[i].y;
/* 57:67 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 58:   */       {
/* 59:69 */         double tmp = Tools.hueDistance(centralHue, this.input.getXYCDouble(_x, _y, 2)) * 2.0D;
/* 60:70 */         tmp *= 1.0D / ((1.0D + Math.exp(-10.0D * (this.input.getXYCDouble(_x, _y, 1) - 0.35D))) * (1.0D + Math.exp(-10.0D * (centralSaturation - 0.35D))));
/* 61:72 */         if (tmp > maxDist) {
/* 62:72 */           maxDist = tmp;
/* 63:   */         }
/* 64:   */       }
/* 65:   */     }
/* 66:75 */     return Math.max(maxDist, Math.max(this.lumGradient.getXYDouble(x, y), this.satGradient.getXYDouble(x, y)));
/* 67:   */   }
/* 68:   */   
/* 69:   */   public static Image invoke(Image image, FlatSE se)
/* 70:   */   {
/* 71:   */     try
/* 72:   */     {
/* 73:81 */       return (Image)new MHSYMaxGradient().preprocess(new Object[] { image, se });
/* 74:   */     }
/* 75:   */     catch (GlobalException e)
/* 76:   */     {
/* 77:83 */       e.printStackTrace();
/* 78:   */     }
/* 79:84 */     return null;
/* 80:   */   }
/* 81:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MHSYMaxGradient
 * JD-Core Version:    0.7.0.1
 */