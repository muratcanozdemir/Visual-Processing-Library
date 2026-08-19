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
/*  11:    */ import vpt.algorithms.geometric.KeepLargestCC;
/*  12:    */ import vpt.algorithms.mm.binary.BInternGradient;
/*  13:    */ import vpt.algorithms.texture.HorizontalMorphologicalCovariance;
/*  14:    */ import vpt.util.Distance;
/*  15:    */ import vpt.util.se.FlatSE;
/*  16:    */ 
/*  17:    */ public class BorderCovariance
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20: 29 */   public double[] output = null;
/*  21: 30 */   public Image input = null;
/*  22: 31 */   public Integer length = null;
/*  23:    */   
/*  24:    */   public BorderCovariance()
/*  25:    */   {
/*  26: 34 */     this.inputFields = "input, length";
/*  27: 35 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void execute()
/*  31:    */     throws GlobalException
/*  32:    */   {
/*  33: 39 */     int cdim = this.input.getCDim();
/*  34: 40 */     int ydim = this.input.getYDim();
/*  35: 41 */     int xdim = this.input.getXDim();
/*  36: 43 */     if (cdim != 1) {
/*  37: 43 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  38:    */     }
/*  39: 45 */     this.output = new double[this.length.intValue()];
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43: 49 */     this.input = KeepLargestCC.invoke(this.input);
/*  44:    */     
/*  45:    */ 
/*  46: 52 */     Point centroid = ImageCentroid.invoke(this.input);
/*  47:    */     
/*  48:    */ 
/*  49: 55 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53: 59 */     ArrayList<Double> borders = new ArrayList();
/*  54:    */     
/*  55: 61 */     BooleanImage mask = new BooleanImage(this.input, false);
/*  56: 62 */     mask.fill(false);
/*  57: 64 */     for (int x = 0; x < xdim; x++) {
/*  58: 65 */       for (int y = 0; y < ydim; y++)
/*  59:    */       {
/*  60: 66 */         boolean r = borderImage.getXYBoolean(x, y);
/*  61: 67 */         if (r)
/*  62:    */         {
/*  63: 69 */           boolean m = mask.getXYBoolean(x, y);
/*  64: 70 */           if (!m)
/*  65:    */           {
/*  66: 73 */             Point p = new Point(x, y);
/*  67:    */             for (;;)
/*  68:    */             {
/*  69: 76 */               mask.setXYBoolean(p.x, p.y, true);
/*  70: 77 */               borders.add(Double.valueOf(Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y)));
/*  71: 80 */               if ((p.x + 1 < xdim) && (borderImage.getXYBoolean(p.x + 1, p.y)) && (!mask.getXYBoolean(p.x + 1, p.y)))
/*  72:    */               {
/*  73: 81 */                 p.x += 1;
/*  74:    */               }
/*  75: 82 */               else if ((p.y + 1 < ydim) && (borderImage.getXYBoolean(p.x, p.y + 1)) && (!mask.getXYBoolean(p.x, p.y + 1)))
/*  76:    */               {
/*  77: 83 */                 p.y += 1;
/*  78:    */               }
/*  79: 84 */               else if ((p.x - 1 >= 0) && (borderImage.getXYBoolean(p.x - 1, p.y)) && (!mask.getXYBoolean(p.x - 1, p.y)))
/*  80:    */               {
/*  81: 85 */                 p.x -= 1;
/*  82:    */               }
/*  83:    */               else
/*  84:    */               {
/*  85: 86 */                 if ((p.y - 1 < 0) || (!borderImage.getXYBoolean(p.x, p.y - 1)) || (mask.getXYBoolean(p.x, p.y - 1))) {
/*  86:    */                   break;
/*  87:    */                 }
/*  88: 87 */                 p.y -= 1;
/*  89:    */               }
/*  90:    */             }
/*  91:    */           }
/*  92:    */         }
/*  93:    */       }
/*  94:    */     }
/*  95: 95 */     Image tmp = new DoubleImage(borders.size(), 1, 1);
/*  96: 96 */     for (int i = 0; i < borders.size(); i++) {
/*  97: 97 */       tmp.setDouble(i, ((Double)borders.get(i)).doubleValue());
/*  98:    */     }
/*  99:102 */     this.output = HorizontalMorphologicalCovariance.invoke(tmp, this.length, Integer.valueOf(0));
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static double[] invoke(Image img, Integer length)
/* 103:    */   {
/* 104:    */     try
/* 105:    */     {
/* 106:108 */       return (double[])new BorderCovariance().preprocess(new Object[] { img, length });
/* 107:    */     }
/* 108:    */     catch (GlobalException e)
/* 109:    */     {
/* 110:110 */       e.printStackTrace();
/* 111:    */     }
/* 112:111 */     return null;
/* 113:    */   }
/* 114:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.BorderCovariance
 * JD-Core Version:    0.7.0.1
 */