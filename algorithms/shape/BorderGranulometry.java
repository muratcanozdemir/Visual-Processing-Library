/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.BooleanImage;
/*   7:    */ import vpt.DoubleImage;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.geometric.ImageCentroid;
/*  11:    */ import vpt.algorithms.mm.binary.BInternGradient;
/*  12:    */ import vpt.algorithms.texture.LineGranulometry;
/*  13:    */ import vpt.util.Distance;
/*  14:    */ import vpt.util.se.FlatSE;
/*  15:    */ 
/*  16:    */ public class BorderGranulometry
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 25 */   public double[] output = null;
/*  20: 26 */   public Image input = null;
/*  21: 27 */   public Integer length = null;
/*  22:    */   
/*  23:    */   public BorderGranulometry()
/*  24:    */   {
/*  25: 30 */     this.inputFields = "input, length";
/*  26: 31 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 35 */     int cdim = this.input.getCDim();
/*  33: 36 */     int ydim = this.input.getYDim();
/*  34: 37 */     int xdim = this.input.getXDim();
/*  35: 39 */     if (cdim != 1) {
/*  36: 39 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  37:    */     }
/*  38: 41 */     this.output = new double[this.length.intValue()];
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45: 48 */     Point centroid = ImageCentroid.invoke(this.input);
/*  46:    */     
/*  47:    */ 
/*  48: 51 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/*  49:    */     
/*  50:    */ 
/*  51: 54 */     ArrayList<Double> borders = new ArrayList();
/*  52:    */     
/*  53: 56 */     BooleanImage mask = new BooleanImage(this.input, false);
/*  54: 57 */     mask.fill(false);
/*  55: 59 */     for (int x = 0; x < xdim; x++) {
/*  56: 60 */       for (int y = 0; y < ydim; y++)
/*  57:    */       {
/*  58: 61 */         boolean r = borderImage.getXYBoolean(x, y);
/*  59: 62 */         if (r)
/*  60:    */         {
/*  61: 64 */           boolean m = mask.getXYBoolean(x, y);
/*  62: 65 */           if (!m)
/*  63:    */           {
/*  64: 68 */             Point p = new Point(x, y);
/*  65:    */             for (;;)
/*  66:    */             {
/*  67: 71 */               mask.setXYBoolean(p.x, p.y, true);
/*  68: 72 */               borders.add(Double.valueOf(Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y)));
/*  69: 75 */               if ((p.x + 1 < xdim) && (borderImage.getXYBoolean(p.x + 1, p.y)) && (!mask.getXYBoolean(p.x + 1, p.y)))
/*  70:    */               {
/*  71: 76 */                 p.x += 1;
/*  72:    */               }
/*  73: 77 */               else if ((p.y + 1 < ydim) && (borderImage.getXYBoolean(p.x, p.y + 1)) && (!mask.getXYBoolean(p.x, p.y + 1)))
/*  74:    */               {
/*  75: 78 */                 p.y += 1;
/*  76:    */               }
/*  77: 79 */               else if ((p.x - 1 >= 0) && (borderImage.getXYBoolean(p.x - 1, p.y)) && (!mask.getXYBoolean(p.x - 1, p.y)))
/*  78:    */               {
/*  79: 80 */                 p.x -= 1;
/*  80:    */               }
/*  81:    */               else
/*  82:    */               {
/*  83: 81 */                 if ((p.y - 1 < 0) || (!borderImage.getXYBoolean(p.x, p.y - 1)) || (mask.getXYBoolean(p.x, p.y - 1))) {
/*  84:    */                   break;
/*  85:    */                 }
/*  86: 82 */                 p.y -= 1;
/*  87:    */               }
/*  88:    */             }
/*  89:    */           }
/*  90:    */         }
/*  91:    */       }
/*  92:    */     }
/*  93: 90 */     Image tmp = new DoubleImage(borders.size(), 1, 1);
/*  94: 91 */     for (int i = 0; i < borders.size(); i++) {
/*  95: 92 */       tmp.setDouble(i, ((Double)borders.get(i)).doubleValue());
/*  96:    */     }
/*  97: 95 */     this.output = LineGranulometry.invoke(tmp, this.length, Integer.valueOf(0));
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static double[] invoke(Image img, Integer length)
/* 101:    */   {
/* 102:    */     try
/* 103:    */     {
/* 104:101 */       return (double[])new BorderGranulometry().preprocess(new Object[] { img, length });
/* 105:    */     }
/* 106:    */     catch (GlobalException e)
/* 107:    */     {
/* 108:103 */       e.printStackTrace();
/* 109:    */     }
/* 110:104 */     return null;
/* 111:    */   }
/* 112:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.BorderGranulometry
 * JD-Core Version:    0.7.0.1
 */