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
/* 12:   */ public class GMaxTreeIncrMultiAttrOpening
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:23 */   public Image input = null;
/* 16:24 */   public Integer channel = null;
/* 17:25 */   public Attribute crit = null;
/* 18:26 */   public Image output = null;
/* 19:   */   
/* 20:   */   public GMaxTreeIncrMultiAttrOpening()
/* 21:   */   {
/* 22:29 */     this.inputFields = "input, channel, crit";
/* 23:30 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:35 */     Node root = MaxTree.invoke(this.input.getChannel(this.channel.intValue()));
/* 30:39 */     if (!this.crit.test(root.pixels, this.input))
/* 31:   */     {
/* 32:40 */       this.output = null;
/* 33:41 */       return;
/* 34:   */     }
/* 35:43 */     processNode(root);
/* 36:   */     
/* 37:45 */     this.output = MaxTree.tree2Image(root, this.input.getXDim(), this.input.getYDim());
/* 38:   */   }
/* 39:   */   
/* 40:   */   private void processNode(Node node)
/* 41:   */   {
/* 42:52 */     Iterator<Node> ite = node.children.iterator();
/* 43:55 */     while (ite.hasNext())
/* 44:   */     {
/* 45:57 */       Node n = (Node)ite.next();
/* 46:60 */       if (!this.crit.test(n.pixels, this.input)) {
/* 47:61 */         ite.remove();
/* 48:   */       }
/* 49:   */     }
/* 50:65 */     ite = node.children.iterator();
/* 51:67 */     while (ite.hasNext()) {
/* 52:68 */       processNode((Node)ite.next());
/* 53:   */     }
/* 54:   */   }
/* 55:   */   
/* 56:   */   public static Image invoke(Image input, Integer channel, Attribute c)
/* 57:   */   {
/* 58:   */     try
/* 59:   */     {
/* 60:73 */       return (Image)new GMaxTreeIncrMultiAttrOpening().preprocess(new Object[] { input, channel, c });
/* 61:   */     }
/* 62:   */     catch (GlobalException e)
/* 63:   */     {
/* 64:75 */       e.printStackTrace();
/* 65:   */     }
/* 66:76 */     return null;
/* 67:   */   }
/* 68:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeIncrMultiAttrOpening
 * JD-Core Version:    0.7.0.1
 */