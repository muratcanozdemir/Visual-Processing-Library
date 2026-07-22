/*  1:   */ package vpt.algorithms.mm.gray.adaptive;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class ADilation
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Image output = null;
/* 13:18 */   public Image input = null;
/* 14:19 */   public FlatSE[][] se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:25 */   private final double MIN = -1.797693134862316E+308D;
/* 19:   */   
/* 20:   */   public ADilation()
/* 21:   */   {
/* 22:28 */     this.inputFields = "input,se";
/* 23:29 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:33 */     this.output = this.input.newInstance(false);
/* 30:   */     
/* 31:35 */     this.xdim = this.output.getXDim();
/* 32:36 */     this.ydim = this.output.getYDim();
/* 33:37 */     this.cdim = this.output.getCDim();
/* 34:39 */     if (this.cdim != 1) {
/* 35:39 */       throw new GlobalException("Only grayscale for now.. sorry");
/* 36:   */     }
/* 37:41 */     for (int x = 0; x < this.xdim; x++) {
/* 38:42 */       for (int y = 0; y < this.ydim; y++) {
/* 39:43 */         this.output.setXYDouble(x, y, getMax(this.input, x, y, this.se[x][y].getCoords()));
/* 40:   */       }
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   private double getMax(Image img, int x, int y, Point[] coords)
/* 45:   */   {
/* 46:48 */     double max = -1.797693134862316E+308D;
/* 47:50 */     for (int i = 0; i < coords.length; i++)
/* 48:   */     {
/* 49:53 */       int _x = x - coords[i].x;
/* 50:54 */       int _y = y - coords[i].y;
/* 51:56 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 52:   */       {
/* 53:59 */         double p = img.getXYDouble(_x, _y);
/* 54:60 */         if (p > max) {
/* 55:60 */           max = p;
/* 56:   */         }
/* 57:   */       }
/* 58:   */     }
/* 59:64 */     return max > -1.797693134862316E+308D ? max : img.getXYDouble(x, y);
/* 60:   */   }
/* 61:   */   
/* 62:   */   public static Image invoke(Image image, FlatSE[][] se)
/* 63:   */   {
/* 64:   */     try
/* 65:   */     {
/* 66:69 */       return (Image)new ADilation().preprocess(new Object[] { image, se });
/* 67:   */     }
/* 68:   */     catch (GlobalException e)
/* 69:   */     {
/* 70:71 */       e.printStackTrace();
/* 71:   */     }
/* 72:72 */     return null;
/* 73:   */   }
/* 74:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.adaptive.ADilation
 * JD-Core Version:    0.7.0.1
 */