/*   1:    */ package vpt.algorithms.mm.multi.connected;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.LinkedList;
/*   5:    */ import java.util.Stack;
/*   6:    */ import vpt.Algorithm;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.algorithms.mm.multi.connected.attribute.MultiAttribute;
/*  10:    */ import vpt.algorithms.mm.multi.connected.maxtree.MaxTreeNode;
/*  11:    */ import vpt.algorithms.mm.multi.connected.maxtree.MultivariateMaxTree;
/*  12:    */ import vpt.algorithms.mm.multi.connected.maxtree.MyList;
/*  13:    */ import vpt.algorithms.mm.multi.connected.maxtree.MyListNode;
/*  14:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  15:    */ 
/*  16:    */ public class MMaxTreeNonIncrAttrOpening
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 26 */   public Image input = null;
/*  20: 27 */   public MultiAttribute crit = null;
/*  21: 28 */   public AlgebraicOrdering vo = null;
/*  22: 29 */   public Image output = null;
/*  23: 30 */   public Integer filteringStrategy = null;
/*  24:    */   public static final int DIRECT = 0;
/*  25:    */   public static final int SUBTRACTIVE = 1;
/*  26:    */   public static final int MIN = 2;
/*  27:    */   public static final int MAX = 3;
/*  28:    */   
/*  29:    */   public MMaxTreeNonIncrAttrOpening()
/*  30:    */   {
/*  31: 50 */     this.inputFields = "input, crit, vo, filteringStrategy";
/*  32: 51 */     this.outputFields = "output";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void execute()
/*  36:    */     throws GlobalException
/*  37:    */   {
/*  38: 56 */     MaxTreeNode root = MultivariateMaxTree.invoke(this.input, this.vo);
/*  39: 57 */     if (root == null) {
/*  40: 57 */       throw new GlobalException("Tree is null..WTF");
/*  41:    */     }
/*  42: 60 */     if (this.filteringStrategy == null) {
/*  43: 60 */       this.filteringStrategy = Integer.valueOf(3);
/*  44:    */     }
/*  45: 62 */     if (this.filteringStrategy.intValue() == 0) {
/*  46: 69 */       System.err.println("Not yet supported");
/*  47: 71 */     } else if (this.filteringStrategy.intValue() != 2) {
/*  48: 81 */       if (this.filteringStrategy.intValue() == 3) {
/*  49: 85 */         root = processByMaxRule(root);
/*  50:    */       } else {
/*  51: 88 */         throw new GlobalException("Invalid filtering strategy");
/*  52:    */       }
/*  53:    */     }
/*  54: 91 */     this.output = MultivariateMaxTree.tree2Image(root, this.input.getXDim(), this.input.getYDim(), this.input.getCDim());
/*  55:    */   }
/*  56:    */   
/*  57:    */   private MaxTreeNode processByMaxRule(MaxTreeNode root)
/*  58:    */   {
/*  59: 96 */     LinkedList<MaxTreeNode> queue = new LinkedList();
/*  60: 97 */     Stack<MaxTreeNode> stack = new Stack();
/*  61:    */     
/*  62:    */ 
/*  63:100 */     stack.push(root);
/*  64:102 */     while (!stack.isEmpty())
/*  65:    */     {
/*  66:104 */       MaxTreeNode n = (MaxTreeNode)stack.pop();
/*  67:    */       
/*  68:106 */       queue.add(n);
/*  69:108 */       if (n.children.size() > 0)
/*  70:    */       {
/*  71:110 */         int size = n.children.size();
/*  72:111 */         MyListNode<MaxTreeNode> tmp = n.children.getHead();
/*  73:112 */         for (int i = 0; i < size; i++)
/*  74:    */         {
/*  75:113 */           MaxTreeNode child = (MaxTreeNode)tmp.getDatum();
/*  76:114 */           stack.push(child);
/*  77:115 */           tmp = tmp.getNext();
/*  78:    */         }
/*  79:    */       }
/*  80:    */     }
/*  81:122 */     while (!queue.isEmpty())
/*  82:    */     {
/*  83:124 */       MaxTreeNode n = (MaxTreeNode)queue.removeLast();
/*  84:129 */       if (n.children.size() == 0) {
/*  85:132 */         if (!this.crit.test(n.pixels, this.input))
/*  86:    */         {
/*  87:135 */           if (n.parent == null) {
/*  88:136 */             return null;
/*  89:    */           }
/*  90:139 */           n.parent.pixels.addAll(n.pixels);
/*  91:    */           
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:144 */           n.parent.children.remove(n);
/*  96:    */         }
/*  97:    */       }
/*  98:    */     }
/*  99:149 */     return root;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static Image invoke(Image input, MultiAttribute c, AlgebraicOrdering vo)
/* 103:    */   {
/* 104:    */     try
/* 105:    */     {
/* 106:216 */       return (Image)new MMaxTreeNonIncrAttrOpening().preprocess(new Object[] { input, c, vo, null });
/* 107:    */     }
/* 108:    */     catch (GlobalException e)
/* 109:    */     {
/* 110:218 */       e.printStackTrace();
/* 111:    */     }
/* 112:219 */     return null;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static Image invoke(Image input, MultiAttribute c, AlgebraicOrdering vo, Integer filteringStrategy)
/* 116:    */   {
/* 117:    */     try
/* 118:    */     {
/* 119:225 */       return (Image)new MMaxTreeNonIncrAttrOpening().preprocess(new Object[] { input, c, vo, filteringStrategy });
/* 120:    */     }
/* 121:    */     catch (GlobalException e)
/* 122:    */     {
/* 123:227 */       e.printStackTrace();
/* 124:    */     }
/* 125:228 */     return null;
/* 126:    */   }
/* 127:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.MMaxTreeNonIncrAttrOpening
 * JD-Core Version:    0.7.0.1
 */