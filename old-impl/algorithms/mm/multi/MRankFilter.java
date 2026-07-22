/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.ordering.Ordering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MRankFilter
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Image output = null;
/* 14:18 */   public Image input = null;
/* 15:19 */   public FlatSE se = null;
/* 16:20 */   public Integer rank = null;
/* 17:21 */   public Ordering or = null;
/* 18:   */   private int xdim;
/* 19:   */   private int ydim;
/* 20:25 */   private double[][] pixelArray = null;
/* 21:   */   
/* 22:   */   public MRankFilter()
/* 23:   */   {
/* 24:28 */     this.inputFields = "input,se,or,rank";
/* 25:29 */     this.outputFields = "output";
/* 26:   */   }
/* 27:   */   
/* 28:   */   public void execute()
/* 29:   */     throws GlobalException
/* 30:   */   {
/* 31:33 */     this.output = this.input.newInstance(false);
/* 32:   */     
/* 33:35 */     this.xdim = this.output.getXDim();
/* 34:36 */     this.ydim = this.output.getYDim();
/* 35:38 */     if ((this.rank.intValue() < 0) || (this.rank.intValue() > this.se.getCoords().length - 1)) {
/* 36:39 */       throw new GlobalException("Invalid rank value");
/* 37:   */     }
/* 38:41 */     if (!this.se.BOX)
/* 39:   */     {
/* 40:43 */       this.pixelArray = new double[this.se.getCoords().length][];
/* 41:45 */       for (int x = 0; x < this.xdim; x++) {
/* 42:46 */         for (int y = 0; y < this.ydim; y++) {
/* 43:47 */           this.output.setVXYDouble(x, y, getRank(this.input, x, y, this.se.getCoords(), this.rank.intValue()));
/* 44:   */         }
/* 45:   */       }
/* 46:   */     }
/* 47:   */     else
/* 48:   */     {
/* 49:50 */       FlatSE hline = this.se.getHLine();
/* 50:51 */       FlatSE vline = this.se.getVLine();
/* 51:   */       
/* 52:53 */       int hrank = this.rank.intValue() % this.se.getXDim();
/* 53:54 */       int vrank = this.rank.intValue() / this.se.getXDim();
/* 54:   */       
/* 55:56 */       Image tmp = this.input.newInstance(false);
/* 56:   */       
/* 57:58 */       this.pixelArray = new double[hline.getCoords().length][];
/* 58:60 */       for (int x = 0; x < this.xdim; x++) {
/* 59:61 */         for (int y = 0; y < this.ydim; y++) {
/* 60:62 */           tmp.setVXYDouble(x, y, getRank(this.input, x, y, hline.getCoords(), hrank));
/* 61:   */         }
/* 62:   */       }
/* 63:64 */       this.pixelArray = new double[vline.getCoords().length][];
/* 64:66 */       for (int x = 0; x < this.xdim; x++) {
/* 65:67 */         for (int y = 0; y < this.ydim; y++) {
/* 66:68 */           this.output.setVXYDouble(x, y, getRank(tmp, x, y, vline.getCoords(), vrank));
/* 67:   */         }
/* 68:   */       }
/* 69:   */     }
/* 70:   */   }
/* 71:   */   
/* 72:   */   private double[] getRank(Image img, int x, int y, Point[] coords, int rank)
/* 73:   */   {
/* 74:73 */     int index = 0;
/* 75:75 */     for (int i = 0; i < coords.length; i++)
/* 76:   */     {
/* 77:77 */       int _x = x - coords[i].x;
/* 78:78 */       int _y = y - coords[i].y;
/* 79:80 */       if (_x < 0) {
/* 80:80 */         _x = 0;
/* 81:   */       }
/* 82:81 */       if (_y < 0) {
/* 83:81 */         _y = 0;
/* 84:   */       }
/* 85:82 */       if (_x >= this.xdim) {
/* 86:82 */         _x = this.xdim - 1;
/* 87:   */       }
/* 88:83 */       if (_y >= this.ydim) {
/* 89:83 */         _y = this.ydim - 1;
/* 90:   */       }
/* 91:85 */       this.pixelArray[(index++)] = img.getVXYDouble(_x, _y);
/* 92:   */     }
/* 93:89 */     return index > 0 ? this.or.rank(this.pixelArray, rank) : img.getVXYDouble(x, y);
/* 94:   */   }
/* 95:   */   
/* 96:   */   public static Image invoke(Image image, FlatSE se, Ordering or, Integer rank)
/* 97:   */   {
/* 98:   */     try
/* 99:   */     {
/* :0:94 */       return (Image)new MRankFilter().preprocess(new Object[] { image, se, or, rank });
/* :1:   */     }
/* :2:   */     catch (GlobalException e)
/* :3:   */     {
/* :4:96 */       e.printStackTrace();
/* :5:   */     }
/* :6:97 */     return null;
/* :7:   */   }
/* :8:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MRankFilter
 * JD-Core Version:    0.7.0.1
 */