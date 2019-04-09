/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.conversion.RGB2HSY;
/*  6:   */ import vpt.algorithms.display.Display2D;
/*  7:   */ import vpt.algorithms.flatzones.angular.AngularQFZ5;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ 
/* 10:   */ public class AngularQFZTest2
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:29 */     Image img = Load.invoke("samples/lenna256.png");
/* 15:   */     
/* 16:   */ 
/* 17:   */ 
/* 18:   */ 
/* 19:   */ 
/* 20:   */ 
/* 21:36 */     Display2D.invoke(img, Boolean.valueOf(true));
/* 22:   */     
/* 23:38 */     double alpha = 0.36D;double omega = 0.36D;
/* 24:   */     
/* 25:40 */     Image img2 = RGB2HSY.invoke(img);
/* 26:41 */     Image img3 = img2.newInstance(true);
/* 27:   */     
/* 28:   */ 
/* 29:   */ 
/* 30:   */ 
/* 31:   */ 
/* 32:   */ 
/* 33:   */ 
/* 34:   */ 
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
/* 53:   */ 
/* 54:69 */     long time = System.currentTimeMillis();
/* 55:70 */     Image labels = AngularQFZ5.invoke(img3.getChannel(0), alpha, omega);
/* 56:71 */     System.err.println(System.currentTimeMillis() - time);
/* 57:   */   }
/* 58:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.AngularQFZTest2
 * JD-Core Version:    0.7.0.1
 */