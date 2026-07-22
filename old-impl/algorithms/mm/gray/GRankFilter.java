/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.Arrays;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class GRankFilter
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Image output = null;
/* 14:18 */   public Image input = null;
/* 15:19 */   public FlatSE se = null;
/* 16:20 */   public Integer rank = null;
/* 17:   */   private int xdim;
/* 18:   */   private int ydim;
/* 19:   */   private int cdim;
/* 20:   */   
/* 21:   */   public GRankFilter()
/* 22:   */   {
/* 23:27 */     this.inputFields = "input,se,rank";
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
/* 34:36 */     this.cdim = this.output.getCDim();
/* 35:38 */     if ((this.rank.intValue() < 0) || (this.rank.intValue() > this.se.getCoords().length - 1)) {
/* 36:39 */       throw new GlobalException("Invalid rank value");
/* 37:   */     }
/* 38:41 */     if (!this.se.BOX)
/* 39:   */     {
/* 40:42 */       for (int x = 0; x < this.xdim; x++) {
/* 41:43 */         for (int y = 0; y < this.ydim; y++) {
/* 42:44 */           for (int c = 0; c < this.cdim; c++) {
/* 43:45 */             this.output.setXYCDouble(x, y, c, getRank(this.input, x, y, c, this.se.getCoords(), this.rank.intValue()));
/* 44:   */           }
/* 45:   */         }
/* 46:   */       }
/* 47:   */     }
/* 48:   */     else
/* 49:   */     {
/* 50:48 */       FlatSE hline = this.se.getHLine();
/* 51:49 */       FlatSE vline = this.se.getVLine();
/* 52:   */       
/* 53:51 */       int hrank = this.rank.intValue() % this.se.getXDim();
/* 54:52 */       int vrank = this.rank.intValue() / this.se.getXDim();
/* 55:   */       
/* 56:54 */       Image tmp = this.input.newInstance(false);
/* 57:56 */       for (int x = 0; x < this.xdim; x++) {
/* 58:57 */         for (int y = 0; y < this.ydim; y++) {
/* 59:58 */           for (int c = 0; c < this.cdim; c++) {
/* 60:59 */             tmp.setXYCDouble(x, y, c, getRank(this.input, x, y, c, hline.getCoords(), hrank));
/* 61:   */           }
/* 62:   */         }
/* 63:   */       }
/* 64:61 */       for (int x = 0; x < this.xdim; x++) {
/* 65:62 */         for (int y = 0; y < this.ydim; y++) {
/* 66:63 */           for (int c = 0; c < this.cdim; c++) {
/* 67:64 */             this.output.setXYCDouble(x, y, c, getRank(tmp, x, y, c, vline.getCoords(), vrank));
/* 68:   */           }
/* 69:   */         }
/* 70:   */       }
/* 71:   */     }
/* 72:   */   }
/* 73:   */   
/* 74:   */   private double getRank(Image img, int x, int y, int c, Point[] coords, int rank)
/* 75:   */   {
/* 76:69 */     double[] pixels = new double[coords.length];
/* 77:71 */     for (int i = 0; i < coords.length; i++)
/* 78:   */     {
/* 79:73 */       int _x = x - coords[i].x;
/* 80:74 */       int _y = y - coords[i].y;
/* 81:76 */       if (_x < 0) {
/* 82:76 */         _x = 0;
/* 83:   */       }
/* 84:77 */       if (_y < 0) {
/* 85:77 */         _y = 0;
/* 86:   */       }
/* 87:78 */       if (_x >= this.xdim) {
/* 88:78 */         _x = this.xdim - 1;
/* 89:   */       }
/* 90:79 */       if (_y >= this.ydim) {
/* 91:79 */         _y = this.ydim - 1;
/* 92:   */       }
/* 93:81 */       double p = img.getXYCDouble(_x, _y, c);
/* 94:82 */       pixels[i] = p;
/* 95:   */     }
/* 96:85 */     Arrays.sort(pixels);
/* 97:   */     
/* 98:87 */     return pixels[rank];
/* 99:   */   }
/* :0:   */   
/* :1:   */   public static Image invoke(Image image, FlatSE se, Integer rank)
/* :2:   */   {
/* :3:   */     try
/* :4:   */     {
/* :5:92 */       return (Image)new GRankFilter().preprocess(new Object[] { image, se, rank });
/* :6:   */     }
/* :7:   */     catch (GlobalException e)
/* :8:   */     {
/* :9:94 */       e.printStackTrace();
/* ;0:   */     }
/* ;1:95 */     return null;
/* ;2:   */   }
/* ;3:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GRankFilter
 * JD-Core Version:    0.7.0.1
 */