/*   1:    */ package vpt.algorithms.mm.multi.connected;
/*   2:    */ 
/*   3:    */ import java.util.LinkedList;
/*   4:    */ import java.util.Stack;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.mm.multi.connected.attribute.MultiAttribute;
/*   9:    */ import vpt.algorithms.mm.multi.connected.maxtree.MaxTreeNode;
/*  10:    */ import vpt.algorithms.mm.multi.connected.maxtree.MultivariateMaxTree;
/*  11:    */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  12:    */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  13:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  14:    */ 
/*  15:    */ public class MMaxTreeIncrAttrOpening
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 26 */   public Image input = null;
/*  19: 27 */   public MultiAttribute crit = null;
/*  20: 28 */   public AlgebraicOrdering vo = null;
/*  21: 29 */   public Image output = null;
/*  22:    */   
/*  23:    */   public MMaxTreeIncrAttrOpening()
/*  24:    */   {
/*  25: 32 */     this.inputFields = "input, crit, vo";
/*  26: 33 */     this.outputFields = "output";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void execute()
/*  30:    */     throws GlobalException
/*  31:    */   {
/*  32: 38 */     MaxTreeNode root = MultivariateMaxTree.invoke(this.input, this.vo);
/*  33: 39 */     root = processNode(root);
/*  34: 40 */     this.output = MultivariateMaxTree.tree2Image(root, this.input.getXDim(), this.input.getYDim(), this.input.getCDim());
/*  35:    */   }
/*  36:    */   
/*  37:    */   private MaxTreeNode processNode(MaxTreeNode root)
/*  38:    */   {
/*  39: 49 */     LinkedList<MaxTreeNode> queue = new LinkedList();
/*  40: 50 */     Stack<MaxTreeNode> stack = new Stack();
/*  41:    */     
/*  42:    */ 
/*  43: 53 */     stack.push(root);
/*  44: 55 */     while (!stack.isEmpty())
/*  45:    */     {
/*  46: 57 */       MaxTreeNode n = (MaxTreeNode)stack.pop();
/*  47:    */       
/*  48: 59 */       queue.add(n);
/*  49: 61 */       if (n.children.size() > 0) {
/*  50: 63 */         for (MyListNode<MaxTreeNode> tmp = n.children.getHead(); tmp != null; tmp = tmp.getNext())
/*  51:    */         {
/*  52: 64 */           MaxTreeNode child = (MaxTreeNode)tmp.getDatum();
/*  53: 65 */           stack.push(child);
/*  54:    */         }
/*  55:    */       }
/*  56:    */     }
/*  57: 72 */     while (!queue.isEmpty())
/*  58:    */     {
/*  59: 74 */       MaxTreeNode n = (MaxTreeNode)queue.removeLast();
/*  60: 79 */       if (n.children.size() == 0) {
/*  61: 82 */         if (!this.crit.test(n.pixels, this.input))
/*  62:    */         {
/*  63: 85 */           if (n.parent == null) {
/*  64: 86 */             return null;
/*  65:    */           }
/*  66: 89 */           n.parent.pixels.addAll(n.pixels);
/*  67:    */           
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71: 94 */           n.parent.children.remove(n);
/*  72:    */         }
/*  73:    */       }
/*  74:    */     }
/*  75: 99 */     return root;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static Image invoke(Image input, MultiAttribute c, AlgebraicOrdering vo)
/*  79:    */   {
/*  80:    */     try
/*  81:    */     {
/*  82:104 */       return (Image)new MMaxTreeIncrAttrOpening().preprocess(new Object[] { input, c, vo });
/*  83:    */     }
/*  84:    */     catch (GlobalException e)
/*  85:    */     {
/*  86:106 */       e.printStackTrace();
/*  87:    */     }
/*  88:107 */     return null;
/*  89:    */   }
/*  90:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.MMaxTreeIncrAttrOpening
 * JD-Core Version:    0.7.0.1
 */