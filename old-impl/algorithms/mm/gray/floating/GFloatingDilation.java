/*  1:   */ package vpt.algorithms.mm.gray.floating;
/*  2:   */ 
/*  3:   */ import java.awt.geom.Point2D.Double;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FloatingFlatSE;
/*  8:   */ 
/*  9:   */ public class GFloatingDilation
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:15 */   public Image output = null;
/* 13:16 */   public Image input = null;
/* 14:17 */   public FloatingFlatSE se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:22 */   private final double MIN = -1.797693134862316E+308D;
/* 19:   */   
/* 20:   */   public GFloatingDilation()
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
/* 34:36 */     for (int x = 0; x < this.xdim; x++) {
/* 35:37 */       for (int y = 0; y < this.ydim; y++) {
/* 36:38 */         for (int c = 0; c < this.cdim; c++) {
/* 37:39 */           this.output.setXYCDouble(x, y, c, getMax(this.input, x, y, c, this.se.getCoords()));
/* 38:   */         }
/* 39:   */       }
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   private double getMax(Image img, int x, int y, int c, Point2D.Double[] coords)
/* 44:   */   {
/* 45:45 */     double max = -1.797693134862316E+308D;
/* 46:47 */     for (int i = 0; i < coords.length; i++)
/* 47:   */     {
/* 48:50 */       double _x = x - coords[i].x;
/* 49:51 */       double _y = y - coords[i].y;
/* 50:53 */       if ((_x >= 0.0D) && (_y >= 0.0D) && (_x < this.xdim) && (_y < this.ydim))
/* 51:   */       {
/* 52:56 */         double p = img.pgetXYCDouble(_x, _y, c);
/* 53:57 */         if (p > max) {
/* 54:57 */           max = p;
/* 55:   */         }
/* 56:   */       }
/* 57:   */     }
/* 58:61 */     return max > -1.797693134862316E+308D ? max : img.getXYCDouble(x, y, c);
/* 59:   */   }
/* 60:   */   
/* 61:   */   public static Image invoke(Image image, FloatingFlatSE se)
/* 62:   */   {
/* 63:   */     try
/* 64:   */     {
/* 65:67 */       return (Image)new GFloatingDilation().preprocess(new Object[] { image, se });
/* 66:   */     }
/* 67:   */     catch (GlobalException e)
/* 68:   */     {
/* 69:69 */       e.printStackTrace();
/* 70:   */     }
/* 71:70 */     return null;
/* 72:   */   }
/* 73:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.floating.GFloatingDilation
 * JD-Core Version:    0.7.0.1
 */