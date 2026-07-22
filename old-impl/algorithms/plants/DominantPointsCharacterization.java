/*   1:    */ package vpt.algorithms.plants;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import vpt.Algorithm;
/*   8:    */ import vpt.GlobalException;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.conversion.RGB2Gray;
/*  11:    */ import vpt.algorithms.io.Load;
/*  12:    */ import vpt.algorithms.shape.DominantPointsDetection;
/*  13:    */ import vpt.util.Distance;
/*  14:    */ 
/*  15:    */ public class DominantPointsCharacterization
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 55 */   public double[] output = null;
/*  19: 56 */   public Image input = null;
/*  20: 57 */   public Integer bins = null;
/*  21:    */   
/*  22:    */   public DominantPointsCharacterization()
/*  23:    */   {
/*  24: 60 */     this.inputFields = "input, bins";
/*  25: 61 */     this.outputFields = "output";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 65 */     if (this.input.getCDim() != 1) {
/*  32: 65 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/*  33:    */     }
/*  34: 67 */     Point[] domPoints = DominantPointsDetection.invoke(this.input, Double.valueOf(20.0D));
/*  35:    */     
/*  36:    */ 
/*  37: 70 */     ArrayList<Double> distances = new ArrayList();
/*  38:    */     
/*  39: 72 */     double maxDist = 0.0D;
/*  40: 75 */     for (int i = 0; i < domPoints.length; i++) {
/*  41: 76 */       for (int j = 0; j < domPoints.length; j++) {
/*  42: 77 */         if (i != j)
/*  43:    */         {
/*  44: 79 */           dist = Distance.EuclideanDistance(domPoints[i], domPoints[j]);
/*  45: 80 */           distances.add(Double.valueOf(dist));
/*  46: 82 */           if (dist > maxDist) {
/*  47: 82 */             maxDist = dist;
/*  48:    */           }
/*  49:    */         }
/*  50:    */       }
/*  51:    */     }
/*  52: 86 */     this.output = new double[this.bins.intValue()];
/*  53: 89 */     for (double dist = distances.iterator(); dist.hasNext();)
/*  54:    */     {
/*  55: 89 */       double d = ((Double)dist.next()).doubleValue();
/*  56: 90 */       int bin = (int)(d / maxDist * this.bins.intValue());
/*  57: 91 */       if (bin == this.bins.intValue()) {
/*  58: 91 */         bin--;
/*  59:    */       }
/*  60: 92 */       this.output[bin] += 1.0D;
/*  61:    */     }
/*  62: 96 */     for (int i = 0; i < this.output.length; i++) {
/*  63: 97 */       this.output[i] /= distances.size();
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static void main(String[] args)
/*  68:    */   {
/*  69:101 */     Image img = Load.invoke("../clef_2013/train/categorized/scan_indexed_118_classes/Acer_campestre/300/2151.png");
/*  70:    */     
/*  71:    */ 
/*  72:104 */     double[] feature = invoke(RGB2Gray.invoke(img), Integer.valueOf(10));
/*  73:106 */     for (double d : feature) {
/*  74:107 */       System.err.println(d);
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static double[] invoke(Image img, Integer bins)
/*  79:    */   {
/*  80:    */     try
/*  81:    */     {
/*  82:113 */       return (double[])new DominantPointsCharacterization().preprocess(new Object[] { img, bins });
/*  83:    */     }
/*  84:    */     catch (GlobalException e)
/*  85:    */     {
/*  86:115 */       e.printStackTrace();
/*  87:    */     }
/*  88:116 */     return null;
/*  89:    */   }
/*  90:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.plants.DominantPointsCharacterization
 * JD-Core Version:    0.7.0.1
 */