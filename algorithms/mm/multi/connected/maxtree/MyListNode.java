/*  1:   */ package vpt.algorithms.mm.multi.connected.maxtree;
/*  2:   */ 
/*  3:   */ public class MyListNode<T>
/*  4:   */ {
/*  5:   */   T datum;
/*  6:   */   MyListNode<T> next;
/*  7:   */   MyListNode<T> previous;
/*  8:   */   long serial;
/*  9: 9 */   static long counter = 0L;
/* 10:   */   
/* 11:   */   public MyListNode(T t)
/* 12:   */   {
/* 13:12 */     this.datum = t;
/* 14:13 */     this.next = null;
/* 15:14 */     this.previous = null;
/* 16:15 */     this.serial = (counter++);
/* 17:   */   }
/* 18:   */   
/* 19:   */   public boolean Equals(Object o)
/* 20:   */   {
/* 21:20 */     MyListNode m = (MyListNode)o;
/* 22:21 */     return m.serial == this.serial;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public T getDatum()
/* 26:   */   {
/* 27:25 */     return this.datum;
/* 28:   */   }
/* 29:   */   
/* 30:   */   public MyListNode<T> getNext()
/* 31:   */   {
/* 32:29 */     return this.next;
/* 33:   */   }
/* 34:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.maxtree.MyListNode
 * JD-Core Version:    0.7.0.1
 */