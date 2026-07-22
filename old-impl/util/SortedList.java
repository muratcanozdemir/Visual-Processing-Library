/*  1:   */ package vpt.util;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.Comparator;
/*  5:   */ 
/*  6:   */ public class SortedList
/*  7:   */   extends ArrayList
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = -5444323180059523802L;
/* 10:   */   private Comparator comparator;
/* 11:   */   
/* 12:   */   public SortedList(Comparator c)
/* 13:   */   {
/* 14:28 */     this.comparator = c;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void add(int index, Object element) {}
/* 18:   */   
/* 19:   */   public boolean add(Object o)
/* 20:   */   {
/* 21:50 */     int i = 0;
/* 22:51 */     boolean found = false;
/* 23:52 */     while ((!found) && (i < size()))
/* 24:   */     {
/* 25:53 */       found = this.comparator.compare(o, get(i)) < 0;
/* 26:54 */       if (!found) {
/* 27:54 */         i++;
/* 28:   */       }
/* 29:   */     }
/* 30:56 */     super.add(i, o);
/* 31:57 */     return true;
/* 32:   */   }
/* 33:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.SortedList
 * JD-Core Version:    0.7.0.1
 */