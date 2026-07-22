/*  1:   */ package vpt.algorithms.mm.gray.connected.maxtree;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ 
/*  6:   */ public class Node
/*  7:   */ {
/*  8:   */   public int level;
/*  9: 9 */   public Node parent = null;
/* 10:10 */   public ArrayList<Node> children = null;
/* 11:11 */   public ArrayList<Point> pixels = null;
/* 12:   */   
/* 13:   */   Node(int val)
/* 14:   */   {
/* 15:14 */     this.level = val;
/* 16:15 */     this.children = new ArrayList();
/* 17:16 */     this.pixels = new ArrayList();
/* 18:   */   }
/* 19:   */   
/* 20:   */   public Node newInstance()
/* 21:   */   {
/* 22:21 */     Node n = new Node(this.level);
/* 23:24 */     for (Node m : this.children) {
/* 24:25 */       n.children.add(m.newInstance());
/* 25:   */     }
/* 26:28 */     for (Point m : this.pixels) {
/* 27:29 */       n.pixels.add(new Point(m.x, m.y));
/* 28:   */     }
/* 29:31 */     return n;
/* 30:   */   }
/* 31:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.maxtree.Node
 * JD-Core Version:    0.7.0.1
 */