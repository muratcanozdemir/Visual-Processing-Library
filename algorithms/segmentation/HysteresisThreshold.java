/*   1:    */ package vpt.algorithms.segmentation;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.util.ArrayDeque;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ 
/*   9:    */ public class HysteresisThreshold
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12: 24 */   public Image output = null;
/*  13: 25 */   public Image input = null;
/*  14: 26 */   public Double lowThreshold = null;
/*  15: 27 */   public Double highThreshold = null;
/*  16:    */   
/*  17:    */   public HysteresisThreshold()
/*  18:    */   {
/*  19: 30 */     this.inputFields = "input,lowThreshold,highThreshold";
/*  20: 31 */     this.outputFields = "output";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void execute()
/*  24:    */     throws GlobalException
/*  25:    */   {
/*  26: 35 */     this.lowThreshold = Double.valueOf(Math.max(0.0D, Math.min(this.lowThreshold.doubleValue(), 1.0D)));
/*  27: 36 */     this.highThreshold = Double.valueOf(Math.max(0.0D, Math.min(this.highThreshold.doubleValue(), 1.0D)));
/*  28: 38 */     if (this.lowThreshold.doubleValue() >= this.highThreshold.doubleValue()) {
/*  29: 39 */       throw new GlobalException("Low threshold must be inferior to high threshold");
/*  30:    */     }
/*  31: 41 */     this.output = this.input.newInstance(true);
/*  32:    */     
/*  33: 43 */     ArrayDeque<Point> chosen = new ArrayDeque(50000);
/*  34: 45 */     for (int c = 0; c < this.output.getCDim(); c++) {
/*  35: 46 */       for (int x = 0; x < this.output.getXDim(); x++) {
/*  36: 47 */         for (int y = 0; y < this.output.getYDim(); y++)
/*  37:    */         {
/*  38: 49 */           double pixel = this.output.getXYCDouble(x, y, c);
/*  39: 51 */           if ((pixel >= this.highThreshold.doubleValue()) && (pixel < 1.0D))
/*  40:    */           {
/*  41: 52 */             chosen.addLast(new Point(x, y));
/*  42: 54 */             while (!chosen.isEmpty())
/*  43:    */             {
/*  44: 55 */               Point p = (Point)chosen.pollFirst();
/*  45: 57 */               if ((p.x >= 0) && (p.y >= 0) && (p.x < this.output.getXDim()) && (p.y < this.output.getYDim()))
/*  46:    */               {
/*  47: 60 */                 double tmp = this.output.getXYCDouble(p.x, p.y, c);
/*  48: 62 */                 if (tmp >= this.lowThreshold.doubleValue()) {
/*  49: 66 */                   if (tmp < 1.0D)
/*  50:    */                   {
/*  51: 67 */                     this.output.setXYCDouble(p.x, p.y, c, 1.0D);
/*  52:    */                     
/*  53: 69 */                     chosen.addLast(new Point(p.x - 1, p.y - 1));
/*  54: 70 */                     chosen.addLast(new Point(p.x - 1, p.y));
/*  55: 71 */                     chosen.addLast(new Point(p.x - 1, p.y + 1));
/*  56: 72 */                     chosen.addLast(new Point(p.x, p.y - 1));
/*  57: 73 */                     chosen.addLast(new Point(p.x, p.y + 1));
/*  58: 74 */                     chosen.addLast(new Point(p.x + 1, p.y - 1));
/*  59: 75 */                     chosen.addLast(new Point(p.x + 1, p.y));
/*  60: 76 */                     chosen.addLast(new Point(p.x + 1, p.y + 1));
/*  61:    */                   }
/*  62:    */                 }
/*  63:    */               }
/*  64:    */             }
/*  65:    */           }
/*  66:    */         }
/*  67:    */       }
/*  68:    */     }
/*  69: 84 */     for (int c = 0; c < this.output.getCDim(); c++) {
/*  70: 85 */       for (int x = 0; x < this.output.getXDim(); x++) {
/*  71: 86 */         for (int y = 0; y < this.output.getYDim(); y++)
/*  72:    */         {
/*  73: 87 */           double pixel = this.output.getXYCDouble(x, y, c);
/*  74: 89 */           if (pixel < 1.0D) {
/*  75: 90 */             this.output.setXYCDouble(x, y, c, 0.0D);
/*  76:    */           }
/*  77:    */         }
/*  78:    */       }
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Image invoke(Image image, Double lowThreshold, Double highThreshold)
/*  83:    */   {
/*  84:    */     try
/*  85:    */     {
/*  86:101 */       return (Image)new HysteresisThreshold().preprocess(new Object[] { image, lowThreshold, highThreshold });
/*  87:    */     }
/*  88:    */     catch (GlobalException e)
/*  89:    */     {
/*  90:103 */       e.printStackTrace();
/*  91:    */     }
/*  92:104 */     return null;
/*  93:    */   }
/*  94:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.HysteresisThreshold
 * JD-Core Version:    0.7.0.1
 */