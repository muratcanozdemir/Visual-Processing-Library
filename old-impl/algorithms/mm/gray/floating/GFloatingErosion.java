/*  1:   */ package vpt.algorithms.mm.gray.floating;
/*  2:   */ 
/*  3:   */ import java.awt.geom.Point2D.Double;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FloatingFlatSE;
/*  8:   */ 
/*  9:   */ public class GFloatingErosion
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:15 */   public Image output = null;
/* 13:16 */   public Image input = null;
/* 14:17 */   public FloatingFlatSE se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:22 */   private final double MAX = 1.7976931348623157E+308D;
/* 19:   */   
/* 20:   */   public GFloatingErosion()
/* 21:   */   {
/* 22:25 */     this.inputFields = "input,se";
/* 23:26 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:30 */     this.output = this.input.newInstance(false);
/* 30:   */     
/* 31:32 */     this.xdim = this.output.getXDim();
/* 32:33 */     this.ydim = this.output.getYDim();
/* 33:34 */     this.cdim = this.output.getCDim();
/* 34:   */     
/* 35:36 */     this.se = this.se.reflection();
/* 36:38 */     for (int x = 0; x < this.xdim; x++) {
/* 37:39 */       for (int y = 0; y < this.ydim; y++) {
/* 38:40 */         for (int c = 0; c < this.cdim; c++) {
/* 39:41 */           this.output.setXYCDouble(x, y, c, getMin(this.input, x, y, c, this.se.getCoords()));
/* 40:   */         }
/* 41:   */       }
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   private double getMin(Image img, int x, int y, int c, Point2D.Double[] coords)
/* 46:   */   {
/* 47:47 */     double min = 1.7976931348623157E+308D;
/* 48:49 */     for (int i = 0; i < coords.length; i++)
/* 49:   */     {
/* 50:54 */       double _x = x - coords[i].x;
/* 51:55 */       double _y = y - coords[i].y;
/* 52:57 */       if ((_x >= 0.0D) && (_y >= 0.0D) && (_x < this.xdim) && (_y < this.ydim))
/* 53:   */       {
/* 54:60 */         double p = img.pgetXYCDouble(_x, _y, c);
/* 55:61 */         if (p < min) {
/* 56:61 */           min = p;
/* 57:   */         }
/* 58:   */       }
/* 59:   */     }
/* 60:65 */     return min < 1.7976931348623157E+308D ? min : img.getXYCDouble(x, y, c);
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static Image invoke(Image image, FloatingFlatSE se)
/* 64:   */   {
/* 65:   */     try
/* 66:   */     {
/* 67:71 */       return (Image)new GFloatingErosion().preprocess(new Object[] { image, se });
/* 68:   */     }
/* 69:   */     catch (GlobalException e)
/* 70:   */     {
/* 71:73 */       e.printStackTrace();
/* 72:   */     }
/* 73:74 */     return null;
/* 74:   */   }
/* 75:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.floating.GFloatingErosion
 * JD-Core Version:    0.7.0.1
 */