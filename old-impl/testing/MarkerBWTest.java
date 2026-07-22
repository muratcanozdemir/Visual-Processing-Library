/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.ByteImage;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.arithmetic.AdditionC;
/*  6:   */ import vpt.algorithms.arithmetic.Minimum;
/*  7:   */ import vpt.algorithms.conversion.RGB2Gray;
/*  8:   */ import vpt.algorithms.display.Display2D;
/*  9:   */ import vpt.algorithms.io.Load;
/* 10:   */ import vpt.algorithms.mm.gray.GExternGradient;
/* 11:   */ import vpt.algorithms.segmentation.LabelsToMeanValue;
/* 12:   */ import vpt.algorithms.segmentation.MarkerBasedWatershed;
/* 13:   */ import vpt.util.se.FlatSE;
/* 14:   */ 
/* 15:   */ public class MarkerBWTest
/* 16:   */ {
/* 17:   */   public static void main(String[] args)
/* 18:   */   {
/* 19:29 */     Image img = Load.invoke("samples/plants/leaf_marked2.png");
/* 20:30 */     Image originalColor = Load.invoke("samples/plants/leaf.jpg");
/* 21:   */     
/* 22:32 */     Image original = RGB2Gray.invoke(originalColor);
/* 23:33 */     Display2D.invoke(original);
/* 24:   */     
/* 25:   */ 
/* 26:36 */     Image marker = new ByteImage(original, false);
/* 27:37 */     marker.fill(255);
/* 28:39 */     for (int x = 0; x < img.getXDim(); x++) {
/* 29:40 */       for (int y = 0; y < img.getYDim(); y++)
/* 30:   */       {
/* 31:41 */         int[] p = img.getVXYByte(x, y);
/* 32:43 */         if ((p[0] == 255) && (p[1] == 0) && (p[2] == 0)) {
/* 33:44 */           marker.setXYByte(x, y, 0);
/* 34:   */         }
/* 35:   */       }
/* 36:   */     }
/* 37:49 */     Display2D.invoke(marker);
/* 38:   */     
/* 39:   */ 
/* 40:52 */     Image gradient = GExternGradient.invoke(original, FlatSE.square(3));
/* 41:53 */     Display2D.invoke(gradient);
/* 42:   */     
/* 43:   */ 
/* 44:56 */     gradient = AdditionC.invoke(gradient, Double.valueOf(0.00392156862745098D));
/* 45:   */     
/* 46:   */ 
/* 47:   */ 
/* 48:60 */     gradient = Minimum.invoke(gradient, marker);
/* 49:61 */     Display2D.invoke(gradient);
/* 50:   */     
/* 51:63 */     Image labels = MarkerBasedWatershed.invoke(gradient);
/* 52:64 */     labels = LabelsToMeanValue.invoke(labels, original);
/* 53:65 */     Display2D.invoke(labels);
/* 54:   */   }
/* 55:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.MarkerBWTest
 * JD-Core Version:    0.7.0.1
 */