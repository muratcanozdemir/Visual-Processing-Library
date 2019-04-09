/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.arithmetic.Equal;
/*  6:   */ import vpt.algorithms.display.Display2D;
/*  7:   */ import vpt.algorithms.io.Load;
/*  8:   */ import vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrAttrOpening;
/*  9:   */ import vpt.algorithms.mm.gray.connected.attribute.Area;
/* 10:   */ 
/* 11:   */ public class Test1
/* 12:   */ {
/* 13:   */   public static void main(String[] args)
/* 14:   */   {
/* 15:65 */     Image img = Load.invoke("/data/arge/veri_yedegi/hyperspectral/pavia_U/pavia_normalized_610_340_103/paviaPCA3.png");
/* 16:   */     
/* 17:   */ 
/* 18:   */ 
/* 19:   */ 
/* 20:   */ 
/* 21:   */ 
/* 22:   */ 
/* 23:73 */     Image opened = GMaxTreeNonIncrAttrOpening.invoke(img, new Area(Integer.valueOf(100)), Integer.valueOf(3));
/* 24:74 */     Display2D.invoke(opened, "opened MAX");
/* 25:   */     
/* 26:   */ 
/* 27:   */ 
/* 28:   */ 
/* 29:79 */     Image opened2 = GMaxTreeNonIncrAttrOpening.invoke(img, new Area(Integer.valueOf(100)), Integer.valueOf(2));
/* 30:80 */     Display2D.invoke(opened2, "opened MIN");
/* 31:   */     
/* 32:   */ 
/* 33:   */ 
/* 34:   */ 
/* 35:85 */     System.err.println(Equal.invoke(opened, opened2));
/* 36:   */   }
/* 37:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.Test1
 * JD-Core Version:    0.7.0.1
 */