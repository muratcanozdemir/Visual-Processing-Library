/*  1:   */ package vpt.algorithms.mm.gray.adaptive;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class AErosion
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:15 */   public Image output = null;
/* 13:16 */   public Image input = null;
/* 14:17 */   public FlatSE[][] se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:22 */   private final double MAX = 1.7976931348623157E+308D;
/* 19:   */   
/* 20:   */   public AErosion()
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
/* 34:36 */     if (this.cdim != 1) {
/* 35:36 */       throw new GlobalException("Only grayscale for now, sorry");
/* 36:   */     }
/* 37:38 */     for (int x = 0; x < this.xdim; x++) {
/* 38:39 */       for (int y = 0; y < this.ydim; y++) {
/* 39:41 */         this.output.setXYDouble(x, y, getMin(this.input, x, y, this.se[x][y].getCoords()));
/* 40:   */       }
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   private double getMin(Image img, int x, int y, Point[] coords)
/* 45:   */   {
/* 46:47 */     double min = 1.7976931348623157E+308D;
/* 47:49 */     for (int i = 0; i < coords.length; i++)
/* 48:   */     {
/* 49:54 */       int _x = x - coords[i].x;
/* 50:55 */       int _y = y - coords[i].y;
/* 51:57 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 52:   */       {
/* 53:60 */         double p = img.getXYDouble(_x, _y);
/* 54:61 */         if (p < min) {
/* 55:61 */           min = p;
/* 56:   */         }
/* 57:   */       }
/* 58:   */     }
/* 59:65 */     return min < 1.7976931348623157E+308D ? min : img.getXYDouble(x, y);
/* 60:   */   }
/* 61:   */   
/* 62:   */   public static Image invoke(Image image, FlatSE[][] se)
/* 63:   */   {
/* 64:   */     try
/* 65:   */     {
/* 66:71 */       return (Image)new AErosion().preprocess(new Object[] { image, se });
/* 67:   */     }
/* 68:   */     catch (GlobalException e)
/* 69:   */     {
/* 70:73 */       e.printStackTrace();
/* 71:   */     }
/* 72:74 */     return null;
/* 73:   */   }
/* 74:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.adaptive.AErosion
 * JD-Core Version:    0.7.0.1
 */