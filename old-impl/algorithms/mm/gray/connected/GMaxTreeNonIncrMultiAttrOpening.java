/*   1:    */ package vpt.algorithms.mm.gray.connected;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.mm.gray.connected.attribute.Attribute;
/*   9:    */ import vpt.algorithms.mm.gray.connected.maxtree.MaxTree;
/*  10:    */ import vpt.algorithms.mm.gray.connected.maxtree.Node;
/*  11:    */ 
/*  12:    */ public class GMaxTreeNonIncrMultiAttrOpening
/*  13:    */   extends Algorithm
/*  14:    */ {
/*  15: 23 */   public Image input = null;
/*  16: 24 */   public Integer channel = null;
/*  17: 25 */   public Attribute crit = null;
/*  18: 26 */   public Image output = null;
/*  19: 27 */   public Integer filteringStrategy = null;
/*  20:    */   public static final int MAX = 3;
/*  21:    */   
/*  22:    */   public GMaxTreeNonIncrMultiAttrOpening()
/*  23:    */   {
/*  24: 35 */     this.inputFields = "input, channel, crit, filteringStrategy";
/*  25: 36 */     this.outputFields = "output";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 41 */     Node root = MaxTree.invoke(this.input.getChannel(this.channel.intValue()));
/*  32: 42 */     if (root == null) {
/*  33: 42 */       throw new GlobalException("Tree is null..WTF");
/*  34:    */     }
/*  35: 45 */     if (this.filteringStrategy == null) {
/*  36: 45 */       this.filteringStrategy = Integer.valueOf(3);
/*  37:    */     }
/*  38: 47 */     if (this.filteringStrategy.intValue() == 3) {
/*  39: 51 */       root = processNodeByMaxRule(root, null);
/*  40:    */     } else {
/*  41: 53 */       throw new GlobalException("Invalid filtering strategy");
/*  42:    */     }
/*  43: 56 */     this.output = MaxTree.tree2Image(root, this.input.getXDim(), this.input.getYDim());
/*  44:    */   }
/*  45:    */   
/*  46:    */   private Node processNodeByMaxRule(Node node, Iterator<Node> parentsIterator)
/*  47:    */   {
/*  48: 69 */     if (node.children.size() > 0)
/*  49:    */     {
/*  50: 72 */       Iterator<Node> ite = node.children.iterator();
/*  51: 74 */       while (ite.hasNext()) {
/*  52: 75 */         processNodeByMaxRule((Node)ite.next(), ite);
/*  53:    */       }
/*  54:    */     }
/*  55: 84 */     if (node.children.size() == 0) {
/*  56: 86 */       if (!this.crit.test(node.pixels, this.input)) {
/*  57: 89 */         if (parentsIterator != null) {
/*  58: 89 */           parentsIterator.remove();
/*  59:    */         } else {
/*  60: 90 */           return null;
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64: 94 */     return node;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static Image invoke(Image input, Integer channel, Attribute c)
/*  68:    */   {
/*  69:    */     try
/*  70:    */     {
/*  71: 99 */       return (Image)new GMaxTreeNonIncrMultiAttrOpening().preprocess(new Object[] { input, channel, c, null });
/*  72:    */     }
/*  73:    */     catch (GlobalException e)
/*  74:    */     {
/*  75:101 */       e.printStackTrace();
/*  76:    */     }
/*  77:102 */     return null;
/*  78:    */   }
/*  79:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GMaxTreeNonIncrMultiAttrOpening
 * JD-Core Version:    0.7.0.1
 */