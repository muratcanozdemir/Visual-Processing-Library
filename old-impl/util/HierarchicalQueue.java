/*  1:   */ package vpt.util;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.LinkedList;
/*  5:   */ 
/*  6:   */ public class HierarchicalQueue
/*  7:   */ {
/*  8:   */   private LinkedList<Point>[] queue;
/*  9:   */   
/* 10:   */   public HierarchicalQueue(int size)
/* 11:   */   {
/* 12:11 */     this.queue = new LinkedList[size];
/* 13:13 */     for (int i = 0; i < this.queue.length; i++) {
/* 14:14 */       this.queue[i] = new LinkedList();
/* 15:   */     }
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void add(Point p, int val)
/* 19:   */   {
/* 20:18 */     this.queue[val].add(p);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public Point get(int val)
/* 24:   */   {
/* 25:22 */     return (Point)this.queue[val].remove();
/* 26:   */   }
/* 27:   */   
/* 28:   */   public boolean isEmpty(int val)
/* 29:   */   {
/* 30:26 */     return this.queue[val].isEmpty();
/* 31:   */   }
/* 32:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.HierarchicalQueue
 * JD-Core Version:    0.7.0.1
 */