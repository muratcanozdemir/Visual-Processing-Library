/*  1:   */ package vpt.algorithms.mm.multi.connected.attribute;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Image;
/*  5:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  6:   */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  7:   */ 
/*  8:   */ public class MultiSpectralVolume
/*  9:   */   implements MultiAttribute
/* 10:   */ {
/* 11:   */   private int threshold;
/* 12:   */   
/* 13:   */   public MultiSpectralVolume(Integer threshold)
/* 14:   */   {
/* 15:23 */     this.threshold = threshold.intValue();
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean test(MyList<Point> pixels, Image img)
/* 19:   */   {
/* 20:33 */     int volume = 0;
/* 21:35 */     for (MyListNode<Point> n = pixels.getHead(); n != null; n = n.getNext())
/* 22:   */     {
/* 23:36 */       Point p = (Point)n.getDatum();
/* 24:   */       
/* 25:38 */       int[] val = img.getVXYByte(p.x, p.y);
/* 26:40 */       for (int c = 0; c < val.length; c++) {
/* 27:41 */         volume += img.getXYCByte(p.x, p.y, c);
/* 28:   */       }
/* 29:   */     }
/* 30:44 */     if (volume < this.threshold) {
/* 31:44 */       return false;
/* 32:   */     }
/* 33:45 */     return true;
/* 34:   */   }
/* 35:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.attribute.MultiSpectralVolume
 * JD-Core Version:    0.7.0.1
 */