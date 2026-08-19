/*  1:   */ package vpt.algorithms.mm.gray.connected;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.Iterator;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*  9:   */ import vpt.algorithms.mm.gray.connected.maxtree.MaxTree;
/* 10:   */ import vpt.algorithms.mm.gray.connected.maxtree.Node;
/* 11:   */ 
/* 12:   */ public class GMaxTreeIncrAttrOpening
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:23 */   public Image input = null;
/* 16:24 */   public Attribute crit = null;
/* 17:25 */   public Image output = null;
/* 18:   */   
/* 19:   */   public GMaxTreeIncrAttrOpening()
/* 20:   */   {
/* 21:28 */     this.inputFields = "input, crit";
/* 22:29 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:34 */     Node root = MaxTree.invoke(this.input);
/* 29:38 */     if (!this.crit.test(root.pixels, this.input))
/* 30:   */     {
/* 31:39 */       this.output = null;
/* 32:40 */       return;
/* 33:   */     }
/* 34:42 */     processNode(root);
/* 35:   */     
/* 36:44 */     this.output = MaxTree.tree2Image(root, this.input.getXDim(), this.input.getYDim());
/* 37:   */   }
/* 38:   */   
/* 39:   */   private void processNode(Node node)
/* 40:   */   {
/* 41:51 */     Iterator<Node> ite = node.children.iterator();
/* 42:54 */     while (ite.hasNext())
/* 43:   */     {
/* 44:56 */       Node n = (Node)ite.next();
/* 45:59 */       if (!this.crit.test(n.pixels, this.input)) {
/* 46:60 */         ite.remove();
/* 47:   */       }
/* 48:   */     }
/* 49:64 */     ite = node.children.iterator();
/* 50:66 */     while (ite.hasNext()) {
/* 51:67 */       processNode((Node)ite.next());
/* 52:   */     }
/* 53:   */   }
/* 54:   */   
/* 55:   */   public static Image invoke(Image input, Attribute c)
/* 56:   */   {
/* 57:   */     try
/* 58:   */     {
/* 59:72 */       return (Image)new GMaxTreeIncrAttrOpening().preprocess(new Object[] { input, c });
/* 60:   */     }
/* 61:   */     catch (GlobalException e)
/* 62:   */     {
/* 63:74 */       e.printStackTrace();
/* 64:   */     }
/* 65:75 */     return null;
/* 66:   */   }
/* 67:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeIncrAttrOpening
 * JD-Core Version:    0.7.0.1
 */