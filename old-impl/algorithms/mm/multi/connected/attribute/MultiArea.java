/*  1:   */ package vpt.algorithms.mm.multi.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  6:   */ 
/*  7:   */ public class MultiArea
/*  8:   */   implements MultiAttribute
/*  9:   */ {
/* 10:   */   private int threshold;
/* 11:   */   
/* 12:   */   public MultiArea(Integer threshold)
/* 13:   */   {
/* 14:22 */     this.threshold = threshold.intValue();
/* 15:   */   }
/* 16:   */   
/* 17:   */   public boolean test(MyList<Point> pixels, Image img)
/* 18:   */   {
/* 19:32 */     if (pixels.size() < this.threshold) {
/* 20:32 */       return false;
/* 21:   */     }
/* 22:33 */     return true;
/* 23:   */   }
/* 24:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiArea
 * JD-Core Version:    0.7.0.1
 */