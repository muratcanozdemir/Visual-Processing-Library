/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.Image;
/*  4:   */ import vpt.algorithms.conversion.Angular2Color;
/*  5:   */ import vpt.algorithms.display.Display2D;
/*  6:   */ import vpt.algorithms.flatzones.angular.LabelBasedAngularQFZ;
/*  7:   */ import vpt.algorithms.io.LoadFLO;
/*  8:   */ import vpt.algorithms.io.Save;
/*  9:   */ import vpt.algorithms.segmentation.LabelsToMeanHue;
/* 10:   */ 
/* 11:   */ public class OpticalFlowTest
/* 12:   */ {
/* 13:   */   public static void main(String[] args)
/* 14:   */   {
/* 15:48 */     double alpha = 0.01D;double omega = 0.01D;
/* 16:   */     
/* 17:   */ 
/* 18:   */ 
/* 19:   */ 
/* 20:   */ 
/* 21:   */ 
/* 22:   */ 
/* 23:   */ 
/* 24:   */ 
/* 25:   */ 
/* 26:   */ 
/* 27:   */ 
/* 28:   */ 
/* 29:   */ 
/* 30:   */ 
/* 31:64 */     Image img = LoadFLO.invoke("/media/data/arge/veri_yedegi/optical_flow/other-gt-flow/RubberWhale/flow10.flo");
/* 32:65 */     Image color = Angular2Color.invoke(img);
/* 33:66 */     Save.invoke(color, "RubberWhaleColor.png");
/* 34:67 */     Display2D.invoke(color, Boolean.valueOf(true));
/* 35:   */     
/* 36:   */ 
/* 37:   */ 
/* 38:   */ 
/* 39:   */ 
/* 40:   */ 
/* 41:   */ 
/* 42:   */ 
/* 43:   */ 
/* 44:   */ 
/* 45:   */ 
/* 46:   */ 
/* 47:   */ 
/* 48:   */ 
/* 49:   */ 
/* 50:   */ 
/* 51:   */ 
/* 52:   */ 
/* 53:86 */     Image labels = LabelBasedAngularQFZ.invoke(img, (int)(alpha * 2.0D * 255.0D), (int)(omega * 2.0D * 255.0D), 0.0D);
/* 54:87 */     Image mean = LabelsToMeanHue.invoke(labels, img);
/* 55:88 */     mean = Angular2Color.invoke(mean);
/* 56:89 */     Display2D.invoke(mean, Boolean.valueOf(true));
/* 57:90 */     Save.invoke(mean, "RubberWhaleLabelQFZ-001.png");
/* 58:   */   }
/* 59:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.OpticalFlowTest
 * JD-Core Version:    0.7.0.1
 */