/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.Image;
/*  4:   */ import vpt.algorithms.conversion.RGB2HSY;
/*  5:   */ import vpt.algorithms.display.Display2D;
/*  6:   */ import vpt.algorithms.io.Load;
/*  7:   */ import vpt.algorithms.io.Save;
/*  8:   */ 
/*  9:   */ public class QFZTest
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:53 */     Image img = Load.invoke("samples/lenna256.png");
/* 14:54 */     Display2D.invoke(RGB2HSY.invoke(img).getChannel(0));
/* 15:   */     
/* 16:   */ 
/* 17:   */ 
/* 18:   */ 
/* 19:59 */     Save.invoke(RGB2HSY.invoke(img).getChannel(0), "lennaHue.png");
/* 20:   */   }
/* 21:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.QFZTest
 * JD-Core Version:    0.7.0.1
 */