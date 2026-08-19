/*  1:   */ package vpt.algorithms.mm.gray.connected.maxtree;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ 
/*  6:   */ public class Node_yedek
/*  7:   */ {
/*  8:   */   public int status;
/*  9:   */   public int level;
/* 10:10 */   public Node_yedek parent = null;
/* 11:11 */   public ArrayList<Node_yedek> children = null;
/* 12:12 */   public ArrayList<Point> pixels = null;
/* 13:   */   
/* 14:   */   Node_yedek(int stat, int val)
/* 15:   */   {
/* 16:15 */     this.status = stat;
/* 17:16 */     this.level = val;
/* 18:17 */     this.children = new ArrayList();
/* 19:18 */     this.pixels = new ArrayList();
/* 20:   */   }
/* 21:   */   
/* 22:   */   public Node_yedek newInstance()
/* 23:   */   {
/* 24:23 */     Node_yedek n = new Node_yedek(this.status, this.level);
/* 25:26 */     for (Node_yedek m : this.children) {
/* 26:27 */       n.children.add(m.newInstance());
/* 27:   */     }
/* 28:30 */     for (Point m : this.pixels) {
/* 29:31 */       n.pixels.add(new Point(m.x, m.y));
/* 30:   */     }
/* 31:33 */     return n;
/* 32:   */   }
/* 33:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.maxtree.Node_yedek
 * JD-Core Version:    0.7.0.1
 */