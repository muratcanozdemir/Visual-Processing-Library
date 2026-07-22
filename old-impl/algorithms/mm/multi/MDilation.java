/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.ordering.Ordering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MDilation
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Image output = null;
/* 14:18 */   public Image input = null;
/* 15:19 */   public FlatSE se = null;
/* 16:20 */   public Ordering or = null;
/* 17:   */   private int xdim;
/* 18:   */   private int ydim;
/* 19:24 */   private double[][] pixelArray = null;
/* 20:   */   
/* 21:   */   public MDilation()
/* 22:   */   {
/* 23:27 */     this.inputFields = "input,se,or";
/* 24:28 */     this.outputFields = "output";
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void execute()
/* 28:   */     throws GlobalException
/* 29:   */   {
/* 30:32 */     this.output = this.input.newInstance(false);
/* 31:   */     
/* 32:34 */     this.xdim = this.output.getXDim();
/* 33:35 */     this.ydim = this.output.getYDim();
/* 34:38 */     if (!this.se.BOX)
/* 35:   */     {
/* 36:39 */       this.pixelArray = new double[this.se.getCoords().length][];
/* 37:41 */       for (int x = 0; x < this.xdim; x++) {
/* 38:42 */         for (int y = 0; y < this.ydim; y++) {
/* 39:43 */           this.output.setVXYDouble(x, y, getMax(this.input, x, y, this.se.getCoords()));
/* 40:   */         }
/* 41:   */       }
/* 42:   */     }
/* 43:   */     else
/* 44:   */     {
/* 45:46 */       FlatSE hline = this.se.getHLine();
/* 46:47 */       FlatSE vline = this.se.getVLine();
/* 47:48 */       Image tmp = this.input.newInstance(false);
/* 48:   */       
/* 49:50 */       this.pixelArray = new double[hline.getCoords().length][];
/* 50:52 */       for (int x = 0; x < this.xdim; x++) {
/* 51:53 */         for (int y = 0; y < this.ydim; y++) {
/* 52:54 */           tmp.setVXYDouble(x, y, getMax(this.input, x, y, hline.getCoords()));
/* 53:   */         }
/* 54:   */       }
/* 55:56 */       this.pixelArray = new double[vline.getCoords().length][];
/* 56:58 */       for (int x = 0; x < this.xdim; x++) {
/* 57:59 */         for (int y = 0; y < this.ydim; y++) {
/* 58:60 */           this.output.setVXYDouble(x, y, getMax(tmp, x, y, vline.getCoords()));
/* 59:   */         }
/* 60:   */       }
/* 61:   */     }
/* 62:   */   }
/* 63:   */   
/* 64:   */   private double[] getMax(Image img, int x, int y, Point[] coords)
/* 65:   */   {
/* 66:66 */     int index = 0;
/* 67:68 */     for (int i = 0; i < coords.length; i++)
/* 68:   */     {
/* 69:71 */       int _x = x - coords[i].x;
/* 70:72 */       int _y = y - coords[i].y;
/* 71:74 */       if (_x < 0) {
/* 72:74 */         _x = 0;
/* 73:   */       }
/* 74:75 */       if (_y < 0) {
/* 75:75 */         _y = 0;
/* 76:   */       }
/* 77:76 */       if (_x >= this.xdim) {
/* 78:76 */         _x = this.xdim - 1;
/* 79:   */       }
/* 80:77 */       if (_y >= this.ydim) {
/* 81:77 */         _y = this.ydim - 1;
/* 82:   */       }
/* 83:79 */       this.pixelArray[(index++)] = img.getVXYDouble(_x, _y);
/* 84:   */     }
/* 85:83 */     return index > 0 ? this.or.max(this.pixelArray) : img.getVXYDouble(x, y);
/* 86:   */   }
/* 87:   */   
/* 88:   */   public static Image invoke(Image image, FlatSE se, Ordering or)
/* 89:   */   {
/* 90:   */     try
/* 91:   */     {
/* 92:89 */       return (Image)new MDilation().preprocess(new Object[] { image, se, or });
/* 93:   */     }
/* 94:   */     catch (GlobalException e)
/* 95:   */     {
/* 96:91 */       e.printStackTrace();
/* 97:   */     }
/* 98:92 */     return null;
/* 99:   */   }
/* :0:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MDilation
 * JD-Core Version:    0.7.0.1
 */