/*  1:   */ package vpt.algorithms.mm.multi.connected.maxtree;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import java.util.Iterator;
/*  5:   */ 
/*  6:   */ public class MyListIterator<T>
/*  7:   */   implements Iterator<T>
/*  8:   */ {
/*  9:15 */   MyList<T> mylist = null;
/* 10:16 */   MyListNode<T> current = null;
/* 11:   */   
/* 12:   */   MyListIterator(MyList<T> myList)
/* 13:   */   {
/* 14:19 */     this.mylist = myList;
/* 15:20 */     this.current = this.mylist.getHead();
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean hasNext()
/* 19:   */   {
/* 20:24 */     return this.current != null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public T next()
/* 24:   */   {
/* 25:28 */     if (hasNext())
/* 26:   */     {
/* 27:29 */       T result = this.current.getDatum();
/* 28:30 */       this.current = this.current.next;
/* 29:31 */       return result;
/* 30:   */     }
/* 31:33 */     return null;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public void remove()
/* 35:   */   {
/* 36:40 */     System.err.println("MyListIterator does not support removal, yet.");
/* 37:   */   }
/* 38:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.maxtree.MyListIterator
 * JD-Core Version:    0.7.0.1
 */