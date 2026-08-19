/*  1:   */ package vpt.algorithms.mm.gray.nonflat;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.NonFlatSE;
/*  8:   */ 
/*  9:   */ public class GNFDilation
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:18 */   public Image output = null;
/* 13:19 */   public Image input = null;
/* 14:20 */   public NonFlatSE se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:26 */   private final double MIN = -1.797693134862316E+308D;
/* 19:   */   
/* 20:   */   public GNFDilation()
/* 21:   */   {
/* 22:29 */     this.inputFields = "input,se";
/* 23:30 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:34 */     this.output = this.input.newInstance(false);
/* 30:   */     
/* 31:36 */     this.xdim = this.output.getXDim();
/* 32:37 */     this.ydim = this.output.getYDim();
/* 33:38 */     this.cdim = this.output.getCDim();
/* 34:40 */     for (int x = 0; x < this.xdim; x++) {
/* 35:41 */       for (int y = 0; y < this.ydim; y++) {
/* 36:42 */         for (int c = 0; c < this.cdim; c++) {
/* 37:43 */           this.output.setXYCDouble(x, y, c, getMax(this.input, x, y, c, this.se.getCoords()));
/* 38:   */         }
/* 39:   */       }
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   private double getMax(Image img, int x, int y, int c, Point[] coords)
/* 44:   */   {
/* 45:48 */     double max = -1.797693134862316E+308D;
/* 46:50 */     for (int i = 0; i < coords.length; i++)
/* 47:   */     {
/* 48:53 */       int _x = x - coords[i].x;
/* 49:54 */       int _y = y - coords[i].y;
/* 50:56 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 51:   */       {
/* 52:59 */         double p = img.getXYCDouble(_x, _y, c) + this.se.getXYDouble(coords[i].x + this.se.center.x, coords[i].y + this.se.center.y);
/* 53:60 */         if (p > max) {
/* 54:60 */           max = p;
/* 55:   */         }
/* 56:   */       }
/* 57:   */     }
/* 58:64 */     return max > -1.797693134862316E+308D ? max : img.getXYCDouble(x, y, c);
/* 59:   */   }
/* 60:   */   
/* 61:   */   public static Image invoke(Image image, NonFlatSE se)
/* 62:   */   {
/* 63:   */     try
/* 64:   */     {
/* 65:69 */       return (Image)new GNFDilation().preprocess(new Object[] { image, se });
/* 66:   */     }
/* 67:   */     catch (GlobalException e)
/* 68:   */     {
/* 69:71 */       e.printStackTrace();
/* 70:   */     }
/* 71:72 */     return null;
/* 72:   */   }
/* 73:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.nonflat.GNFDilation
 * JD-Core Version:    0.7.0.1
 */