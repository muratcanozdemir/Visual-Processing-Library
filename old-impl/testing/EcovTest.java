/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.io.Load;
/*  6:   */ import vpt.algorithms.texture.ExtendedMorphologicalCovariance;
/*  7:   */ 
/*  8:   */ public class EcovTest
/*  9:   */ {
/* 10:   */   public static void main(String[] args)
/* 11:   */   {
/* 12:45 */     Image img = Load.invoke(args[0]);
/* 13:   */     
/* 14:47 */     double[] feature = ExtendedMorphologicalCovariance.invoke(img, Integer.valueOf(Integer.parseInt(args[1])));
/* 15:49 */     for (int i = 0; i < feature.length; i++) {
/* 16:50 */       System.err.println(feature[i]);
/* 17:   */     }
/* 18:   */   }
/* 19:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.EcovTest
 * JD-Core Version:    0.7.0.1
 */