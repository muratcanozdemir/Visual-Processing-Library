/*  1:   */ package vpt.util;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.HashMap;
/*  5:   */ import java.util.LinkedList;
/*  6:   */ 
/*  7:   */ public class HugeHierarchicalQueue
/*  8:   */ {
/*  9:   */   private LinkedList<Point>[] queue;
/* 10:   */   private int top;
/* 11:20 */   HashMap<Integer, Integer> map = null;
/* 12:   */   
/* 13:   */   public HugeHierarchicalQueue(int size)
/* 14:   */   {
/* 15:24 */     this.queue = new LinkedList[size];
/* 16:25 */     this.map = new HashMap(size);
/* 17:26 */     this.top = 0;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void add(Point p, int val)
/* 21:   */   {
/* 22:31 */     Integer listIndex = (Integer)this.map.get(Integer.valueOf(val));
/* 23:33 */     if (listIndex == null)
/* 24:   */     {
/* 25:35 */       this.map.put(Integer.valueOf(val), Integer.valueOf(this.top));
/* 26:   */       
/* 27:37 */       LinkedList<Point> list = new LinkedList();
/* 28:38 */       list.add(p);
/* 29:39 */       this.queue[(this.top++)] = list;
/* 30:   */     }
/* 31:   */     else
/* 32:   */     {
/* 33:42 */       this.queue[listIndex.intValue()].add(p);
/* 34:   */     }
/* 35:   */   }
/* 36:   */   
/* 37:   */   public Point get(int val)
/* 38:   */   {
/* 39:48 */     Integer listIndex = (Integer)this.map.get(Integer.valueOf(val));
/* 40:50 */     if (listIndex == null) {
/* 41:50 */       return null;
/* 42:   */     }
/* 43:51 */     if (this.queue[listIndex.intValue()] == null) {
/* 44:51 */       return null;
/* 45:   */     }
/* 46:53 */     return (Point)this.queue[listIndex.intValue()].remove();
/* 47:   */   }
/* 48:   */   
/* 49:   */   public boolean isEmpty(int val)
/* 50:   */   {
/* 51:58 */     Integer listIndex = (Integer)this.map.get(Integer.valueOf(val));
/* 52:60 */     if (listIndex == null) {
/* 53:60 */       return true;
/* 54:   */     }
/* 55:61 */     return this.queue[listIndex.intValue()].isEmpty();
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.HugeHierarchicalQueue
 * JD-Core Version:    0.7.0.1
 */