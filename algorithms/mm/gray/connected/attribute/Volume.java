/*  1:   */ package vpt.algorithms.mm.gray.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Volume
/*  8:   */   implements Attribute
/*  9:   */ {
/* 10:   */   private int threshold;
/* 11:   */   
/* 12:   */   public Volume(Integer threshold)
/* 13:   */   {
/* 14:22 */     this.threshold = threshold.intValue();
/* 15:   */   }
/* 16:   */   
/* 17:   */   public boolean test(ArrayList<Point> pixels, Image img)
/* 18:   */   {
/* 19:32 */     int volume = 0;
/* 20:34 */     for (Point p : pixels) {
/* 21:35 */       volume += img.getXYByte(p.x, p.y);
/* 22:   */     }
/* 23:37 */     if (volume < this.threshold) {
/* 24:37 */       return false;
/* 25:   */     }
/* 26:38 */     return true;
/* 27:   */   }
/* 28:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.attribute.Volume
 * JD-Core Version:    0.7.0.1
 */