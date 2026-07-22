/*  1:   */ package vpt.algorithms.experimental;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Collections;
/*  6:   */ import vpt.Algorithm;
/*  7:   */ import vpt.GlobalException;
/*  8:   */ import vpt.Image;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class Weird
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:17 */   public Image output = null;
/* 15:18 */   public Image input = null;
/* 16:19 */   public FlatSE se = null;
/* 17:   */   private int xdim;
/* 18:   */   private int ydim;
/* 19:   */   private int cdim;
/* 20:   */   
/* 21:   */   public Weird()
/* 22:   */   {
/* 23:26 */     this.inputFields = "input,se";
/* 24:27 */     this.outputFields = "output";
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void execute()
/* 28:   */     throws GlobalException
/* 29:   */   {
/* 30:31 */     this.output = this.input.newInstance(false);
/* 31:   */     
/* 32:33 */     this.xdim = this.output.getXDim();
/* 33:34 */     this.ydim = this.output.getYDim();
/* 34:35 */     this.cdim = this.output.getCDim();
/* 35:37 */     for (int x = 0; x < this.xdim; x++) {
/* 36:38 */       for (int y = 0; y < this.ydim; y++) {
/* 37:39 */         for (int c = 0; c < this.cdim; c++) {
/* 38:40 */           this.output.setXYCDouble(x, y, c, getOutput(this.input, x, y, c, this.se.getCoords()));
/* 39:   */         }
/* 40:   */       }
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   private double getOutput(Image img, int x, int y, int c, Point[] coords)
/* 45:   */   {
/* 46:60 */     ArrayList<Double> content = new ArrayList();
/* 47:   */     
/* 48:62 */     double current = img.getXYCDouble(x, y, c);
/* 49:64 */     for (int i = 0; i < coords.length; i++)
/* 50:   */     {
/* 51:66 */       int _x = x - coords[i].x;
/* 52:67 */       int _y = y - coords[i].y;
/* 53:69 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 54:   */       {
/* 55:72 */         double p = img.getXYCDouble(_x, _y, c);
/* 56:73 */         content.add(Double.valueOf(p));
/* 57:   */       }
/* 58:   */     }
/* 59:78 */     Collections.sort(content);
/* 60:83 */     if (((Double)content.get(content.size() / 2)).doubleValue() > current) {
/* 61:83 */       return ((Double)content.get(0)).doubleValue();
/* 62:   */     }
/* 63:84 */     return current;
/* 64:   */   }
/* 65:   */   
/* 66:   */   public static Image invoke(Image image, FlatSE se)
/* 67:   */   {
/* 68:   */     try
/* 69:   */     {
/* 70:89 */       return (Image)new Weird().preprocess(new Object[] { image, se });
/* 71:   */     }
/* 72:   */     catch (GlobalException e)
/* 73:   */     {
/* 74:91 */       e.printStackTrace();
/* 75:   */     }
/* 76:92 */     return null;
/* 77:   */   }
/* 78:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.experimental.Weird
 * JD-Core Version:    0.7.0.1
 */