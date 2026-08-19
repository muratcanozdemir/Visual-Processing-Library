/*  1:   */ package vpt.algorithms.mm.multi.connected.maxtree;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ 
/*  5:   */ public class MaxTreeNode
/*  6:   */ {
/*  7:   */   public int level;
/*  8: 8 */   public MaxTreeNode parent = null;
/*  9: 9 */   public MyList<MaxTreeNode> children = null;
/* 10:10 */   public MyList<Point> pixels = null;
/* 11:   */   
/* 12:   */   MaxTreeNode(int val)
/* 13:   */   {
/* 14:13 */     this.level = val;
/* 15:14 */     this.children = new MyList();
/* 16:15 */     this.pixels = new MyList();
/* 17:   */   }
/* 18:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.maxtree.MaxTreeNode
 * JD-Core Version:    0.7.0.1
 */