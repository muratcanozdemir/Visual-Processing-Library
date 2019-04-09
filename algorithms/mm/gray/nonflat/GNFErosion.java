/*  1:   */ package vpt.algorithms.mm.gray.nonflat;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.NonFlatSE;
/*  8:   */ 
/*  9:   */ public class GNFErosion
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Image output = null;
/* 13:18 */   public Image input = null;
/* 14:19 */   public NonFlatSE se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:24 */   private final double MAX = 1.7976931348623157E+308D;
/* 19:   */   
/* 20:   */   public GNFErosion()
/* 21:   */   {
/* 22:27 */     this.inputFields = "input,se";
/* 23:28 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:32 */     this.output = this.input.newInstance(false);
/* 30:   */     
/* 31:34 */     this.xdim = this.output.getXDim();
/* 32:35 */     this.ydim = this.output.getYDim();
/* 33:36 */     this.cdim = this.output.getCDim();
/* 34:   */     
/* 35:   */ 
/* 36:39 */     this.se = this.se.reflection();
/* 37:41 */     for (int x = 0; x < this.xdim; x++) {
/* 38:42 */       for (int y = 0; y < this.ydim; y++) {
/* 39:43 */         for (int c = 0; c < this.cdim; c++) {
/* 40:44 */           this.output.setXYCDouble(x, y, c, getMin(this.input, x, y, c, this.se.getCoords()));
/* 41:   */         }
/* 42:   */       }
/* 43:   */     }
/* 44:   */   }
/* 45:   */   
/* 46:   */   private double getMin(Image img, int x, int y, int c, Point[] coords)
/* 47:   */   {
/* 48:49 */     double min = 1.7976931348623157E+308D;
/* 49:51 */     for (int i = 0; i < coords.length; i++)
/* 50:   */     {
/* 51:56 */       int _x = x - coords[i].x;
/* 52:57 */       int _y = y - coords[i].y;
/* 53:59 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 54:   */       {
/* 55:62 */         double p = img.getXYCDouble(_x, _y, c) - this.se.getXYDouble(coords[i].x + this.se.center.x, coords[i].y + this.se.center.y);
/* 56:63 */         if (p < min) {
/* 57:63 */           min = p;
/* 58:   */         }
/* 59:   */       }
/* 60:   */     }
/* 61:67 */     return min < 1.7976931348623157E+308D ? min : img.getXYCDouble(x, y, c);
/* 62:   */   }
/* 63:   */   
/* 64:   */   public static Image invoke(Image image, NonFlatSE se)
/* 65:   */   {
/* 66:   */     try
/* 67:   */     {
/* 68:73 */       return (Image)new GNFErosion().preprocess(new Object[] { image, se });
/* 69:   */     }
/* 70:   */     catch (GlobalException e)
/* 71:   */     {
/* 72:75 */       e.printStackTrace();
/* 73:   */     }
/* 74:76 */     return null;
/* 75:   */   }
/* 76:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.nonflat.GNFErosion
 * JD-Core Version:    0.7.0.1
 */